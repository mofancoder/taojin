package com.tj.event.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.tj.dto.*;
import com.tj.event.dao.*;
import com.tj.event.domain.*;
import com.tj.event.service.BetInfoService;
import com.tj.event.service.EventService;
import com.tj.event.subscribe.RedisMsg;
import com.tj.util.A.SacException;
import com.tj.util.AbstractRule;
import com.tj.util.Results;
import com.tj.util.enums.*;
import com.tj.util.redis.CloudRedisService;
import com.tj.util.time.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EventServiceImpl implements EventService, RedisMsg {

    @Value("${dive.unit.time}")
    private int HOUR2MILESECONDS;

//    @Value("${dive.rebate.max_limit}")
//    BigDecimal MAX_REBATE;

    @Resource
    private RaceInfoMapper raceInfoMapper;
    @Resource
    private RaceRebateInfoMapper raceRebateInfoMapper;
    @Resource
    private DiveRuleMapper diveRuleMapper;
    private final CloudRedisService cloudRedisService;
    private final RedisTemplate redisTemplate;
    private final BetInfoService betInfoService;

    private static final BigDecimal MAX_REBATE = new BigDecimal(1);

    private static final BigDecimal MIN_PROCESS = new BigDecimal(0.01);
    @Resource
    private BetRecdMapperEx betRecdMapperEx;
    @Resource
    private RaceRebateInfoMapperEx raceRebateInfoMapperEx;

    private Map<Integer, AbstractRule> ruleMap = new ConcurrentHashMap<>();
    @Resource
    private RaceInfoMapperEx raceInfoMapperEx;
    private ExecutorService executorService = Executors.newFixedThreadPool(10);
    @Autowired
    public EventServiceImpl(CloudRedisService cloudRedisService, RedisTemplate redisTemplate, BetInfoService betInfoService, List<AbstractRule> rules) {
        this.cloudRedisService = cloudRedisService;
        this.redisTemplate = redisTemplate;
        this.betInfoService = betInfoService;
        for (AbstractRule rule : rules) {
            ruleMap.putIfAbsent(rule.type().getCode(), rule);
        }
    }

    @Override
    public Results.Result<AdminEventPage> redisEventList(Long firstTime, Long secondTime, String type, Integer curPage, Integer pageSize) {
        StringBuilder prefix = new StringBuilder("*");
        List<JSONObject> list = new LinkedList<>();
        if (firstTime != null && secondTime != null) {
            String firstDate = new Date(firstTime).toInstant().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String secondDate = new Date(secondTime).toInstant().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            int index = TimeUtil.getDateInterval(firstDate, secondDate);
            for (; index > 0; index-- ) {
                Date date = TimeUtil.plusDay(1, TimeUtil.getDateFormat(firstDate, "yyyy-MM-dd"));
                list.addAll(searchRedisEvent(TimeUtil.ChangeDateFormat(date, "yyyy/MM/dd"), type));
            }
        } else if (firstTime != null){
            String firstDate = new Date(firstTime).toInstant().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            list = searchRedisEvent(firstDate, type);
        } else if (secondTime != null) {
            String firstDate = new Date(secondTime).toInstant().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            list = searchRedisEvent(firstDate, type);
        } else {
            list = searchRedisEvent(null, type);
        }
        if (null == list || list.isEmpty()) {
            return new Results.Result<>(Results.Result.NOT_FIND, "没有数据", null);
        }

        //分页遍历
        int offset = (curPage - 1) * pageSize;
        int endSet = (offset) + pageSize;
        if (endSet >= list.size()) {
            endSet = list.size();
        }
        if (endSet <= 1) {
            endSet = 1;
        }
        if (offset <= 0) {
            offset = 0;
        }

        //遍历和数据库进行比较、 判断是否落地到数据库
        List<AdminEventInfo> collect = list.stream().map(v -> {
            RedisRaceInfo redisRaceInfo = JSON.parseObject(JSON.toJSONString(v), RedisRaceInfo.class);

            RaceInfo raceInfo = raceInfoMapper.selectByPrimaryKey(redisRaceInfo.getId());
            redisRaceInfo.setIsExist(ExistEnum.EXISTED.ordinal());
            if (raceInfo == null) {
                redisRaceInfo.setIsExist(ExistEnum.NOT_EXISTED.ordinal());
            }
            AdminEventInfo eventInfo = AdminEventInfo.builder()
                    .raceId(redisRaceInfo.getId())
                    .category(redisRaceInfo.getCategory())
                    .startTime(redisRaceInfo.getStartTime())
                    .homeTeam(redisRaceInfo.getHomeTeam())
                    .visitTeam(redisRaceInfo.getVisitTeam())
                    .raceResult(redisRaceInfo.getRaceResult())
                    .isExist(redisRaceInfo.getIsExist())
                    .build();
            return eventInfo;
        }).sorted((o1, o2) -> o2.getStartTime().compareTo(o1.getStartTime())).collect(Collectors.toList());
        List<AdminEventInfo> subList = collect.subList(offset, endSet);//分页结果

        AdminEventPage adminEventPage = AdminEventPage.builder()
                .countNum(list.size())
                .build();
        adminEventPage.setEventInfos(subList);
        return new Results.Result(Results.SUCCESS, adminEventPage);
    }

    private List<JSONObject> searchRedisEvent(String timestamp, String type) {
        StringBuilder prefix = new StringBuilder("*");
        if (timestamp != null) {
            prefix = prefix.append(timestamp).append("*");
        }
        if (type != null) {
            prefix = prefix.append(type).append("*");
        }
        Set keys = redisTemplate.keys(RedisKeys.EVENT_RACE + prefix.toString());
        return  redisTemplate.opsForValue().multiGet(keys);
    }

    @Override
    public Results.Result<AdminRebateInfoTotal> rebateEventList(String raceId) {
        if (StringUtils.isEmpty(raceId)) {
            return Results.PARAMETER_INCORRENT;
        }

        Set keys = redisTemplate.keys(RedisKeys.EVENT_RACE + "*" + raceId + "*" );
        System.out.println(keys.toString());

        List<JSONObject> list = redisTemplate.opsForValue().multiGet(keys);
        if (list.size() < 1) {
            throw new SacException("赛事" + raceId + "已经过期");
        }
        RedisRaceInfo redisRace = JSON.parseObject(JSON.toJSONString(list.get(0)), RedisRaceInfo.class);
        List<RedisRaceRebateInfo> redisRebates = redisRace.getRebates();

        List<AdminRebateInfo> rebateInfos = Lists.newLinkedList();
        final BigDecimal[] rebateTotal = {BigDecimal.ZERO, BigDecimal.ZERO};
        redisRebates.forEach(v->{
            String [] teams = v.getTeams().split(":");
            AdminRebateInfo rebateInfo = AdminRebateInfo.builder()
                    .homeTeam(teams[0])
                    .visitTeam(teams[1])
                    .score(v.getScore())
                    .startOdds(v.getStartOdds())
                    .normalOdds(v.getNormalOdds())
                    .normalRebate(v.getNormalRebate())
                    .oppositeOdds(v.getOppositeOdds())
                    .oppositeRebate(v.getOppositeRebate())
                    .build();
            rebateInfos.add(rebateInfo);
            rebateTotal[0] = rebateTotal[0].add(v.getNormalRebate());
            rebateTotal[1] = rebateTotal[1].add(v.getOppositeRebate());

        });
        AdminRebateInfoTotal build = AdminRebateInfoTotal.builder().list(sortAdmin(rebateInfos)).normalRebateTotal(rebateTotal[0]).oppositeRebateTotal(rebateTotal[1]).build();
        return new Results.Result(Results.SUCCESS, build);
    }

    @Transactional
    @Override
    public Results.Result insertDBEvent(String raceId) {
        if (StringUtils.isEmpty(raceId)) {
            return Results.PARAMETER_INCORRENT;
        }
        //查询是否存在
        RaceInfo info = raceInfoMapper.selectByPrimaryKey(raceId);
        if (info != null) {
            throw new SacException("赛事" + info.getId() + "已经存在");
        }
        Set keys = redisTemplate.keys(RedisKeys.EVENT_RACE + "*" + raceId + "*" );

        log.info("event raceInfo insert DB， key:", keys);
        List<JSONObject> list = redisTemplate.opsForValue().multiGet(keys);
        if (list.size() < 1) {
            throw new SacException("赛事" + raceId + "已经过期");
        }
        RedisRaceInfo redisRaceInfo = JSON.parseObject(JSON.toJSONString(list.get(0)), RedisRaceInfo.class);
        RaceInfo raceInfo = new RaceInfo();
        BeanUtils.copyProperties(redisRaceInfo, raceInfo);
        raceInfo.setShelvesStatus(EventShelvesStatusEnum.NotOnShelves.getCode());
        //插入比赛
        raceInfoMapper.insertSelective(raceInfo);
        //插入返利率
        List<RedisRaceRebateInfo> rebates = redisRaceInfo.getRebates();
        if (rebates == null || rebates.isEmpty()) {
            throw new SacException("返利率为空");
        }
        rebates.forEach(v -> {
            RaceRebateInfo rebateInfo = new RaceRebateInfo();
            BeanUtils.copyProperties(v, rebateInfo);
            ScoreRule scoreRule = ScoreRule.builder()
                    .raceId(v.getRaceId())
                    .score(v.getScore())
                    .teams(v.getTeams())
                    .rebateRatio(v.getOppositeRebate().toString())//入库的时候初始返利率=前端返利率
                    .ruleType(BetTypeEnum.score.getCode())
                    .diveType(DiveTypeEnum.closed.getCode())
                    .build();
            rebateInfo.setRule(JSONObject.toJSONString(scoreRule));
            rebateInfo.setRuleType(BetTypeEnum.score.getCode());// 反波胆
            rebateInfo.setCreateTime(new Date());
            raceRebateInfoMapper.insertSelective(rebateInfo);
            v.setStartRebate(v.getOppositeRebate());
            v.setStartOdds(v.getOppositeOdds());
        });
        //更新redis的初始赔率
        keys.forEach(key -> {
            cloudRedisService.updateOutTime(key, redisRaceInfo, LocalDateTime.now().plusDays(30).toEpochSecond(ZoneOffset.of("+8")));
        });
        return new Results.Result<>(Results.SUCCESS, null);
    }

    @Override
    public Results.Result<HistEventInfo> histRaceList(String raceId) {
        Set keys = redisTemplate.keys(RedisKeys.EVENT_RACE + "*" + raceId + "*");
        List<JSONObject> list = redisTemplate.opsForValue().multiGet(keys);
        if (list == null || list.isEmpty()) {
            return Results.PARAMETER_INCORRENT;
        }
        JSONObject jsonObject = list.get(0);
        RedisRaceInfo redisRace = JSON.parseObject(JSON.toJSONString(jsonObject), RedisRaceInfo.class);

        HistEventInfo histEvent = new HistEventInfo();
        Map<String, RedisTeamTrendInfo> teamTrendInfos = new HashMap<>();
        teamTrendInfos.putAll(getRaceTrend(redisRace.getHomeTeam()));
        teamTrendInfos.putAll(getRaceTrend(redisRace.getVisitTeam()));
        histEvent.setTeamTrendInfos(teamTrendInfos);

        histEvent.setSelfHistories(getRaceHistory(redisRace.getHomeTeam() + "|" + redisRace.getVisitTeam()));
        histEvent.setBothHistories(getBothSide(redisRace.getHomeTeam() + "|" + redisRace.getVisitTeam()));
        return new Results.Result(Results.SUCCESS, histEvent);
    }

    @Override
    public Results.Result<PageInfo<RaceInfoDto>> selectDBEvent(Long firstTime, Long secondTime, String category, Integer curPage, Integer pageSize, String homeTeam, String visitTeam,
                                                               Integer openStatus, Integer shelveStatus, Integer commandStatus,Integer resultStatus){
        RaceInfoExample example = new RaceInfoExample();
        RaceInfoExample.Criteria or = example.or();
        if (!StringUtils.isEmpty(category)) {
            or.andCategoryEqualTo(category);
        }
        if (firstTime != null) {
            Date date = new Date(firstTime);
            LocalDateTime from = LocalDateTime.from(date.toInstant().atZone(ZoneId.systemDefault()));
            or.andStartTimeGreaterThanOrEqualTo(date);
        }
        if (secondTime != null) {
            Date date = new Date(secondTime);
            LocalDateTime from = LocalDateTime.from(date.toInstant().atZone(ZoneId.systemDefault()));
            or.andStartTimeLessThanOrEqualTo(date);
        }
        or = (!StringUtils.isEmpty(homeTeam)) ? or.andHomeTeamLike(homeTeam) : or;
        or = (!StringUtils.isEmpty(visitTeam)) ? or.andVisitTeamLike(visitTeam) : or;
        or = (openStatus != null) ? or.andOpenStatusEqualTo(openStatus) : or;
        or = (shelveStatus != null) ? or.andShelvesStatusEqualTo(shelveStatus) : or;
        or = (commandStatus != null) ? or.andIsRecommendEqualTo(commandStatus) : or;
        or = (resultStatus != null) ? or.andRaceStatusEqualTo(resultStatus) : or;
        example.setOrderByClause("start_time desc");
        PageInfo<RaceInfo> pageInfo = PageHelper.startPage(curPage, pageSize).doSelectPageInfo(() -> {
            raceInfoMapper.selectByExample(example);
        });
        if (pageInfo.getList().isEmpty()) {
            return new Results.Result(Results.SUCCESS, new PageInfo<RaceInfoDto>(Lists.newLinkedList()));
        }
        List<RaceInfo> raceInfos = pageInfo.getList();

        List<RaceInfoDto> eventInfos = Lists.newLinkedList();
        raceInfos.forEach(v->{
            RaceInfoDto dto = new RaceInfoDto();
            BeanUtils.copyProperties(v, dto);
            dto.setRaceId(v.getId());
            eventInfos.add(dto);
        });
        PageInfo<RaceInfoDto> results = new PageInfo<>();
        BeanUtils.copyProperties(pageInfo, results, "list");
        results.setList(eventInfos);
        return new Results.Result(Results.SUCCESS, results);
    }


    private Results.Result<Void> updateAllRebateOpenStatus(Integer openStatus, String raceId) {
        RaceInfo raceInfo = raceInfoMapper.selectByPrimaryKey(raceId);
        if (raceInfo == null) {
            return new Results.Result<>(Results.Result.NOT_FIND, "赛事不存在", null);
        }

        RaceRebateInfoExample example = new RaceRebateInfoExample();
        example.or().andRaceIdEqualTo(raceId);
        List<RaceRebateInfo> rebateInfos = raceRebateInfoMapper.selectByExample(example);
        if (rebateInfos == null) {
            return new Results.Result<>(Results.Result.NOT_FIND, "赛事比分信息不存在", null);
        }

        rebateInfos.forEach(vv ->{
            RaceRebateInfo rebateInfo = new RaceRebateInfo();
            rebateInfo.setId(vv.getId());
            rebateInfo.setOpenStatus(openStatus);
            raceRebateInfoMapper.updateByPrimaryKeySelective(rebateInfo);
        });
        RaceInfo race = new RaceInfo();
        race.setId(raceId);
        race.setOpenStatus(openStatus);
        raceInfoMapper.updateByPrimaryKeySelective(race);
        return new Results.Result<>(Results.SUCCESS, null);
    }

    @Override
    @Transactional
    public Results.Result<Void> recommend(String id, Integer shelvesStatus, Integer openStatus, Integer recommend, Double weight) {
        RaceInfo raceInfo = new RaceInfo();
        raceInfo.setId(id);
        raceInfo.setShelvesStatus(shelvesStatus);
        raceInfo.setOpenStatus(openStatus);
        raceInfo.setIsRecommend(recommend);
        raceInfo.setWeight(BigDecimal.valueOf(weight));
        raceInfoMapper.updateByPrimaryKeySelective(raceInfo);
        updateAllRebateOpenStatus(openStatus, id);
        return Results.SUCCESS;
    }

    @Override
    public Results.Result<Void> recommend(String id, Integer commendStatus, BigDecimal weight) {
        RaceInfo raceInfo = new RaceInfo();
        raceInfo.setId(id);
        raceInfo.setIsRecommend(commendStatus);
        raceInfo.setWeight(weight);
        raceInfoMapper.updateByPrimaryKeySelective(raceInfo);

        return Results.SUCCESS;
    }

    @Override
    public Results.Result<Void> shelvesStatus(String id, Integer shelvesStatus) {
        RaceInfo raceInfo = new RaceInfo();
        raceInfo.setId(id);
        raceInfo.setShelvesStatus(shelvesStatus);
        raceInfoMapper.updateByPrimaryKeySelective(raceInfo);
        return Results.SUCCESS;
    }

    @Override
    public Results.Result<Void> openStatus(String id, Integer openStatus) {
        RaceInfo raceInfo = raceInfoMapper.selectByPrimaryKey(id);
        if (raceInfo == null) {
            return new Results.Result<>(Results.Result.NOT_FIND, "赛事不存在", null);
        }

        RaceRebateInfoExample example = new RaceRebateInfoExample();
        example.or().andRaceIdEqualTo(id);
        List<RaceRebateInfo> rebateInfos = raceRebateInfoMapper.selectByExample(example);
        if (rebateInfos == null) {
            return new Results.Result<>(Results.Result.NOT_FIND, "赛事比分信息不存在", null);
        }

        RaceInfo race = new RaceInfo();
        race.setId(id);
        race.setOpenStatus(openStatus);
        raceInfoMapper.updateByPrimaryKeySelective(race);

        if (openStatus == ScoreTypeEnum.open.getCode()) {
            rebateInfos.forEach(v ->{
                ScoreRule rule = (ScoreRule) ruleMap.get(v.getRuleType()).serialize(v.getRule());
                String score = rule.getScore();
                List<String> scores = Splitter.on("-").trimResults().splitToList(score);
                if (scores.size() == 2) {
                    //其他
                    String prefix = scores.get(0);
                    String suffix = scores.get(1);
                    if (Integer.valueOf(prefix).compareTo(3) <= 0 && Integer.valueOf(suffix).compareTo(3) <= 0) {
                        RaceRebateInfo rebateInfo = new RaceRebateInfo();
                        rebateInfo.setId(v.getId());
                        rebateInfo.setOpenStatus(SysStatusEnum.VALID1.ordinal());//3:3之前默认是可以投注的。
                        raceRebateInfoMapper.updateByPrimaryKeySelective(rebateInfo);
                    }
                }
            });
        }
        return Results.SUCCESS;
    }

    @Override
    public Results.Result<PageInfo<RaceDetailDto>> getEventDetail(String raceId, Long startTime, String category, Integer curPage, Integer pageSize, Integer type, Integer sortStyle) {
        RaceInfoExample example = new RaceInfoExample();
        RaceInfoExample.Criteria or = example.or();
        if (!StringUtils.isEmpty(raceId)) {
            or.andIdEqualTo(raceId);
        }
        if (!StringUtils.isEmpty(category)) {
            or.andCategoryEqualTo(category);
        }
        if (startTime != null) {
            Date date = new Date(startTime);
            LocalDateTime from = LocalDateTime.from(date.toInstant().atZone(ZoneId.systemDefault()));
            or.andStartTimeGreaterThanOrEqualTo(date);
        }
//        String time = TimeUtil.addTime(TimeUtil.ChangeDateFormat(new Date(), "yyyy-MM-dd HH:mm:ss"), "15"); //开赛15分钟前
//        or.andStartTimeGreaterThan(TimeUtil.getDateFormat(time, "yyyy-MM-dd HH:mm:ss"));
        or.andStartTimeGreaterThan(new Date());
        or.andEndTimeIsNull();// 只返回未结束的赛事信息
        or.andRaceStatusNotEqualTo(RaceStatusEnum.end.getCode());
        or.andOpenStatusEqualTo(SysStatusEnum.VALID1.ordinal());
        or.andShelvesStatusEqualTo(SysStatusEnum.VALID1.ordinal());
        example.setOrderByClause("start_time desc, race_status desc ");
        PageInfo<RaceInfo> pageInfo = PageHelper.startPage(curPage, pageSize).doSelectPageInfo(() -> {
            raceInfoMapper.selectByExample(example);
        });
        if (pageInfo.getList().isEmpty()) {
            return new Results.Result(Results.SUCCESS, pageInfo.getList());
        }
        List<RaceInfo> raceInfos = pageInfo.getList();

        List<RaceDetailDto> raceDetails = Lists.newLinkedList();
        raceInfos.forEach(v -> {
            DBRebateTotal rebateTotal = (sortStyle == SortStyleEnum.flat.getCode()) ? selectValidRebate(v.getId(), type).getResult() : selectRebateByComplexSort(v.getId(), type).getResult();

            RaceDetailDto dto = new RaceDetailDto();
            BeanUtils.copyProperties(v, dto);
            dto.setRaceId(v.getId());

            dto.setDbRebateTotal(rebateTotal);
            raceDetails.add(dto);
        });
        PageInfo<RaceDetailDto> results = new PageInfo<>();
        BeanUtils.copyProperties(pageInfo, results, "list");
        results.setList(raceDetails);

        return new Results.Result(Results.SUCCESS, results);
    }

    @Override
    public Results.Result<PageInfo<RaceInfoDto>> getEventResult(Long startTime, String category, Integer curPage, Integer pageSize) {
        RaceInfoExample example = new RaceInfoExample();
        RaceInfoExample.Criteria or = example.or();
        if (!StringUtils.isEmpty(category)) {
            or.andCategoryEqualTo(category);
        }
        if (startTime != null) {
            Date date = new Date(startTime);
            or.andStartTimeGreaterThan(date);
            or.andStartTimeLessThan(TimeUtil.plusDay(1, date));
        }
        example.setOrderByClause("create_time ");
        PageInfo<RaceInfo> pageInfo = PageHelper.startPage(curPage, pageSize).doSelectPageInfo(() -> {
            raceInfoMapper.selectByExample(example);
        });
        or.andEndTimeIsNotNull();// 只返回结束的赛事信息
        or.andRaceStatusEqualTo(RaceStatusEnum.end.getCode());
        if (pageInfo.getList().isEmpty()) {
            return new Results.Result(Results.SUCCESS, pageInfo.getList());
        }
        List<RaceInfo> raceInfos = pageInfo.getList();

        List<RaceInfoDto> eventInfos = Lists.newLinkedList();
        raceInfos.forEach(v -> {
            RaceInfoDto dto = new RaceInfoDto();
            BeanUtils.copyProperties(v, dto);
            dto.setRaceId(v.getId());
            eventInfos.add(dto);
        });
        PageInfo<RaceInfoDto> results = new PageInfo<>();
        BeanUtils.copyProperties(pageInfo, results, "list");
        results.setList(eventInfos);
        return new Results.Result(Results.SUCCESS, results);
    }

    @Override
    @Transactional
    public Results.Result updateEventRebate(Integer rebateId, BigDecimal increaseRebate, BigDecimal validAmount) {
        // 1.查数据库
        RaceRebateInfo raceRebate = raceRebateInfoMapper.selectByPrimaryKey(rebateId);
        // 2.判断开启关闭状态
        if (ScoreTypeEnum.closed.getCode().equals(raceRebate.getOpenStatus())) {
            return Results.CLOSED;
        }
        // 3.更新
        ScoreRule rule = (ScoreRule) ruleMap.get(raceRebate.getRuleType()).serialize(raceRebate.getRule());
        BigDecimal rebateRadio = BigDecimal.valueOf(Double.parseDouble(rule.getRebateRatio()));
        ScoreRule scoreRule = ScoreRule.builder()
                .raceId(rule.getRaceId())
                .score(rule.getScore())
                .teams(rule.getTeams())
                .rebateRatio(rebateRadio.add(increaseRebate).toString())
                .ruleType(rule.getRuleType())
                .diveType(rule.getDiveType())
                .build();
        RaceRebateInfo rebateInfo = new RaceRebateInfo();
        rebateInfo.setId(rebateId);
        rebateInfo.setRule(JSON.toJSONString(scoreRule));
        rebateInfo.setValidAmount(validAmount);
        raceRebateInfoMapper.updateByPrimaryKeySelective(rebateInfo);
        return Results.SUCCESS;
    }

    // 设置是否可投注
    @Override
    public Results.Result<Map<Integer, RebateInfoDto>> updateRebateOpenStatus(Integer openStatus, Integer rebateId, BigDecimal base) {
        RaceRebateInfo raceRebateInfo = raceRebateInfoMapper.selectByPrimaryKey(rebateId);
        if (raceRebateInfo == null) {
            return new Results.Result<>(Results.Result.NOT_FIND, "返利率不存在", null);
        }
        Map<Integer, RebateInfoDto> map = reCalculateRebate(openStatus, raceRebateInfo, base);//重新计算的利率
        return new Results.Result<>(Results.SUCCESS, map);
    }

    @Override
    public Results.Result<DBRebateTotal> selectEventRebate(String raceId, Integer type) {
        RaceInfo raceInfo = raceInfoMapper.selectByPrimaryKey(raceId);
        if (raceInfo == null) {
            return new Results.Result<>(Results.Result.NOT_FIND, "赛事不存在", null);
        }
        //查询赛事规则、赔率、返利率
        RaceRebateInfoExample rebateInfoExample = new RaceRebateInfoExample();
        rebateInfoExample.or().andRaceIdEqualTo(raceId);
        List<RaceRebateInfo> rebateInfos = raceRebateInfoMapper.selectByExample(rebateInfoExample);
        if (rebateInfos.isEmpty()) {
            return new Results.Result<>(Results.Result.NOT_FIND, "暂无操盘信息", null);
        }
        if (type.equals(BetTypeEnum.half_bet.getCode())) {
            return new Results.Result<>(Results.Result.NOT_FIND, "暂无半场波胆操盘信息", null);
        }
        List<RedisRaceRebateInfo> rebateInfoList = new ArrayList<>();
        //[波胆返利率,反波胆返利率,初始返利率,平均返利率,前端返利率]
        BigDecimal[] totalRebate = {BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO};

        rebateInfos.forEach(v -> {
            RedisRaceRebateInfo rebateInfo = new RedisRaceRebateInfo();
            ScoreRule rule = (ScoreRule) ruleMap.get(v.getRuleType()).serialize(v.getRule());
            ScoreRule scoreRule = ScoreRule.builder()
                    .raceId(rule.getRaceId())
                    .score(rule.getScore())
                    .teams(rule.getTeams())
                    .rebateRatio(rule.getRebateRatio())
                    .ruleType(v.getRuleType())
                    .diveType(rule.getDiveType())
                    .build();
            rebateInfo.setTeams(scoreRule.getTeams());
            rebateInfo.setRaceId(raceInfo.getId());
            rebateInfo.setCreateTime(raceInfo.getCreateTime());
            rebateInfo.setScore(scoreRule.getScore());
            rebateInfo.setOpenStatus(v.getOpenStatus());
            rebateInfo.setId(v.getId());
            rebateInfo.setRuleType(v.getRuleType());
            rebateInfo.setUpdateTime(v.getUpdateTime());
            rebateInfo.setRebateRatio(scoreRule.getRebateRatio());//前端返利率
            rebateInfo.setRule(JSONObject.toJSONString(scoreRule));
            rebateInfo.setValidAmount(v.getValidAmount());
            rebateInfo.setUsedAmount(BigDecimal.ZERO);
            rebateInfo.setSumAmount(v.getValidAmount().add(rebateInfo.getUsedAmount()));
            String keyPatten = RedisKeys.EVENT_RACE + "*" + raceInfo.getId() + "*";
            Set keys = redisTemplate.keys(keyPatten);
            List<JSONObject> list = redisTemplate.opsForValue().multiGet(keys);
            if (list != null && !list.isEmpty()) {
                JSONObject jsonObject = list.get(0);
                RedisRaceInfo redisRaceInfo = JSON.parseObject(jsonObject.toJSONString(), RedisRaceInfo.class);
                List<RedisRaceRebateInfo> rebates = redisRaceInfo.getRebates();
                rebates.forEach(vv -> {
                    if (vv.getScore().startsWith(scoreRule.getScore())) {
                        rebateInfo.setStartOdds(vv.getStartOdds());
                        rebateInfo.setNormalOdds(vv.getNormalOdds());
                        rebateInfo.setNormalRebate(vv.getNormalRebate());
                        rebateInfo.setOppositeOdds(vv.getOppositeOdds());
                        rebateInfo.setOppositeRebate(vv.getOppositeRebate());//redis 中存的返利率
                        rebateInfo.setStartRebate(vv.getStartRebate());
                        if (v.getOpenStatus() == ScoreTypeEnum.open.getCode()) {
                            totalRebate[0] = totalRebate[0].add(vv.getNormalRebate() == null ? BigDecimal.ZERO : vv.getNormalRebate());
                        }
                        totalRebate[2] = totalRebate[2].add(vv.getStartRebate() == null ? BigDecimal.ZERO : vv.getStartRebate());
                        totalRebate[4] = totalRebate[4].add(new BigDecimal(scoreRule.getRebateRatio() == null ? "0" : scoreRule.getRebateRatio()));
                    }
                });
            }
            List<RebateBetInfo> rebateBetInfos = rebateBetInfo(new ArrayList<Integer>() {{
                add(v.getId());
            }});
            if (!rebateBetInfos.isEmpty()) {
                RebateBetInfo betInfo = rebateBetInfos.get(0);
                rebateInfo.setAvgRebate(betInfo.getAvgRebateRatio());
                rebateInfo.setUsedAmount(betInfo.getBetAmount());
                rebateInfo.setSumAmount(betInfo.getBetAmount() == null ? v.getValidAmount() : betInfo.getBetAmount().add(v.getValidAmount()));
                BigDecimal balance = betRecdMapperEx.getBalance(raceId, betInfo.getRebateId());
                if (balance != null) {
                    rebateInfo.setBalance(betInfo.getBetAmount().subtract(balance).setScale(2, BigDecimal.ROUND_HALF_UP));
                }
                totalRebate[3] = totalRebate[3].add(betInfo.getAvgRebateRatio());
            }
            rebateInfoList.add(rebateInfo);
        });
        totalRebate[1] = BigDecimal.ONE.divide(totalRebate[0], 4, BigDecimal.ROUND_HALF_UP);
        rebateInfoList.forEach(v -> {
            ScoreRule rule = (ScoreRule) ruleMap.get(v.getRuleType()).serialize(v.getRule());
            ScoreRule scoreRule = ScoreRule.builder()
                    .raceId(rule.getRaceId())
                    .score(rule.getScore())
                    .teams(rule.getTeams())
                    .rebateRatio(rule.getRebateRatio())
                    .ruleType(v.getRuleType())
                    .diveType(rule.getDiveType())
                    .build();
            if (v.getScore().startsWith(scoreRule.getScore()) && v.getOpenStatus() == ScoreTypeEnum.open.getCode()) {
                //重新算反波胆利率和赔率
                v.setOppositeRebate(v.getNormalRebate().multiply(totalRebate[1]).setScale(4, BigDecimal.ROUND_HALF_UP));//反波胆返利率
                v.setOppositeOdds(BigDecimal.ONE.divide(v.getNormalRebate().multiply(totalRebate[1]).multiply(totalRebate[1]).setScale(4, BigDecimal.ROUND_HALF_UP),4, BigDecimal.ROUND_HALF_UP));//反波胆赔率
            }
        });
        DBRebateTotal build = DBRebateTotal.builder().
                normalRebateTotal(totalRebate[0])
                .oppositeRebateTotal(totalRebate[1])
                .initOppositeRebateTotal(totalRebate[2])
                .avgRebateTotal(totalRebate[3])
                .frontRebateTotal(totalRebate[4])
                .list(sort(rebateInfoList))
                .build();
        return new Results.Result<>(Results.SUCCESS, build);
    }

    private Results.Result<DBRebateTotal> selectValidRebate(String raceId, Integer type) {
        RaceInfo raceInfo = raceInfoMapper.selectByPrimaryKey(raceId);
        if (raceInfo == null) {
            return new Results.Result<>(Results.Result.NOT_FIND, "赛事不存在", null);
        }
        //查询赛事规则、赔率、返利率
        RaceRebateInfoExample rebateInfoExample = new RaceRebateInfoExample();
        rebateInfoExample.or().andRaceIdEqualTo(raceId);
        List<RaceRebateInfo> rebateInfos = raceRebateInfoMapper.selectByExample(rebateInfoExample);
        if (rebateInfos.isEmpty()) {
            return new Results.Result<>(Results.Result.NOT_FIND, "暂无操盘信息", null);
        }
        if (type.equals(BetTypeEnum.half_bet.getCode())) {
            return new Results.Result<>(Results.Result.NOT_FIND, "暂无半场波胆操盘信息", null);
        }
        List<RedisRaceRebateInfo> rebateInfoList = new ArrayList<>();
        //[波胆返利率,反波胆返利率,初始返利率,平均返利率,前端返利率]
        BigDecimal[] totalRebate = {BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO};

        rebateInfos.forEach(v -> {
            RedisRaceRebateInfo rebateInfo = new RedisRaceRebateInfo();
            ScoreRule rule = (ScoreRule) ruleMap.get(v.getRuleType()).serialize(v.getRule());
            ScoreRule scoreRule = ScoreRule.builder()
                    .raceId(rule.getRaceId())
                    .score(rule.getScore())
                    .teams(rule.getTeams())
                    .rebateRatio(rule.getRebateRatio())
                    .ruleType(v.getRuleType())
                    .diveType(rule.getDiveType())
                    .build();
            rebateInfo.setTeams(scoreRule.getTeams());
            rebateInfo.setRaceId(raceInfo.getId());
            rebateInfo.setCreateTime(raceInfo.getCreateTime());
            rebateInfo.setScore(scoreRule.getScore());
            rebateInfo.setOpenStatus(v.getOpenStatus());
            rebateInfo.setId(v.getId());
            rebateInfo.setRuleType(v.getRuleType());
            rebateInfo.setUpdateTime(v.getUpdateTime());
            rebateInfo.setRebateRatio(scoreRule.getRebateRatio());//前端返利率
            rebateInfo.setRule(JSONObject.toJSONString(scoreRule));
            rebateInfo.setValidAmount(v.getValidAmount());
            rebateInfo.setUsedAmount(BigDecimal.ZERO);
            rebateInfo.setSumAmount(v.getValidAmount().add(rebateInfo.getUsedAmount()));
            String keyPatten = RedisKeys.EVENT_RACE + "*" + raceInfo.getId() + "*";
            Set keys = redisTemplate.keys(keyPatten);
            List<JSONObject> list = redisTemplate.opsForValue().multiGet(keys);
            if (list != null && !list.isEmpty()) {
                JSONObject jsonObject = list.get(0);
                RedisRaceInfo redisRaceInfo = JSON.parseObject(jsonObject.toJSONString(), RedisRaceInfo.class);
                List<RedisRaceRebateInfo> rebates = redisRaceInfo.getRebates();
                rebates.forEach(vv -> {
                    if (vv.getScore().startsWith(scoreRule.getScore())) {
                        rebateInfo.setStartOdds(vv.getStartOdds());
                        rebateInfo.setNormalOdds(vv.getNormalOdds());
                        rebateInfo.setNormalRebate(vv.getNormalRebate());
                        rebateInfo.setOppositeOdds(vv.getOppositeOdds());
                        rebateInfo.setOppositeRebate(vv.getOppositeRebate());//redis 中存的返利率
                        rebateInfo.setStartRebate(vv.getStartRebate());
                        if (v.getOpenStatus() == ScoreTypeEnum.open.getCode()) {
                            totalRebate[0] = totalRebate[0].add(vv.getNormalRebate() == null ? BigDecimal.ZERO : vv.getNormalRebate());
                        }
                        totalRebate[2] = totalRebate[2].add(vv.getStartRebate() == null ? BigDecimal.ZERO : vv.getStartRebate());
                        totalRebate[4] = totalRebate[4].add(new BigDecimal(scoreRule.getRebateRatio() == null ? "0" : scoreRule.getRebateRatio()));
                    }
                });
            }
            List<RebateBetInfo> rebateBetInfos = rebateBetInfo(new ArrayList<Integer>() {{
                add(v.getId());
            }});
            if (!rebateBetInfos.isEmpty()) {
                RebateBetInfo betInfo = rebateBetInfos.get(0);
                rebateInfo.setAvgRebate(betInfo.getAvgRebateRatio());
                rebateInfo.setUsedAmount(betInfo.getBetAmount());
                rebateInfo.setSumAmount(betInfo.getBetAmount() == null ? v.getValidAmount() : betInfo.getBetAmount().add(v.getValidAmount()));
                BigDecimal balance = betRecdMapperEx.getBalance(raceId, betInfo.getRebateId());
                if (balance != null) {
                    rebateInfo.setBalance(betInfo.getBetAmount().subtract(balance).setScale(2, BigDecimal.ROUND_HALF_UP));
                }
                totalRebate[3] = totalRebate[3].add(betInfo.getAvgRebateRatio());
            }
            if (ScoreTypeEnum.closed.getCode().equals(rebateInfo.getOpenStatus())) {
                rebateInfo.setNormalOdds(BigDecimal.ZERO);
                rebateInfo.setNormalRebate(BigDecimal.ZERO);
                rebateInfo.setOppositeOdds(BigDecimal.ZERO);
                rebateInfo.setOppositeRebate(BigDecimal.ZERO);
                rebateInfo.setUsedAmount(BigDecimal.ZERO);
                rebateInfo.setValidAmount(BigDecimal.ZERO);
            }
            rebateInfoList.add(rebateInfo);
        });
        totalRebate[1] = BigDecimal.ONE.divide(totalRebate[0], 4, BigDecimal.ROUND_HALF_UP);
        rebateInfoList.forEach(v -> {
            ScoreRule rule = (ScoreRule) ruleMap.get(v.getRuleType()).serialize(v.getRule());
            ScoreRule scoreRule = ScoreRule.builder()
                    .raceId(rule.getRaceId())
                    .score(rule.getScore())
                    .teams(rule.getTeams())
                    .rebateRatio(rule.getRebateRatio())
                    .ruleType(v.getRuleType())
                    .diveType(rule.getDiveType())
                    .build();
            if (v.getScore().startsWith(scoreRule.getScore()) && v.getOpenStatus() == ScoreTypeEnum.open.getCode()) {
                //重新算反波胆利率和赔率
                v.setOppositeRebate(v.getNormalRebate().multiply(totalRebate[1]).setScale(4, BigDecimal.ROUND_HALF_UP));//反波胆返利率
                v.setOppositeOdds(BigDecimal.ONE.divide(v.getNormalRebate().multiply(totalRebate[1]).multiply(totalRebate[1]).setScale(4, BigDecimal.ROUND_HALF_UP),4, BigDecimal.ROUND_HALF_UP));//反波胆赔率
            }
        });
        DBRebateTotal build = DBRebateTotal.builder().
                normalRebateTotal(totalRebate[0])
                .oppositeRebateTotal(totalRebate[1])
                .initOppositeRebateTotal(totalRebate[2])
                .avgRebateTotal(totalRebate[3])
                .frontRebateTotal(totalRebate[4])
                .list(sort(rebateInfoList))
                .build();
        return new Results.Result<>(Results.SUCCESS, build);
    }

    private Results.Result<DBRebateTotal> selectRebateByComplexSort(String raceId, Integer type) {
        DBRebateTotal dbRebateTotal = selectValidRebate(raceId, type).getResult();
        List<RedisRaceRebateInfo> rebateInfoList = dbRebateTotal.getList();
        dbRebateTotal.setList(complexSort(rebateInfoList));
        return new Results.Result<>(Results.SUCCESS, dbRebateTotal);
    }

    @Override
    public Results.Result<List<DiveRuleDto>> selectDiveRule() {
        DiveRuleExample example = new DiveRuleExample();
        example.or().andSysStatusEqualTo(SysStatusEnum.VALID1.ordinal());
        List<DiveRule> rules = diveRuleMapper.selectByExample(example);
        if (rules.isEmpty()) {
            return Results.SUCCESS;
        }
        List<DiveRuleDto> collect = rules.stream().map(v -> DiveRuleDto.builder()
                .autochangeTime(v.getAutochangeTime())
                .timeRange(v.getTimeRange())
                .createTime(v.getCreateTime())
                .enableStatus(v.getEnableStatus())
                .endRebate(v.getEndRebate())
                .shutDownRebate(v.getShutDownRebate())
                .id(v.getId())
                .increase(v.getIncrease())
                .ruleType(v.getRuleType())
                .startAmount(v.getStartAmount())
                .startRebate(v.getStartRebate())
                .build()).collect(Collectors.toList());
        return new Results.Result<>(Results.SUCCESS, collect);
    }

    @Transactional
    @Override
    public Results.Result<DiveRule> updateDiveRule(Integer id, Integer enableStatus) {
        DiveRule diveRule = new DiveRule();
        diveRule.setId(id);
        diveRule.setEnableStatus(enableStatus);
        diveRuleMapper.updateByPrimaryKeySelective(diveRule);
        return Results.SUCCESS;
    }

    @Transactional
    @Override
    public Results.Result updateDiveTimeRange(Integer id, Integer timeRange) {
        DiveRuleExample diveRuleExample = new DiveRuleExample();
        diveRuleExample.or().andEnableStatusEqualTo(SysStatusEnum.VALID1.ordinal()).andSysStatusEqualTo(SysStatusEnum.VALID1.ordinal()).andTimeRangeEqualTo(timeRange);
        List<DiveRule> rules = diveRuleMapper.selectByExample(diveRuleExample);

        DiveRule selectRule = diveRuleMapper.selectByPrimaryKey(id);
        if (selectRule == null) {
            return Results.PARAMETER_INCORRENT;
        }
        Double startRebate = selectRule.getStartRebate().doubleValue();
        Double endRebate = selectRule.getEndRebate().doubleValue();
        if (!rules.isEmpty()) {
            for (DiveRule rule : rules) {
                if (checkRule(startRebate, endRebate, rule)) {
                    return new Results.Result<>(Results.PARAMETER_INCORRENT, "规则利率在已有规则区间内（" + startRebate + "," + endRebate + "),请校验");
                }
            }
        }

        DiveRule diveRule = new DiveRule();
        diveRule.setId(id);
        diveRule.setTimeRange(timeRange);
        diveRuleMapper.updateByPrimaryKeySelective(diveRule);
        return Results.SUCCESS;
    }

    private boolean checkRule(Double startRebate, Double endRebate, DiveRule rule) {
        Double vStartRebate = rule.getStartRebate().doubleValue();
        Double vEndRebate = rule.getEndRebate().doubleValue();
        if (startRebate > vStartRebate) { //起始利率大于已有规则起始利率，则需要 起始利率、结束利率都大于 已有规则结束利率
            if (!(startRebate > vEndRebate && endRebate > vEndRebate)) {
                return true;
            }
        }
        if (endRebate < vEndRebate) { //结束利率小于已有规则结束利率，则需要 起始利率、结束利率都小于 已有规则起始利率
            if (!(startRebate < vStartRebate && endRebate < vStartRebate)) {
                return true;
            }
        }
        return false;
    }

    @Override
    @Transactional
    public Results.Result flushRaceInfoList(List<RedisRaceInfo> redisRaceInfoList) {
        if (redisRaceInfoList == null || redisRaceInfoList.isEmpty()) {
            return Results.SUCCESS;
        }
        redisRaceInfoList.forEach(v -> {
            String key = RedisKeys.EVENT_RACE + v.getStartTime().toInstant().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) + ":" + v.getId() + ":" + v.getCategory();
            //TODO 这里有个计算返利率的公式
            //calculateRebate(v, key);
            calculateRebate(v);
            //判断数据库是否存在此数据
            RaceInfo raceInfo = raceInfoMapper.selectByPrimaryKey(v.getId());
            if (raceInfo != null) {
                //赛事存在,更新数据库赛事比分-结果-赔率
                RaceInfo updateRecd = new RaceInfo();
                updateRecd.setCategory(v.getCategory());
                updateRecd.setStartTime(v.getStartTime());
                updateRecd.setEndTime(v.getEndTime());
                updateRecd.setHomeTeam(v.getHomeTeam());
                updateRecd.setVisitTeam(v.getVisitTeam());
                updateRecd.setRaceStatus(v.getRaceResult());
                updateRecd.setWinTeam(v.getWinTeam());
                updateRecd.setWinResult(v.getWinResult());
                updateRecd.setWinType(v.getWinType());
                updateRecd.setHalfResult(v.getHalfResult());
                updateRecd.setId(v.getId());
                raceInfoMapper.updateByPrimaryKeySelective(updateRecd);
            }
            //更新缓存
            Date startTime = v.getStartTime();
            //修改成30天
            long expireTime = startTime.toInstant().atZone(ZoneId.systemDefault()).plusDays(30).toInstant().toEpochMilli();
            cloudRedisService.save(key, v, expireTime);
        });
        return new Results.Result(Results.SUCCESS, null);
    }

    private void calculateRebate(RedisRaceInfo raceInfo, String key) {
        List<RedisRaceRebateInfo> oddsInfos = raceInfo.getRebates();
        System.out.println(oddsInfos.toString());
        BigDecimal LIMIT_COUNT = new BigDecimal(0.01);
        final BigDecimal[] totalRebateRatio = {BigDecimal.ZERO};//正常返利率总和
        List<RedisRaceRebateInfo> collect = oddsInfos.stream().map(v -> {
            if (!redisTemplate.hasKey(key)) {
                BigDecimal startOdds = v.getStartOdds();
                v.setStartOdds(startOdds); //初始赔率
            }
            BigDecimal normalOdds = v.getNormalOdds();//正波胆赔率
            BigDecimal normalRebate = BigDecimal.ONE.divide(normalOdds, 2, BigDecimal.ROUND_HALF_UP);//正波胆返利率
            if (normalRebate.compareTo(LIMIT_COUNT) < 0) normalRebate = LIMIT_COUNT;
            totalRebateRatio[0] = totalRebateRatio[0].add(normalRebate);
            v.setOpenStatus(ScoreTypeEnum.open.getCode());

            v.setNormalOdds(v.getNormalOdds()); //正波胆赔率
            v.setNormalRebate(normalRebate);
            return v;
        }).collect(Collectors.toList());
        BigDecimal totalFbdRebateRatio = BigDecimal.ONE.divide(totalRebateRatio[0], 2, BigDecimal.ROUND_HALF_UP);//总反波胆返利率
        System.out.println("总波胆" + totalFbdRebateRatio);
        collect.forEach(v -> {
            BigDecimal normalRebate = v.getNormalRebate();//正波胆返利率
            BigDecimal fbdRate = normalRebate.multiply(totalFbdRebateRatio).multiply(totalFbdRebateRatio);
            BigDecimal oppositeOdds = BigDecimal.ONE.divide(fbdRate,2, BigDecimal.ROUND_HALF_UP);

            BigDecimal oppositeRebate = normalRebate.multiply(totalFbdRebateRatio).setScale(2, BigDecimal.ROUND_HALF_UP);//反波胆返利率
            v.setOppositeRebate(oppositeRebate);
            v.setOppositeOdds(oppositeOdds); //反波胆赔率
        });
        raceInfo.setRebates(collect);
    }

    private void calculateRebate(RedisRaceInfo raceInfo) {
        List<RedisRaceRebateInfo> rebates = raceInfo.getRebates();
        BigDecimal totalRebate = BigDecimal.ZERO;//正波胆返利率总和
        BigDecimal fbdTotalRebate = BigDecimal.ZERO;//反波胆返利率总和
        for (RedisRaceRebateInfo info : rebates) {
            BigDecimal normalOdds = info.getNormalOdds();//正波胆赔率
            BigDecimal BDRebate = BigDecimal.ONE.divide(normalOdds, 4, BigDecimal.ROUND_HALF_UP);//正波胆返利率
            info.setNormalRebate(BDRebate);//正波胆返利率
            totalRebate = totalRebate.add(BDRebate);//总正波胆返利率之和
        }
        fbdTotalRebate = BigDecimal.ONE.divide(totalRebate, 4, BigDecimal.ROUND_HALF_UP);
        for (RedisRaceRebateInfo info : rebates) {
            BigDecimal normalRebate = info.getNormalRebate();//正波胆返利率
            BigDecimal fbdRebate = normalRebate.multiply(fbdTotalRebate).setScale(4, BigDecimal.ROUND_HALF_UP);//反波胆返利率
            info.setOppositeRebate(fbdRebate);//反波胆返利率
            BigDecimal fbdOdd = BigDecimal.ONE.divide(normalRebate.multiply(fbdTotalRebate).multiply(fbdTotalRebate).setScale(4, BigDecimal.ROUND_HALF_UP), 4, BigDecimal.ROUND_HALF_UP);//反波胆赔率
            info.setOppositeOdds(fbdOdd);//反波胆赔率
        }
    }

    /**
     * 重新计算返利率
     * 1.查询数据库中 本赛事 所有开启状态的比分
     * 2.跟数据中的比分进行对比，找到该比分对应的redis 比分的正向波胆赔率 p
     * 3. 1/p->f  -> f1+f2+f3....+fn=>sum(fn)=>total_f
     * 4. 1/sum(fn)=>total_fp
     * 5. fn*total_fp=ff
     *
     */
    public Map<Integer, RebateInfoDto> reCalculateRebate(Integer openStatus, RaceRebateInfo raceRebateInfo, BigDecimal base) {
        Map<Integer, RebateInfoDto> result = new HashMap<>();
        String raceId = raceRebateInfo.getRaceId();
        RaceRebateInfoExample example = new RaceRebateInfoExample();
        example.or().andRaceIdEqualTo(raceId).andOpenStatusEqualTo(SysStatusEnum.VALID1.ordinal()).andRuleTypeEqualTo(BetTypeEnum.score.getCode());
        List<RaceRebateInfo> infos = raceRebateInfoMapper.selectByExample(example);
        List<RaceRebateInfo> validInfos = Lists.newArrayList();
        if (infos.isEmpty()) {
            return result;
        }
        Set keys = redisTemplate.keys(RedisKeys.EVENT_RACE + "*" + raceId + "*");
        List<JSONObject> list = redisTemplate.opsForValue().multiGet(keys);
        if (list == null || list.isEmpty()) {
            return result;
        }
        JSONObject jsonObject = list.get(0);
        RedisRaceInfo redisRaceInfo = JSON.parseObject(JSON.toJSONString(jsonObject), RedisRaceInfo.class);
        List<RedisRaceRebateInfo> rebates = redisRaceInfo.getRebates();
        if (rebates.isEmpty()) {
            return result;
        }
        //正波胆赔率
        Map<String, BigDecimal> BDOdds = rebates.stream().collect(Collectors.toMap(RedisRaceRebateInfo::getScore, RedisRaceRebateInfo::getNormalOdds));
        BigDecimal totalRebate = BigDecimal.ZERO;
        Map<Integer, BigDecimal> rebateIdMap = new HashMap<>();

        // 查看这个比分在数据库是不是开启的 true: 开启 false: 关闭
        Boolean flag = false;
        for (RaceRebateInfo v : infos) {// 传递的比分可能在开启的比分集合里，也可能不在，以下两个if 不互斥，不可优化
            if(!String.valueOf(v.getId()).equals(raceRebateInfo.getId().toString()) ) {
                validInfos.add(v);
            }
            if(String.valueOf(v.getId()).equals(raceRebateInfo.getId().toString()) ) {
                flag = true;
            }
        }
        if (flag) { //传递的比分在数据库比分开启，但是传参是关闭，则将该比分信息移除计算返利率集合
            if(openStatus != ScoreTypeEnum.closed.getCode()) {
                validInfos.add(raceRebateInfo);
            }
        } else { //数据库比分关闭，但是传参是开启，则将该比分信息加入计算返利率集合
            if(openStatus == ScoreTypeEnum.open.getCode()) {
                validInfos.add(raceRebateInfo);
            }
        }
        for (RaceRebateInfo info : validInfos) {
            String rule = info.getRule();
            ScoreRule serialize = (ScoreRule) ruleMap.get(info.getRuleType()).serialize(rule);
            String score = serialize.getScore();
            //查询此比分的redis信息
            BigDecimal BDOdd = BDOdds.get(score);//波胆赔率
            if (BDOdd == null) {
                log.error("can not get any BDOdd from redis for raceId:{} and score:{}", raceId, score);
                continue;
            }
            //波胆返利率
            BigDecimal BDRebate = BigDecimal.ONE.divide(BDOdd, 4, BigDecimal.ROUND_HALF_UP);
            totalRebate = totalRebate.add(BDRebate);
            rebateIdMap.putIfAbsent(info.getId(), BDRebate);
        }
        //反波胆总返利率之和
        BigDecimal totalFBDRebate = BigDecimal.ONE.divide(totalRebate, 4, BigDecimal.ROUND_HALF_UP);
        for (Map.Entry<Integer, BigDecimal> next : rebateIdMap.entrySet()) {
            Integer rebateId = next.getKey();
            BigDecimal BDRebate = next.getValue();
            //反波胆返利率
            BigDecimal FBDRebate = BDRebate.multiply(totalFBDRebate).setScale(4, BigDecimal.ROUND_HALF_UP);
            if (base != null) {
                FBDRebate = FBDRebate.multiply(base).setScale(4, BigDecimal.ROUND_HALF_UP);
            }
            //反波胆赔率
            BigDecimal FBDOdd = BigDecimal.ONE.divide(BDRebate.multiply(totalFBDRebate).multiply(totalFBDRebate), 4, BigDecimal.ROUND_HALF_UP);
            RebateInfoDto rebateInfoDto = new RebateInfoDto();
            rebateInfoDto.setOdd(FBDOdd);
            rebateInfoDto.setRebate(FBDRebate);
            result.put(rebateId, rebateInfoDto);
        }
        return result;
    }

    @Override
    public Results.Result<PageInfo<RedisRaceInfo>> eventRecommendList(Integer pageNum, Integer pageSize) {
        PageInfo<RaceInfo> pageInfo = PageHelper.startPage(pageNum, pageSize).doSelectPageInfo(() -> {
            RaceInfoExample raceInfoExample = new RaceInfoExample();
            RaceInfoExample.Criteria or = raceInfoExample.or();
            // or.andIsRecommendEqualTo(EventRecommendStatusEnum.IsRecommend.getCode());// 不过滤是否推荐，按比重排列

//            String time = TimeUtil.addTime(TimeUtil.ChangeDateFormat(new Date(), "yyyy-MM-dd HH:mm:ss"), "15"); //开赛15分钟前
//            or.andStartTimeGreaterThan(TimeUtil.getDateFormat(time, "yyyy-MM-dd HH:mm:ss"));
            or.andStartTimeGreaterThan(new Date());
            or.andRaceStatusEqualTo(RaceStatusEnum.un_start.getCode());
            raceInfoExample.setOrderByClause("weight desc");
            raceInfoMapper.selectByExample(raceInfoExample);
        });
        if (pageInfo.getList().isEmpty()) {
            return new Results.Result<>(Results.Result.NOT_FIND, "暂无赛事", null);
        }
        //查询返利率和赔率
        List<RaceInfo> infos = pageInfo.getList();
        List<RedisRaceInfo> collect = infos.stream().map(v -> {
            final BigDecimal[] totalBetAmount = {BigDecimal.ZERO};
            final BigDecimal[] totalValidAmount = {BigDecimal.ZERO};
            //查询赔率

            //查询返利率
            RaceRebateInfoExample rebateInfoExample = new RaceRebateInfoExample();
            rebateInfoExample.or().andRaceIdEqualTo(v.getId()).andOpenStatusEqualTo(SysStatusEnum.VALID1.ordinal());
            List<RaceRebateInfo> rebateInfos = raceRebateInfoMapper.selectByExample(rebateInfoExample);
            List<RedisRaceRebateInfo> rebateInfoList = new ArrayList<>();
            rebateInfos.forEach(vvv -> {
                RedisRaceRebateInfo rebateInfo = new RedisRaceRebateInfo();
                //该赛事的所有可投注比分
                //查询每个赛事的交易量信息
                List<RebateBetInfo> betInfos = rebateBetInfo(new ArrayList<Integer>() {{
                    add(vvv.getId());
                }});
                if (betInfos != null && !betInfos.isEmpty()) {
                    RebateBetInfo betInfo = betInfos.get(0);
                    BigDecimal betAmount = betInfo.getBetAmount();
                    totalBetAmount[0] = totalBetAmount[0].add(betAmount);
                }
                //每个比分的总下单量
                totalValidAmount[0] = totalValidAmount[0].add(vvv.getValidAmount());
                BeanUtils.copyProperties(vvv, rebateInfo);
                rebateInfoList.add(rebateInfo);

            });
            RedisRaceInfo redisRaceInfo = new RedisRaceInfo();
            BeanUtils.copyProperties(v, redisRaceInfo);
//            redisRaceInfo.setOddsInfos(oddsInfos);
            redisRaceInfo.setRebates(rebateInfoList);
            //计算总的进度和总交易量
            redisRaceInfo.setTotalBetAmount(totalBetAmount[0].compareTo(BigDecimal.ZERO) > 0 ? totalBetAmount[0] : BigDecimal.ZERO);
            redisRaceInfo.setTotalValidAmount(totalValidAmount[0]);
            redisRaceInfo.setProcess(MIN_PROCESS);//
            BigDecimal betAmount = redisRaceInfo.getTotalBetAmount();
            betAmount = betAmount==null||betAmount.compareTo(BigDecimal.ZERO)<=0?BigDecimal.ZERO:betAmount;
            //总下单量/(总下单量+ 总当前可交易量)
            if (totalValidAmount[0].compareTo(BigDecimal.ZERO) > 0
                    && totalBetAmount[0].divide(totalValidAmount[0].add(betAmount),2, BigDecimal.ROUND_HALF_UP).compareTo(MIN_PROCESS) > 0) {
                redisRaceInfo.setProcess(totalBetAmount[0].divide(totalValidAmount[0].add(betAmount),2, BigDecimal.ROUND_HALF_UP));
            }
            return redisRaceInfo;

        }).collect(Collectors.toList());
        PageInfo<RedisRaceInfo> result = new PageInfo<>();
        BeanUtils.copyProperties(pageInfo, result, "list");
        result.setList(collect);
        return new Results.Result(Results.SUCCESS, result);
    }

    @Override
    public Results.Result<Void> cacheRaceTrend(RedisTeamTrendInfo trendInfo) {
        Date expireTime = Date.from(LocalDateTime.now().plusDays(30).atZone(ZoneId.systemDefault()).toInstant());
        cloudRedisService.save(RedisKeys.EVENT_TREND + trendInfo.getTeam(), trendInfo, expireTime);
        return Results.SUCCESS;
    }

    @Override
    public Results.Result<Void> cacheBothSide(List<RedisBothSideRaceHistory> histories) {
        Date expireTime = Date.from(LocalDateTime.now().plusDays(30).atZone(ZoneId.systemDefault()).toInstant());
        histories.forEach(v -> {
            cloudRedisService.save(RedisKeys.EVENT_HISTORY_BOTH_SIDE +
                    v.getTeamA() + "|"
                    + v.getTeamB() + ":"
                    + v.getDate() + ":"
                    + v.getRaceName(), v, expireTime);

        });
        return Results.SUCCESS;
    }

    @Override
    public Results.Result<Void> cacheRaceHistory(List<RedisRaceHistory> histories) {
        Date expireTime = Date.from(LocalDateTime.now().plusDays(30).atZone(ZoneId.systemDefault()).toInstant());
        histories.forEach(v -> {
            cloudRedisService.save(RedisKeys.EVENT_HISTORY_RACE_INFO +
                    v.getTeams() + ":"
                    + v.getDate() + ":"
                    + v.getTeam() + ":"
                    + v.getRaceName(), v, expireTime);
        });
        return Results.SUCCESS;
    }

    @Transactional
    @Override
    public Results.Result insertDiveRule(Double increase, Double autochangeTime, Integer timeRange, Double startAmount, Double shutDownRebate, Double startRebate, Double endRebate, Integer ruleType, Integer enableStatus) {
        DiveRuleExample diveRuleExample = new DiveRuleExample();
        diveRuleExample.or().andEnableStatusEqualTo(SysStatusEnum.VALID1.ordinal()).andSysStatusEqualTo(SysStatusEnum.VALID1.ordinal()).andTimeRangeEqualTo(timeRange);
        List<DiveRule> rules = diveRuleMapper.selectByExample(diveRuleExample);
        if (!rules.isEmpty()) {
            for (DiveRule rule : rules) {
                if (checkRule(startRebate, endRebate, rule)) {
                    return new Results.Result<>(Results.PARAMETER_INCORRENT, "规则利率在已有规则区间内（" + startRebate + "," + endRebate + "),请校验");
                }
             }
        }

        DiveRule diveRule = new DiveRule();
        diveRule.setIncrease(new BigDecimal(increase));
        diveRule.setAutochangeTime(new BigDecimal(autochangeTime));
        diveRule.setTimeRange(timeRange);
        diveRule.setStartAmount(startAmount == null ? null : new BigDecimal(startAmount));
        diveRule.setShutDownRebate(shutDownRebate == null ? null : new BigDecimal(shutDownRebate));
        diveRule.setStartRebate(startRebate == null ? null : new BigDecimal(startRebate));
        diveRule.setEndRebate(endRebate == null ? null : new BigDecimal(endRebate));
        diveRule.setEnableStatus(enableStatus);
        diveRule.setRuleType(ruleType);
        diveRule.setCreateTime(new Date());
        diveRuleMapper.insertSelective(diveRule);
        return Results.SUCCESS;

    }

    @Override
    public Results.Result deleteRule(Integer id){
        if (id == null) {
            return Results.PARAMETER_INCORRENT;
        }
        DiveRule diveRule = new DiveRule();
        diveRule.setId(id);
        diveRule.setSysStatus(SysStatusEnum.INVALID0.ordinal());
        diveRuleMapper.updateByPrimaryKeySelective(diveRule);
        return Results.SUCCESS;
    }

    @Override
    @Transactional
    public Results.Result<Void> submitOperate(List<OperateDto> list) {
        final BigDecimal[] totalRatio = {BigDecimal.ZERO};
        List<@NotEmpty Integer> collect = list.stream().map(v -> v.getRebateId()).collect(Collectors.toList());
        RaceRebateInfoExample rebateExample = new RaceRebateInfoExample();
        rebateExample.or().andIdIn(collect);
        List<RaceRebateInfo> allRaceRebate = raceRebateInfoMapper.selectByExample(rebateExample);

        RaceRebateInfoExample example = new RaceRebateInfoExample();
        example.or().andIdIn(collect).andOpenStatusEqualTo(SysStatusEnum.VALID1.ordinal());
        List<RaceRebateInfo> rebateInfos = raceRebateInfoMapper.selectByExample(example);
        List<Integer> DBIds = rebateInfos.stream().map(v -> v.getId()).collect(Collectors.toList());
        list.forEach(v -> {
            if (DBIds.contains(v.getRebateId())) {
                BigDecimal rebateRatio = v.getRebateRatio();
                totalRatio[0] = totalRatio[0].add(rebateRatio);
            }
        });
        if (totalRatio[0].compareTo(BigDecimal.ONE) > 0) {
            return new Results.Result<>(Results.Result.PARAMETER_INCORRENT, "返利率总和超过100%,请修改");
        }
        list.forEach(v -> {
            Integer rebateId = v.getRebateId();
            BigDecimal rebateRatio = v.getRebateRatio();

            int updateDive = raceRebateInfoMapperEx.updateDive(rebateId, rebateRatio, v.getOpenStatus());
            if (updateDive != 1) {
                throw new SacException("更新返利率失败");
            }
            if (v.getValidAmount() != null) {
                RaceRebateInfo rebateInfo = new RaceRebateInfo();
                rebateInfo.setId(rebateId);
                rebateInfo.setValidAmount(v.getValidAmount());
                int updateValidAmount = raceRebateInfoMapper.updateByPrimaryKeySelective(rebateInfo);
                if (updateValidAmount != 1) {
                    throw new SacException("更新可下单量失败");
                }
            }
        });
        //更新redis里面的初始返利率
        if (allRaceRebate == null || allRaceRebate.isEmpty()) {
            return new Results.Result<>(Results.SUCCESS, null);
        }
        String raceId = allRaceRebate.get(0).getRaceId();
        Set keys = redisTemplate.keys(RedisKeys.EVENT_RACE + "*" + raceId + "*");
        List<JSONObject> redisRaces = redisTemplate.opsForValue().multiGet(keys);
        if (redisRaces == null || redisRaces.isEmpty()) {
            return new Results.Result<>(Results.SUCCESS, null);
        }
        System.out.println(redisRaces.toString());
        JSONObject jsonObject = redisRaces.get(0);
        RedisRaceInfo redisRaceInfo = JSON.parseObject(JSON.toJSONString(jsonObject), RedisRaceInfo.class);
        List<RedisRaceRebateInfo> rebates = redisRaceInfo.getRebates();
        for (RedisRaceRebateInfo v : rebates) {
            for (OperateDto dto : list) {
                if (v.getScore().startsWith(dto.getScore())) {
                    v.setStartRebate(dto.getInitRebateRatio());
                    v.setOppositeOdds(dto.getOppositeOdd());
                    v.setOppositeRebate(dto.getOppositeRebate());
                }
            }
        }
        keys.forEach(key -> {
            cloudRedisService.updateOutTime(key, redisRaceInfo, LocalDateTime.now().plusDays(30).toEpochSecond(ZoneOffset.of("+8")));

        });
        return new Results.Result<>(Results.SUCCESS, null);
    }

    private Map<String, RedisTeamTrendInfo> getRaceTrend(String teamName) {
        Map<String, RedisTeamTrendInfo> map = new HashMap<>();
        Set keys = redisTemplate.keys(RedisKeys.EVENT_TREND + teamName + "*");
        List<JSONObject> list = redisTemplate.opsForValue().multiGet(keys);
        list.forEach(v -> {
            RedisTeamTrendInfo trendInfo = JSON.parseObject(JSON.toJSONString(v), RedisTeamTrendInfo.class);
            map.put(trendInfo.getTeam(), trendInfo);
        });
        return map;
    }

    private List<RedisBothSideRaceHistory> getBothSide(String teamNames) {
        Set keys = redisTemplate.keys(RedisKeys.EVENT_HISTORY_BOTH_SIDE + teamNames + "*");
        List<JSONObject> list = redisTemplate.opsForValue().multiGet(keys);
        assert list != null;
        List<RedisBothSideRaceHistory> collect = list.stream().map(v -> JSON.parseObject(JSON.toJSONString(v), RedisBothSideRaceHistory.class)).collect(Collectors.toList());
        return collect;
    }

    private Map<String, List<RedisRaceHistory>> getRaceHistory(String teamNames) {
        Map<String, List<RedisRaceHistory>> map = new HashMap<>();
        Set keys = redisTemplate.keys(RedisKeys.EVENT_HISTORY_RACE_INFO + teamNames + "*");
        List<JSONObject> list = redisTemplate.opsForValue().multiGet(keys);
        assert list != null;
        list.stream().forEach(v -> {
            RedisRaceHistory history = JSON.parseObject(JSON.toJSONString(v), RedisRaceHistory.class);
            List<RedisRaceHistory> histories = map.getOrDefault(history.getTeam(), new ArrayList<RedisRaceHistory>());
            histories.add(history);
            map.put(history.getTeam(), histories);
        });
        return map;

    }

    @Override
    public Results.Result<Void> updateRule(DiveRuleDto dto) {
        Integer id = dto.getId();
        if (id == null) {
            return Results.PARAMETER_INCORRENT;
        }
        DiveRule selectRule = diveRuleMapper.selectByPrimaryKey(id);
        if (selectRule == null) {
            return Results.PARAMETER_INCORRENT;
        }
        Double startRebate = dto.getStartRebate().doubleValue();
        Double endRebate = dto.getEndRebate().doubleValue();
        if (checkRule(startRebate, endRebate, selectRule)) {
            return new Results.Result(Results.PARAMETER_INCORRENT, "规则利率在已有规则区间内（" + selectRule.getStartRebate() + "," + selectRule.getEndRebate() + "),请校验");
        }

        DiveRule rule = new DiveRule();
        rule.setEnableStatus(dto.getEnableStatus());
        rule.setAutochangeTime(dto.getAutochangeTime());
        rule.setTimeRange(dto.getTimeRange());
        rule.setEndRebate(dto.getEndRebate());
        rule.setIncrease(dto.getIncrease());
        rule.setRuleType(dto.getRuleType());
        rule.setStartAmount(dto.getStartAmount());
        rule.setStartRebate(dto.getStartRebate());
        rule.setId(dto.getId());
        rule.setShutDownRebate(dto.getShutDownRebate());
        diveRuleMapper.updateByPrimaryKeySelective(rule);
        return Results.SUCCESS;

    }

    public List<RebateBetInfo> rebateBetInfo(List<Integer> rebateIds) {
        if (rebateIds.isEmpty()) {
            return new ArrayList<>();
        }
        return betRecdMapperEx.getRebateBetInfo(rebateIds);

    }

    @Override
    public void handleMessage(String message) {
        log.info("received auto dive message:{} ,start auto dive", message);
        List<String> diveMsg = Splitter.on("-").trimResults().splitToList(message);
//        executorService.submit(() -> {
//            autoRebateDive(diveMsg.get(0), Integer.valueOf(diveMsg.get(1)));
//        });
    }

    private BigDecimal getTotalRebate(String raceId) {
        RaceRebateInfoExample example = new RaceRebateInfoExample();
        example.or().andRaceIdEqualTo(raceId);
        List<RaceRebateInfo> rebateInfos = raceRebateInfoMapper.selectByExample(example);
        final BigDecimal[] totalRebate = {new BigDecimal(0)};
        rebateInfos.forEach(rebate -> {
            ScoreRule rebateRule = (ScoreRule) ruleMap.get(rebate.getRuleType()).serialize(rebate.getRule());
            totalRebate[0] = totalRebate[0].add(new BigDecimal(rebateRule.getRebateRatio()));
        });
        return totalRebate[0];
    }

    public Results.Result<PageInfo<String>> raceCategories(Integer curPage, Integer pageSize) {
        PageInfo<String> info = PageHelper.startPage(curPage, pageSize).doSelectPageInfo(() -> {
            raceInfoMapperEx.selectDistinctCategory();
        });
        return new Results.Result<>(Results.SUCCESS, info);
    }

    @Override
    @Transactional
    public Results.Result<Void> sumitInitRebate(InitRebateDto dto) {
        String raceId = dto.getRaceId();
        List<InitRebateDetailDto> list = dto.getList();
        Set keys = redisTemplate.keys(RedisKeys.EVENT_RACE + "*" + raceId + "*");
        List<JSONObject> redisRaces = redisTemplate.opsForValue().multiGet(keys);
        JSONObject jsonObject = redisRaces.get(0);
        RedisRaceInfo redisRaceInfo = JSON.parseObject(JSON.toJSONString(jsonObject), RedisRaceInfo.class);
        List<RedisRaceRebateInfo> rebates = redisRaceInfo.getRebates();
        for (RedisRaceRebateInfo v : rebates) {
            for (InitRebateDetailDto detail : list) {
                if (v.getScore().startsWith(detail.getScore())) {
                    v.setStartRebate(detail.getRebateRatio());

                    RaceRebateInfo rebateInfo = new RaceRebateInfo();
                    rebateInfo.setId(detail.getRebateId());
                    rebateInfo.setOpenStatus(detail.getOpenStatus());
                    raceRebateInfoMapper.updateByPrimaryKeySelective(rebateInfo);
                }
            }
        }

        keys.forEach(key -> {
            cloudRedisService.updateOutTime(key, redisRaceInfo, LocalDateTime.now().plusDays(30).toEpochSecond(ZoneOffset.of("+8")));

        });
        return Results.SUCCESS;
    }

    public List<AdminRebateInfo> sortAdmin(List<AdminRebateInfo> list) {
        List<AdminRebateInfo> low = new ArrayList<>();
        List<AdminRebateInfo> mid = new ArrayList<>();
        List<AdminRebateInfo> large = new ArrayList<>();
        for (AdminRebateInfo info : list) {
            String score = info.getScore();
            if (score.contains("其他")) {
                mid.add(info);
                continue;
            }
            List<String> scores = Splitter.on("-").trimResults().splitToList(score);
            if (scores.size() < 2) {
                mid.add(info);
                continue;
            }
            String prefix = scores.get(0);
            String suffix = scores.get(1);
            //前缀<=3 &&后缀<=3
            if (Integer.valueOf(prefix).compareTo(3) <= 0 && Integer.valueOf(suffix).compareTo(3) <= 0) {
                low.add(info);
            } else {
                large.add(info);
            }
        }
        //对low和large 进行排序
        Collections.sort(low, (o1, o2) -> {
            String first = o1.getScore();
            String second = o2.getScore();
            List<String> firstScores = Splitter.on("-").trimResults().splitToList(first);
            List<String> secondScores = Splitter.on("-").trimResults().splitToList(second);

            Integer firstPrefix = Integer.valueOf(firstScores.get(0));
            Integer secondPrefix = Integer.valueOf(secondScores.get(0));

            Integer firstSuffix = Integer.valueOf(firstScores.get(1));
            Integer secondSuffix = Integer.valueOf(secondScores.get(1));
            if (firstPrefix.compareTo(secondPrefix) < 0) {
                return firstPrefix - secondPrefix;
            } else if (firstPrefix.compareTo(secondPrefix) == 0) {
                return firstSuffix - secondSuffix;
            }
            return 0;
        });
        //对low和large 进行排序
        Collections.sort(large, (o1, o2) -> {
            String first = o1.getScore();
            String second = o2.getScore();
            List<String> firstScores = Splitter.on("-").trimResults().splitToList(first);
            List<String> secondScores = Splitter.on("-").trimResults().splitToList(second);

            Integer firstPrefix = Integer.valueOf(firstScores.get(0));
            Integer secondPrefix = Integer.valueOf(secondScores.get(0));

            Integer firstSuffix = Integer.valueOf(firstScores.get(1));
            Integer secondSuffix = Integer.valueOf(secondScores.get(1));
            if (firstPrefix.compareTo(secondPrefix) < 0) {
                return -(firstPrefix - secondPrefix);
            } else if (firstPrefix.compareTo(secondPrefix) == 0) {
                return (firstSuffix - secondSuffix);
            }
            return 0;
        });
        return new ArrayList<AdminRebateInfo>() {
            {
                addAll(low);
                addAll(mid);
                addAll(large);
            }
        };
    }

    private List<RedisRaceRebateInfo> sort(List<RedisRaceRebateInfo> list) {
        List<RedisRaceRebateInfo> low = new ArrayList<>();
        List<RedisRaceRebateInfo> mid = new ArrayList<>();
        List<RedisRaceRebateInfo> large = new ArrayList<>();

        RedisRaceRebateInfo temp = RedisRaceRebateInfo.builder().score("TEMP").build();
        for (RedisRaceRebateInfo info : list) {
            String score = info.getScore();
            if (score.contains("其他")) {
                mid.add(info);
                continue;
            }
            List<String> scores = Splitter.on("-").trimResults().splitToList(score);
            if (scores.size() < 2) {
                mid.add(info);
                continue;
            }
            String prefix = scores.get(0);
            String suffix = scores.get(1);
            //前缀<=3 &&后缀<=3
            if (Integer.valueOf(prefix).compareTo(3) <= 0 && Integer.valueOf(suffix).compareTo(3) <= 0) {
                low.add(info);
            } else {
                if (prefix.equals(suffix)) {//4:4 提前
                    mid.add(info);
                    continue;
                }
                large.add(info);
            }
        }
        //对low和large 进行排序
        sortScores(low, SysStatusEnum.INVALID0.ordinal());
        //对low和large 进行排序
        sortScores(large, SysStatusEnum.VALID1.ordinal());
        return new ArrayList<RedisRaceRebateInfo>() {
            {
                addAll(low);
                addAll(mid);
                addAll(large);
            }
        };
    }

    private List<RedisRaceRebateInfo> complexSort(List<RedisRaceRebateInfo> list) {
        List<RedisRaceRebateInfo> leftOne = new ArrayList<>();// 左边第一行
        List<RedisRaceRebateInfo> leftTwo = new ArrayList<>();// 左边第二行
        List<RedisRaceRebateInfo> midOne = new ArrayList<>();// 中间第一行
        List<RedisRaceRebateInfo> midTwo = new ArrayList<>();// 中间第二行
        List<RedisRaceRebateInfo> rightOne = new ArrayList<>();
        List<RedisRaceRebateInfo> rightTwo = new ArrayList<>();
        List<RedisRaceRebateInfo> other = new ArrayList<>();

        for (RedisRaceRebateInfo info : list) {
            String score = info.getScore();
            if (score.contains("其他")) {
                other.add(info);
                continue;
            }
            List<String> scores = Splitter.on("-").trimResults().splitToList(score);
            if (scores.size() < 2) {
                other.add(info);
                continue;
            }
            String prefix = scores.get(0);
            String suffix = scores.get(1);

            if (prefix.equals(suffix)) {// 比分相等—> 放中间list, 跳过循环 ——> 比分小于等于1，放第一行
                if (Integer.valueOf(prefix).compareTo(1) <= 0) {
                    midOne.add(info);
                } else {
                    midTwo.add(info);
                }
                continue;
            }

            //前缀<=3 &&后缀<=3
            if (Integer.valueOf(prefix).compareTo(3) <= 0 && Integer.valueOf(suffix).compareTo(3) <= 0) {
                if (Integer.valueOf(prefix).compareTo(Integer.valueOf(suffix)) > 0) {
                    leftOne.add(info);
                } else {
                    leftTwo.add(info);
                }
            } else {
                if (Integer.valueOf(prefix).compareTo(Integer.valueOf(suffix)) > 0) {
                    rightOne.add(info);
                } else {
                    rightTwo.add(info);
                }
            }
        }
        //对low和large 进行排序
        sortScores(leftOne);
        sortScores(leftTwo);

        sortScores(rightOne);
        sortScores(rightTwo);

        sortScores(midOne);
        sortScores(midTwo);

        return new ArrayList<RedisRaceRebateInfo>() {
            {
                addAll(leftOne);
                addAll(midOne);
                addAll(other);
                addAll(rightOne);
                addAll(leftTwo);
                addAll(midTwo);
                addAll(rightTwo);
            }
        };
    }

    /**
     * @param raceLists
     * @param style  VALID1: 比分第二位倒序
     */
    private void sortScores(List<RedisRaceRebateInfo> raceLists, int style) {
        Collections.sort(raceLists, (o1, o2) -> {
            String first = o1.getScore();
            String second = o2.getScore();
            List<String> firstScores = Splitter.on("-").trimResults().splitToList(first);
            List<String> secondScores = Splitter.on("-").trimResults().splitToList(second);

            Integer firstPrefix = Integer.valueOf(firstScores.get(0));
            Integer secondPrefix = Integer.valueOf(secondScores.get(0));

            Integer firstSuffix = Integer.valueOf(firstScores.get(1));
            Integer secondSuffix = Integer.valueOf(secondScores.get(1));
            if (style == SysStatusEnum.VALID1.ordinal()) {
                if (firstPrefix.compareTo(secondPrefix) < 0) {
                    return -(firstPrefix - secondPrefix);
                } else if (firstPrefix.compareTo(secondPrefix) == 0) {
                    return (firstSuffix - secondSuffix);
                }
            } else {
                if (firstPrefix.compareTo(secondPrefix) < 0) {
                    return firstPrefix - secondPrefix;
                } else if (firstPrefix.compareTo(secondPrefix) == 0) {
                    return firstSuffix - secondSuffix;
                }
            }
            return 0;
        });
    }

    private void sortScores(List<RedisRaceRebateInfo> raceLists) {
        sortScores(raceLists, SysStatusEnum.INVALID0.ordinal());
    }
}

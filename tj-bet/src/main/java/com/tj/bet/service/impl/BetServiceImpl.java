package com.tj.bet.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.util.StringUtil;
import com.google.common.collect.Lists;
import com.tj.bet.config.BetStrategy;
import com.tj.bet.dao.*;
import com.tj.bet.domain.*;
import com.tj.bet.service.BetService;
import com.tj.bet.service.CommonService;
import com.tj.bet.service.DiveService;
import com.tj.bet.service.RollbackService;
import com.tj.dto.*;
import com.tj.util.A.SacException;
import com.tj.util.AbstractRule;
import com.tj.util.Results;
import com.tj.util.enums.*;
import com.tj.util.enums.otc.SubOrAddEnum;
import com.tj.util.log.Rlog;
import com.tj.util.redis.CloudRedisService;
import com.tj.util.time.TimeUtil;
import com.tj.util.unique.Unique;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @program: tj-core
 * @description: 用户下注
 * @author: liang.song
 * @create: 2018-12-12-14:42
 **/
@Service
@Slf4j
public class BetServiceImpl implements BetService {
    @Resource
    private RaceInfoMapper raceInfoMapper;
    @Value("${bet.limit.time}")
    private Integer betLimitTime;
    @Resource
    private RaceRebateInfoMapper raceRebateInfoMapper;
    @Resource
    private UserBalanceInfoMapper userBalanceInfoMapper;
    private final Unique unique;
    @Resource
    private BetRecdMapper betRecdMapper;
    @Resource
    private UserBalanceInfoMapperEx userBalanceInfoMapperEx;
    @Resource
    private UserBalanceChangeRecdMapper userBalanceChangeRecdMapper;
    private final Rlog rlog;
    @Resource
    private UserInfoMapper userInfoMapper;
    private final List<BetStrategy> strategies;
    @Resource
    private BetRecdMapperEx betRecdMapperEx;
    @Resource
    private DiveRuleMapper diveRuleMapper;
    @Resource
    private RaceRebateInfoMapperEx raceRebateInfoMapperEx;
    private Map<Integer, Class> jsonMap = new ConcurrentHashMap<>();
    private Map<Integer, BetStrategy> strategyMap = new ConcurrentHashMap<>();
    private Map<Integer, AbstractRule> ruleMap = new ConcurrentHashMap<>();
    private final CommonService commonService;
    @Autowired
    private final RollbackService rollbackService;
    @Autowired
    private RedisTemplate redisTemplate;

    private CloudRedisService cloudRedisService;

    private final DiveService diveService;

    @Value("${bet.cancelTime}")
    private Integer cancelTime;

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Autowired
    public BetServiceImpl(Unique unique, Rlog rlog, List<BetStrategy> strategies, List<AbstractRule> rules, CommonService commonService,RollbackService rollbackService,CloudRedisService cloudRedisService, DiveService diveService) {
        this.unique = unique;
        this.rlog = rlog;
        this.strategies = strategies;
        for (BetStrategy strategy : strategies) {
            strategyMap.putIfAbsent(strategy.type().getCode(), strategy);
        }
        for (AbstractRule rule : rules) {
            ruleMap.putIfAbsent(rule.type().getCode(), rule);
        }
        this.commonService = commonService;
        this.rollbackService = rollbackService;
        this.cloudRedisService = cloudRedisService;
        this.diveService = diveService;
    }

    /**
     * 用户下注
     * <p>
     * 1.校验赛事是否存在
     * 不存在->提示错误
     * 存在->2
     * 2.校验赛事是否已经开赛/或在开赛15分钟之前
     * 已经开赛/距离开赛15分钟->提示不可以下注
     * 未开赛/距离开赛大于15分钟->3
     * 3.校验金额是否合法
     * <p>
     * 4.校验每笔订单的下单金额和可下单金额关系
     * 金额>可下单金额->提示金额错误
     * 其他->5
     * 5.校验下单总金额和用户可用余额的关系
     * 金额>可用余额->提示 余额不足
     * 其他->6
     * 6.扣款-生成订单-个人&商户积分变动  扣款和生成订单积分变动是一个事务
     * <p>
     * 7.下注只会出现成功下注单，扣款失败 不会产生订单
     *
     *
     * </p>
     *
     * @param userId 用户ID
     * @param betRequestDto   下注内容
     * @return
     */
    @Override
    public Results.Result<UnbetReasonDto> add(Integer userId, BetRequestDto betRequestDto) {
        Results.Result<UnbetReasonDto> addBetResult = addBetRecord(userId, betRequestDto);

        if (String.valueOf(addBetResult.getCode()).equals(String.valueOf(Results.SUCCESS.getCode()))) {
            autoRebateDive(null, null, TimeRangeEnum.inner.getCode());//时间范围内自动跳水
            autoRebateDive(null, null, TimeRangeEnum.out.getCode());//范围外自动跳水
        }
        return addBetResult;
    }

    @Transactional
    protected Results.Result<UnbetReasonDto> addBetRecord(Integer userId, BetRequestDto betRequestDto) {
        if (betRequestDto == null || betRequestDto.getBetRequests().isEmpty()) {
            return Results.PARAMETER_INCORRENT;
        }
        List<BetRequest> bets = betRequestDto.getBetRequests();
        LocalDateTime currentTime = LocalDateTime.now();
        final BigDecimal[] totalAmount = {new BigDecimal(0)};
        bets.forEach(bet -> {
            totalAmount[0] = totalAmount[0].add(bet.getAmount());
        });
        UserBalanceInfo balanceInfo = userBalanceInfoMapper.selectByPrimaryKey(userId);
        if (balanceInfo == null) {
            return new Results.Result<>(Results.Result.NOT_FIND, "用户账户无效");
        }
        BigDecimal amount = balanceInfo.getAmount();
        if (totalAmount[0].compareTo(amount) > 0) {
            return new Results.Result<>(Results.Result.SYSTEM_BUSY, "账户余额不足");
        }
        //生成积分变动
        //查找商户
        UserInfoExample userInfoExample = new UserInfoExample();
        userInfoExample.or().andProxyEqualTo(ProxyEnum.sys_merchant.getCode());
        List<UserInfo> userInfos = userInfoMapper.selectByExampleWithRowbounds(userInfoExample, new RowBounds(0, 1));
        if (userInfos.isEmpty()) {
            rlog.error("no system merchant existed");
            throw new SacException("交易失败,不存在商户");
        }
        UserInfo userInfo = userInfos.get(0);
        Integer merchantId = userInfo.getUserId();//平台商户ID

        UnbetReasonDto reasonDto = new UnbetReasonDto();
        List<UnbetReason> unbets = Lists.newLinkedList();
        List<UnbetRebate> unbetRebates = new ArrayList<>();
        List<String> raceIds = Lists.newLinkedList();
        bets.forEach(bet -> {
            UnbetReason unbet = new UnbetReason();
            String raceId = bet.getRaceId();
            RaceInfo raceInfo = raceInfoMapper.selectByPrimaryKey(raceId);
            if (raceInfo == null) {
                unbet.setId(bet.getRebateId());
                unbet.setReason("赛事" + raceId + "不存在");
                unbets.add(unbet);
            }
            LocalDateTime startTime = raceInfo.getStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            if (currentTime.isAfter(startTime) ) {//|| currentTime.plusMinutes(betLimitTime).isAfter(startTime)) {
                unbet.setId(bet.getRebateId());
                unbet.setReason("赛事已经开赛, 不许投注");
                unbets.add(unbet);
            }
            BigDecimal betAmount = bet.getAmount();
            if (betAmount.compareTo(BigDecimal.ZERO) <= 0 || betAmount.scale() >= 2) {
                unbet.setId(bet.getRebateId());
                unbet.setReason("投注金额不合法");
                unbets.add(unbet);
            }
            RaceRebateInfo rebateInfo = raceRebateInfoMapper.selectByPrimaryKey(bet.getRebateId());
            if (rebateInfo == null) {
                unbet.setId(bet.getRebateId());
                unbet.setReason("暂无此返利率");
                unbets.add(unbet);
            }
            if (betAmount.compareTo(rebateInfo.getValidAmount()) > 0) {
                unbet.setId(bet.getRebateId());
                unbet.setReason("投注金额大于可下单量");
                unbets.add(unbet);
            }
            String rule = rebateInfo.getRule();
            ScoreRule scoreRule = (ScoreRule) ruleMap.get(rebateInfo.getRuleType()).serialize(rule);

            if (bet.getRebateRatio().compareTo(new BigDecimal(scoreRule.getRebateRatio())) != 0) {
                String info = "赛事:"
                        + raceInfo.getHomeTeam() + "vs" + raceInfo.getVisitTeam()
                        + "  "
                        + scoreRule.getScore()
                        + " "
                        + "赔率已经由"
                        + bet.getRebateRatio().multiply(BigDecimal.valueOf(100)).toString()
                        + "%"
                        + "->"
                        + new BigDecimal(scoreRule.getRebateRatio()).multiply(BigDecimal.valueOf(100)).toString()
                        + "%";
                UnbetRebate build = UnbetRebate.builder()
                        .id(bet.getRebateId())
                        .ratio(new BigDecimal(scoreRule.getRebateRatio()))
                        .info(info).build();
                unbetRebates.add(build);
            }
        });

        if (unbets.size() > 0 || unbetRebates.size() > 0) {
            reasonDto.setInfo(Results.BetFailed.getMsg());
            reasonDto.setUnbets(unbets);
            reasonDto.setUnbetRebates(unbetRebates);
            return new Results.Result<>(Results.BetFailed, reasonDto);
        }
        bets.forEach(bet -> {
            String raceId = bet.getRaceId();
            BigDecimal betAmount = bet.getAmount();
            RaceRebateInfo rebateInfo = raceRebateInfoMapper.selectByPrimaryKey(bet.getRebateId());
            RaceInfo raceInfo = raceInfoMapper.selectByPrimaryKey(raceId);
            String category = raceInfo.getCategory();
            Date startTime = raceInfo.getStartTime();

            //生成下注单
            BetRecd betRecd = new BetRecd();
            Long betId = unique.nextId();
            betRecd.setId(betId);
            betRecd.setUserId(userId);
            betRecd.setRaceId(bet.getRaceId());

            betRecd.setBetType(BetTypeEnum.score.getCode());
            ScoreRule rule = (ScoreRule) ruleMap.get(rebateInfo.getRuleType()).serialize(rebateInfo.getRule());
            BetScoreDto build = BetScoreDto.builder()
                    .createTime(new Date())
                    .expectRebateAmount(bet.getAmount().multiply(new BigDecimal(rule.getRebateRatio())).setScale(2, BigDecimal.ROUND_HALF_UP))
                    .raceId(raceId)
                    .rebateId(rebateInfo.getId())
                    .rebateRatio(new BigDecimal(rule.getRebateRatio()))
                    .score(rule.getScore())
                    .userId(userId)
                    .teams(rule.getTeams())
                    .expecAmount(bet.getAmount().add(bet.getAmount().multiply(new BigDecimal(rule.getRebateRatio())).setScale(2, BigDecimal.ROUND_HALF_UP)))
                    .betId(betId.toString())
                    .betAmount(betAmount)
                    .category(category)
                    .startTime(startTime)
                    .build();

            betRecd.setBetContent(JSON.toJSONString(build));
            betRecd.setBetAmount(bet.getAmount());
            betRecd.setCreateTime(new Date());
            betRecd.setBetResult(null);
            betRecd.setRebateStatus(RebateStatusEnum.un_rebate.getCode());
            betRecd.setVersion(0);
            betRecd.setRebateId(rebateInfo.getId());
            betRecdMapper.insertSelective(betRecd);
            //扣除可下单量
            int subValidAmount = raceRebateInfoMapperEx.subValidAmount(rebateInfo.getId(), betAmount);
            if (subValidAmount != 1) {
                throw new SacException("投注失败,投注金额大于可下单量");
            }
            //个人扣款
            int cutBetAmount = userBalanceInfoMapperEx.cutBetAmount(userId, betAmount);
            if (cutBetAmount != 1) {
                throw new SacException("投注失败,扣款失败");
            }
            //商户加款
            int addSysBetAmount = userBalanceInfoMapperEx.addSysBetAmount(merchantId, betAmount);
            if (addSysBetAmount != 1) {
                throw new SacException("投注失败,添加商户余额失败");
            }

            //插入商户积分变动
            commonService.insertUserBalanceChange(betId, betAmount, SubOrAddEnum.Add, merchantId);
            //插入个人积分变动
            commonService.insertUserBalanceChange(betId, betAmount, SubOrAddEnum.Sub, userId);
            CompletableFuture.runAsync(() -> {
                redisTemplate.convertAndSend("autodive", raceId + "-" + rebateInfo.getId());
            });

            DiveAmount diveAmount = DiveAmount.builder()
                    .changeAmount(betAmount)
                    .build();
            String key = RedisKeys.EVENT_DIVE + raceId + ":" + rebateInfo.getId();
            long expireTime = new Date().toInstant().atZone(ZoneId.systemDefault()).plusDays(7).toInstant().toEpochMilli();
            Set keys = redisTemplate.keys( key);
            List<JSONObject> jsonList = redisTemplate.opsForValue().multiGet(keys);
            if (jsonList == null || jsonList.isEmpty()) {
                cloudRedisService.save(key, diveAmount, expireTime);
            } else {
                JSONObject json = jsonList.get(0);
                DiveAmount lastDive = JSON.parseObject(JSON.toJSONString(json), DiveAmount.class);
                lastDive.setChangeAmount(lastDive.getChangeAmount().add(betAmount));
                cloudRedisService.updateOutTime(key, lastDive, expireTime); // 更新上次总交易额
            }
        });
        return new Results.Result<>(Results.SUCCESS, null);
    }

    @Override
    @Transactional
    public Results.Result<String> cancellations(Long userId,Long betId){
        UserBalanceInfo balanceInfo = userBalanceInfoMapper.selectByPrimaryKey(userId.intValue());
        if (balanceInfo == null) {
            return new Results.Result<>(Results.Result.NOT_FIND, "用户账户无效");
        }
        BetRecd betRecd = new BetRecd();
        betRecd = betRecdMapper.selectByPrimaryKey(betId);
        String raceId = betRecd.getRaceId();
        RaceInfo raceInfo = raceInfoMapper.selectByPrimaryKey(raceId);
        if(raceInfo==null){
            return new Results.Result<>(Results.Result.BetCancelFailed, "无此赛事！", null);
        }
        //只能撤注三分钟内投注
        String timeStr = TimeUtil.formatTimeToAssign(null,"min",-3,null);
        try {
            Date dateTemp = format.parse(timeStr);
            if(dateTemp.after(betRecd.getCreateTime())){
                return new Results.Result<>(Results.Result.BetCancelFailed, "下注三分钟后不可撤单！", null);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(raceInfo.getStartTime().before(new Date())){
            return new Results.Result<>(Results.Result.BetCancelFailed, "已开赛，不可撤注！", null);
        }
        if(betRecd==null){
            return new Results.Result<>(Results.Result.BetCancelFailed, "订单不存在！", null);
        }
        if(betRecd.getBetStatus()!=BetTypeEnum.bet_normal.getCode()||betRecd.getRebateStatus()==RebateStatusEnum.success.getCode()){
            return new Results.Result<>(Results.Result.BetCancelFailed, "订单已撤注！", null);
        }
        String tempUserId = userId+"";
        //从缓存中获取三分钟内取消次数
        Integer count = cloudRedisService.select(RedisKeys.BET_CANCELLATIONS_MIN + tempUserId, Integer.class);
        if(count==null||count<5){
            updateCancellationsTimes(count,tempUserId,cancelTime * 60L,"MIN");
        }else{
            return new Results.Result<>(Results.Result.BetCancelFailed, "三分钟内最多撤单五次,请稍后重试!", null);
        }
        //获取截至一天内取消次数
        count = cloudRedisService.select(RedisKeys.BET_CANCELLATIONS_DAY + tempUserId, Integer.class);
        if(count==null||count<10){
            Long time = TimeUtil.getSpecificTime();
            updateCancellationsTimes(count,tempUserId,time * 60L,"DAY");
        }else{
            return new Results.Result<>(Results.Result.BetCancelFailed, "一天内最多撤单十次,今日取消投注已达上限!", null);
        }
        //取消投注单，将投注金额返还
        boolean cancelResult = cancelBet(userId,betId);
        if(!cancelResult){
            return new Results.Result<>(Results.SUCCESS, "订单不存在或已撤注！");
        }

        return new Results.Result<>(Results.SUCCESS, "撤单成功！");
    }

    /**
     * 取消指定投注单，并将投注金额返还
     * @param userId 用户ID
     * @param betId 投注ID
     * @return
     */
    private boolean cancelBet(Long userId,Long betId){
        //生成积分变动
        //查找商户
        UserInfoExample userInfoExample = new UserInfoExample();
        userInfoExample.or().andProxyEqualTo(ProxyEnum.sys_merchant.getCode());
        List<UserInfo> userInfos = userInfoMapper.selectByExampleWithRowbounds(userInfoExample, new RowBounds(0, 1));
        if (userInfos.isEmpty()) {
            rlog.error("no system merchant existed");
            throw new SacException("交易失败,不存在商户");
        }
        UserInfo userInfo = userInfos.get(0);
        Integer merchantId = userInfo.getUserId();//平台商户ID
        //查找出对应下注单信息
        BetRecd betRecd = new BetRecd();
        betRecd = betRecdMapper.selectByPrimaryKey(betId);

        BigDecimal betAmount = betRecd.getBetAmount();
        //生成撤注单
        Long takenBetId = unique.nextId();
        //设置撤单ID
        betRecd.setId(takenBetId);
        betRecd.setRebateStatus(RebateStatusEnum.success.getCode());
        //设置投注状态
        betRecd.setBetStatus(BetTypeEnum.user_cancel.getCode());
        //记录关联投注ID
        betRecd.setRelevanceId(betId);
        //插入撤注单
        //betRecdMapper.insertSelective(betRecd);
        Integer betType = betRecd.getBetType();
        //从JSON中找出返利ID
        BetScoreDto serialize = (BetScoreDto)strategyMap.get(betType).serialize(betRecd.getBetContent());
        //返利ID
        Integer rebateId = serialize.getRebateId();
        //增加可下单量
        int addValidAmount = raceRebateInfoMapperEx.addValidAmount(rebateId, betAmount);
        if (addValidAmount != 1) {
            throw new SacException("可下单量返还失败，撤单失败");
        }
        //返还用户投注额，用户加钱
        int cutBetAmount = userBalanceInfoMapperEx.addRebateAmount(userId.intValue(), betAmount);
        if (cutBetAmount != 1) {
            throw new SacException("返还投注额失败,撤注失败");
        }
        //商户减款
        int addSysBetAmount = userBalanceInfoMapperEx.cutBetAmount(merchantId, betAmount);
        if (addSysBetAmount != 1) {
            throw new SacException("撤注,添加商户余额失败");
        }

        //原始投注单状态改变
        BetRecd betRecd2 = new BetRecd();
        betRecd2.setRebateStatus(RebateStatusEnum.success.getCode());
        betRecd2.setBetStatus(BetTypeEnum.user_cancel.getCode());
        BetRecdExample example = new BetRecdExample();
        example.or()
                .andIdEqualTo(Long.valueOf(betId))
                .andRebateStatusEqualTo(RebateStatusEnum.un_rebate.getCode())
                .andBetStatusEqualTo(BetTypeEnum.bet_normal.getCode());
        int updateStatus = betRecdMapper.updateByExampleSelective(betRecd2, example);
        if (updateStatus != 1) {
            throw new RuntimeException("update bet rebate status got error");
        }

        //插入商户积分变动
        commonService.insertUserBalanceChange(betId, betAmount, SubOrAddEnum.Sub, merchantId,OptTypeEnum.cancel_user.getCode());
        //插入个人积分变动
        commonService.insertUserBalanceChange(betId, betAmount, SubOrAddEnum.Add, userId.intValue(),OptTypeEnum.cancel_user.getCode());


        /*CompletableFuture.runAsync(() -> {
            redisTemplate.convertAndSend("autodive", JSON.toJSONString(AutoDiveMessage.builder().raceId(serialize.getRaceId()).rebateId(rebateId).build()));
        });*/

        return true;
    }

    /**
     * 更新缓存中取消投注次数
     * @param count
     * @param userId
     * @param time
     * @param type
     */
    private void updateCancellationsTimes(Integer count, String userId,Long time,String type) {
        switch (type){
            case "MIN":
                if (count == null) {
                    cloudRedisService.save(RedisKeys.BET_CANCELLATIONS_MIN + userId, 0, time);
                } else {
                    count++;
                    Long ttl = cloudRedisService.ttl(RedisKeys.BET_CANCELLATIONS_MIN + userId);
                    cloudRedisService.updateOutTime(RedisKeys.BET_CANCELLATIONS_MIN + userId, count, ttl);
                }
                break;
            case "DAY":
                if (count == null) {
                    cloudRedisService.save(RedisKeys.BET_CANCELLATIONS_DAY + userId, 0, time);
                } else {
                    count++;
                    Long ttl = cloudRedisService.ttl(RedisKeys.BET_CANCELLATIONS_DAY + userId);
                    cloudRedisService.updateOutTime(RedisKeys.BET_CANCELLATIONS_DAY + userId, count, ttl);
                }
                break;
        }
    }
    @Override
    public Results.Result<AdminBetRecdInfo<BetRecdDto>> list(Integer userId, Integer type, Integer dateType, Integer curPage, Integer pageSize,String betResult,String rebateStatus,String betStatus) {
        //交易记录列表
        Date startTime = Date.from(LocalDateTime.now().toInstant(ZoneOffset.ofHours(8)));
        final Date endTime = startTime;//默认结束时间是当前时间
        switch (DateTypeEnum.codeOf(dateType)) {
            case Week:
                try{
                    startTime = format.parse(TimeUtil.formatTimeToAssign(null,"d",-7,null));
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case month:
                try{
                    startTime = format.parse(TimeUtil.formatTimeToAssign(null,"m",-1,null));
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case year:
                try{
                    startTime = format.parse(TimeUtil.formatTimeToAssign(null,"y",-1,null));
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case all:
                try{
                    startTime = format.parse(TimeUtil.formatTimeToAssign(null,"y",-100,null));
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }
        Date finalStartTime = startTime;
        PageInfo<BetRecd> pageInfo = PageHelper.startPage(curPage, pageSize).doSelectPageInfo(() -> {
            BetRecdExample example = new BetRecdExample();
            BetRecdExample.Criteria criteria1 = example.createCriteria();
            criteria1.andUserIdEqualTo(userId).andBetTypeEqualTo(type)
                    .andCreateTimeBetween(finalStartTime, endTime);
            //投注结果
            if(betResult!=null&&!"".equals(betResult)&&!"null".equals(betResult)){
                criteria1.andBetResultEqualTo(Integer.parseInt(betResult));
            }
            //返利结果
            if(rebateStatus!=null&&!"".equals(rebateStatus)&&!"null".equals(rebateStatus)){
                criteria1.andRebateStatusEqualTo(Integer.parseInt(rebateStatus));
            }
            //投注状态
            if(betStatus!=null&&!"".equals(betStatus)&&!"null".equals(betStatus)){
                criteria1.andBetStatusEqualTo(Integer.parseInt(betStatus));
            }
            //按时间降序
            example.setOrderByClause("create_time desc");
            betRecdMapper.selectByExample(example);
        });
        if (pageInfo.getList().isEmpty()) {
            return new Results.Result<>(Results.Result.SUCCESS, null);
        }
        List<BetRecd> list = pageInfo.getList();
        List<BetRecdDto<Object>> collect = list.stream().map(recd -> {
            String betContent = recd.getBetContent();
            Integer betType = recd.getBetType();
            Object serialize = strategyMap.get(betType.intValue()).serialize(betContent);
            BetRecdDto<Object> build = BetRecdDto.builder()
                    .betResult(recd.getBetResult())
                    .betType(recd.getBetType())
                    .content(betContent)
                    .createTime(recd.getCreateTime())
                    .id(recd.getId().toString())
                    .raceId(recd.getRaceId())
                    .rebateStatus(recd.getRebateStatus())
                    .userId(recd.getUserId())
                    .betStatus(recd.getBetStatus())
                    .json(serialize).build();
            return build;
        }).collect(Collectors.toList());
        PageInfo results = new PageInfo();
        BeanUtils.copyProperties(pageInfo, results, "list");
        results.setList(collect);
        AdminBetRecdInfo recdInfo = betRecdMapperEx.userSumBetRecd(userId, type, finalStartTime, endTime);
        recdInfo.setPage(results);
        return new Results.Result<>(Results.SUCCESS, recdInfo);
    }



    /**
     * 1.查询所有已经结束的赛事
     * 2.查询该赛事还是返利的订单
     * 3.返利
     *
     * @return
     */
    @Override
    public Results.Result<Void> settle() {
        RaceInfoExample raceExample = new RaceInfoExample();
        raceExample.or().andEndTimeIsNotNull().andRaceStatusEqualTo(RaceStatusEnum.end.getCode());
        List<RaceInfo> races = raceInfoMapper.selectByExample(raceExample);
        if (races.isEmpty()) {
            return new Results.Result<>(Results.SUCCESS, null);
        }
        races.forEach(raceInfo -> {
            //查询每场赛事的未返利投注记录
            BetRecdExample example = new BetRecdExample();
            example.or().andRaceIdEqualTo(raceInfo.getId()).andRebateStatusEqualTo(RebateStatusEnum.un_rebate.getCode())
                    .andBetStatusEqualTo(BetTypeEnum.bet_normal.getCode());
            Integer curPage = 1;
            Integer pageSize = 200;
            List<BetRecd> recds = null;
            while (true) {
                PageInfo<BetRecd> pageInfo = PageHelper.startPage(curPage, pageSize).doSelectPageInfo(() -> {
                    betRecdMapper.selectByExample(example);
                });
                if (pageInfo.getList().isEmpty()) {
                    break;
                }
                List<BetRecd> betRecds = pageInfo.getList();
                betRecds.forEach(betRecd -> {
                    //结算
                    try {
                        strategyMap.get(betRecd.getBetType()).settle(betRecd.getBetContent());
                    } catch (Exception e) {
                        log.error("bet settle for " + betRecd.getId() + "get error", e);
                    }
                });
                curPage++;
            }

        });
        return new Results.Result<>(Results.SUCCESS, null);
    }

    /**
     * 1.搜索所有已开始赛事，将赛事ID作为KEY，赛事比分作为VALUE存入到redis
     * 2.当赛事比分不为零时，将赛事比分与redis缓存中的值进行对比，如果不一致则将赛事进行结算
     * 3.将需要结算的赛事投注内容按照规则进行对比，并结算对应数据
     *
     * @return
     */
    @Override
    public Results.Result<Void> newSettle() {
        RaceInfoExample raceExample = new RaceInfoExample();
        Date startTime = new Date();
        try {
            //设置获取十分钟前的时间
            startTime = format.parse(TimeUtil.formatTimeToAssign(startTime, "min", -10, null));
        } catch (Exception e) {
            e.printStackTrace();
        }
        //条件：赛事比分不为空，赛事状态为开始，比分更新后十分钟，赛事结束时间为空
        raceExample.or().andWinResultIsNotNull()
                .andRaceStatusEqualTo(RaceStatusEnum.processing.getCode())
                //     .andUpdateTimeLessThanOrEqualTo(startTime)
                .andEndTimeIsNull();
        List<RaceInfo> races = raceInfoMapper.selectByExample(raceExample);
        if (races.isEmpty()) {
            return new Results.Result<>(Results.SUCCESS, null);
        }
        for (RaceInfo raceInfo : races) {
            //更新时间少于10分钟则不进入结算
            if (raceInfo.getUpdateTime().after(startTime)) {
                continue;
            }
            //从redis中取出对应赛事比分信息，没有就存入
            //String score = cloudRedisService.select(RedisKeys.RACE_SCORE + raceInfo.getId(), String.class);
            //cloudRedisService.save(RedisKeys.RACE_SCORE + raceInfo.getId(), raceInfo.getWinResult(), times * 60L);
            //查询每场赛事的未返利投注记录
            BetRecdExample example = new BetRecdExample();
            example.or().andRaceIdEqualTo(raceInfo.getId())
                    .andRebateStatusEqualTo(RebateStatusEnum.un_rebate.getCode())
                    .andBetStatusEqualTo(BetTypeEnum.bet_normal.getCode());
            Integer curPage = 1;
            Integer pageSize = 200;
            while (true) {
                PageInfo<BetRecd> pageInfo = PageHelper.startPage(curPage, pageSize).doSelectPageInfo(() -> {
                    betRecdMapper.selectByExample(example);
                });
                if (pageInfo.getList().isEmpty()) {
                    break;
                }
                List<BetRecd> betRecds = pageInfo.getList();
                betRecds.forEach(betRecd -> {
                    //结算
                    try {
                        //调用结算方法，传入投注信息及赛事比分
                        strategyMap.get(betRecd.getBetType()).newSettle(betRecd.getBetContent(), raceInfo.getWinResult().trim());
                    } catch (Exception e) {
                        log.error("bet settle for " + betRecd.getId() + "get error", e);
                    }
                });
                curPage++;
            }

        }
        return new Results.Result<>(Results.SUCCESS, null);
    }

    @Override
    @Transactional
    public Results.Result<Void> cancelRaceInfo(String raceId) {
        if(!StringUtil.isEmpty(raceId)){
            RaceInfo bean = raceInfoMapper.selectByPrimaryKey(raceId);
            if(bean==null){
                return new Results.Result<>(Results.PARAMETER_INCORRENT, null);
            }
            RaceInfo raceInfo = new RaceInfo();
            raceInfo.setId(raceId);
            raceInfo.setRaceStatus(RaceStatusEnum.cancel.getCode());
            int updateRaceStatus = raceInfoMapper.updateByPrimaryKeySelective(raceInfo);
            if(updateRaceStatus!=1){
                //throw new SacException("取消赛事失败，请重新操作");
                return new Results.Result<>(Results.SYSTEM_BUSY, null);
            }
        }
        return new Results.Result<>(Results.SUCCESS, null);
    }
    @Override
    @Transactional
    public Results.Result<Void> rollbackForCancelRace(String raceId) {
        //当数据库中有取消赛事时
        if (!StringUtil.isEmpty(raceId)) {
            rollbackService.rollbackForCancelRace(raceId);
        }
        return new Results.Result<>(Results.SUCCESS, null);
    }


    @Override
    public Results.Result<AdminBetRecdInfo> adminList(String betId, String phone, String account, String raceId, String score, Integer betType, Integer betResult, Integer rebateStatus, Long startTime, Long endTime, Integer curPage, Integer pageSize) {
        Date startDate = null;
        Date endDate = null;
        if (startTime != null) {
            startDate = new Date(startTime);
        }
        if (endTime != null) {
            endDate = new Date(endTime);
        }
        Date finalStartDate = startDate;
        Date finalEndDate = endDate;
        PageInfo<AdminBetRecdDto> pageInfo = PageHelper.startPage(curPage, pageSize).doSelectPageInfo(() -> {
            List<AdminBetRecdDto> dtos = betRecdMapperEx.adminList(betId, phone, account, raceId, score, betType, betResult, rebateStatus, finalStartDate, finalEndDate);
            dtos.forEach(recd -> {
                String content = recd.getContent();
                Object serialize = strategyMap.get(recd.getBetType()).serialize(content);
                recd.setJson(serialize);
            });
        });
        AdminBetRecdInfo recdInfo = betRecdMapperEx.sumBetRecd(betId, phone, account, raceId, score, betType, betResult, rebateStatus, finalStartDate, finalEndDate);
        recdInfo.setPage(pageInfo);
        return new Results.Result<>(Results.SUCCESS, recdInfo);
    }

    public Results.Result<List<RebateBetInfo>> rebateBetInfo(List<Integer> rebateIds) {
        if (rebateIds.isEmpty()) {
            return new Results.Result<>(Results.PARAMETER_INCORRENT, null);
        }
        List<RebateBetInfo> betInfo = betRecdMapperEx.getRebateBetInfo(rebateIds);
        return new Results.Result<>(Results.SUCCESS, betInfo);
    }

    public Results.Result<Void> selectAllCancelRaceAndRollback(){
        //找出所有取消赛事
        RaceInfoExample exampleRace = new RaceInfoExample();
        exampleRace.or().andRaceStatusEqualTo(RaceStatusEnum.cancel.getCode());
        List<RaceInfo> listRace = raceInfoMapper.selectByExample(exampleRace);
        //当数据库中有取消赛事时
        if(listRace.size()>0){
            for(RaceInfo race:listRace){
                rollbackService.rollbackForCancelRace(race.getId());
            }
        }
        return new Results.Result<>(Results.SUCCESS, null);
    }


    private Results.Result<Void> autoRebateDive(String raceId, Integer rebateId, Integer timeRange) {
        //加载所有的跳水规则
        DiveRuleExample diveRuleExample = new DiveRuleExample();
        diveRuleExample.or().andEnableStatusEqualTo(SysStatusEnum.VALID1.ordinal()).andSysStatusEqualTo(SysStatusEnum.VALID1.ordinal()).andTimeRangeEqualTo(timeRange);
        List<DiveRule> rules = diveRuleMapper.selectByExample(diveRuleExample);
        if (rules.isEmpty()) {
            log.warn("no any rules is opened ");
            return new Results.Result<>(Results.SUCCESS, null);
        }

        //查询待开赛的所有的赛事
        LocalDateTime now = LocalDateTime.now();
        RaceInfoExample raceInfoExample = new RaceInfoExample();
        RaceInfoExample.Criteria criteria = raceInfoExample.or().andStartTimeGreaterThan(Date.from(now.toInstant(ZoneOffset.of("+8"))));
        if (raceId != null) {
            criteria.andIdEqualTo(raceId);
        }
        List<RaceInfo> waitRaces = raceInfoMapper.selectByExample(raceInfoExample);
        if (waitRaces.isEmpty()) {
            log.warn("no any race is opened or wait for dive");
            return new Results.Result<>(Results.SUCCESS, null);
        }
        //遍历赛事的投注
        for (RaceInfo race : waitRaces) {
            RaceRebateInfoExample raceRebateInfoExample = new RaceRebateInfoExample();
            RaceRebateInfoExample.Criteria rebateCriteria = raceRebateInfoExample.or().andRaceIdEqualTo(race.getId());
            if (rebateId != null) {
                rebateCriteria.andIdEqualTo(rebateId);
            }
            List<RaceRebateInfo> rebateInfos = raceRebateInfoMapper.selectByExample(raceRebateInfoExample);
            if (rebateInfos.isEmpty()) {
                continue;
            }
            LocalDateTime startTime = race.getStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            long duration = Duration.between(now, startTime).toHours();

            List<Integer> rebateIds = rebateInfos.stream().map(RaceRebateInfo::getId).collect(Collectors.toList());
            try {
                List<RebateBetInfo> betRebate = rebateBetInfo(rebateIds).getResult();

                for (RebateBetInfo betInfo : betRebate) {
                    Boolean flag = changeRebate(rules, race, duration, betInfo, timeRange);
                    while (flag) {
                        flag = changeRebate(rules, race, duration, betInfo, timeRange);
                    }
                }
            } catch (Exception e) {
                log.error("auto dive got error,can not get any bet amount", e);
                continue;
            }
        }
        return new Results.Result<>(Results.SUCCESS, null);
    }

    @Transactional
    protected Boolean changeRebate(List<DiveRule> rules, RaceInfo race, long duration, RebateBetInfo betInfo, Integer timeRange) {
        Boolean flag = false;
        //遍历是否满足任何一个要求
        //使用二分查找算法 查找到规则

        //跳水返利率返利率排序
        RaceRebateInfo rebateInfo = raceRebateInfoMapper.selectByPrimaryKey(betInfo.getRebateId());
        List<BigDecimal> rebateRatios =
                rules.stream().map(DiveRule::getStartRebate).distinct().sorted((o1, o2) -> o1.compareTo(o2)).collect(Collectors.toList());

        List<BigDecimal> endRebates = rules.stream().map(DiveRule::getEndRebate).distinct().sorted((o1, o2) -> o1.compareTo(o2)).collect(Collectors.toList());
        rebateRatios.addAll(endRebates);
        List<BigDecimal> totalRatios = rebateRatios.stream().distinct().sorted((o1, o2) -> o1.compareTo(o2)).collect(Collectors.toList());
        //使用二分查找
        BigDecimal[] rebates = new BigDecimal[rebateRatios.size()];
        BigDecimal checkRatio = new BigDecimal(JSON.parseObject(rebateInfo.getRule(), ScoreRule.class).getRebateRatio());
        for (int i=0; i<totalRatios.size(); i++) {
            BigDecimal ratio = totalRatios.get(i);
            if (ratio.compareTo(checkRatio) == 0 && i > 1) {
                checkRatio = totalRatios.get(i-2); //说明这个返利率正好是规则的开始利率，此时用小于开始利率的利率去界定规则范围
                break;
            }
        }

        int rebateLocation = recursionBinarySearch(totalRatios.toArray(rebates), checkRatio, 0, rebates.length - 1);
        if (rebateLocation == -1) {//amountLocation == -1 ||
            log.error("can not find any meet rule");
            return false;
        }
        BigDecimal rebate = rebates[rebateLocation];
//                    log.info("meet rule amount:{}", betAmount);
        log.info("meet rule ratio:{}", rebate);
        //查找出此规则
        DiveRuleExample ruleExample = new DiveRuleExample();
        ruleExample.or().andStartRebateEqualTo(rebate).andEnableStatusEqualTo(SysStatusEnum.VALID1.ordinal())
                .andSysStatusEqualTo(SysStatusEnum.VALID1.ordinal()).andTimeRangeEqualTo(timeRange);
        ;//.andStartAmountEqualTo(amount)
        ruleExample.setOrderByClause("start_rebate desc ");
        List<DiveRule> diveRules = diveRuleMapper.selectByExample(ruleExample);
        if (diveRules.isEmpty()) {
            log.error("can not find any meet rule");
            return false;
        }

        for (DiveRule rule : diveRules) {
            //判断开赛时间是否满足
            BigDecimal autochangeTime = rule.getAutochangeTime();
            if (duration > autochangeTime.intValue() && !timeRange.toString().equals(TimeRangeEnum.out.getCode().toString())) {
                log.error("autochange Time not meet");
                continue;
            }
            if (duration <= autochangeTime.intValue() && !timeRange.toString().equals(TimeRangeEnum.inner.getCode().toString())) {
                log.error("autochange Time not meet");
                continue;
            }
            //修改返利率
            log.info("now going to update rebate ratio rebateId:{}, updateUp:{},for dive rule:{}", betInfo.getRebateId(), rule.getIncrease(), rule.getId());

            /*
             *  1. 查询 Redis 中 subBetAmount 是否存在，存在 ？ ->2
             *  2. 查出 rule 中的金额 startAmount 与 betAmount 的变化额 比较
             *      betAmount 变化额 > startAmount ?  ->3 : return
             *  3. 调整利率, 判断是否触底关盘返利率, 触底 ？ 关闭投注状态
             *  4. 将 betAmount - startAmount 存入 Redis
             */
            String key = RedisKeys.EVENT_DIVE + race.getId() + ":" + betInfo.getRebateId();
            long expireTime = new Date().toInstant().atZone(ZoneId.systemDefault()).plusDays(7).toInstant().toEpochMilli();
            Set keys = redisTemplate.keys( RedisKeys.EVENT_DIVE + race.getId() + ":" + betInfo.getRebateId());
            List<JSONObject> jsonList = redisTemplate.opsForValue().multiGet(keys);
            if (jsonList == null || jsonList.isEmpty()) continue;

            JSONObject json = jsonList.get(0);
            DiveAmount lastDive = JSON.parseObject(JSON.toJSONString(json), DiveAmount.class);
            BigDecimal lastBetAmount = lastDive.getChangeAmount();
            int count = (int)(lastBetAmount.doubleValue() / rule.getStartAmount().doubleValue());
            if (count < 1) {
                continue;
            }
            BigDecimal currentRatio = new BigDecimal(JSON.parseObject(rebateInfo.getRule(), ScoreRule.class).getRebateRatio());
            Double countTemp = currentRatio.subtract(rule.getStartRebate()).setScale(4, RoundingMode.HALF_UP).divide(rule.getIncrease(),4, RoundingMode.UP).doubleValue();
            int intTemp = (int)(countTemp * 10) / 10;
            int ratioCount = ((int)(countTemp * 10)) % 10 == 0 ? intTemp : intTemp+1;
            if (ratioCount < count) {
                count = ratioCount;
            }
            for (int index = count; index > 0; index--) {
                BigDecimal temp = lastBetAmount.subtract(rule.getStartAmount());
                if (lastBetAmount.compareTo(rule.getStartAmount()) >= 0) {
                    lastBetAmount = temp;
                }
            }
            DiveAmount diveAmount = DiveAmount.builder()
                    .changeAmount(lastBetAmount)
                    .build();

            BigDecimal newRebate = new BigDecimal(count * rule.getIncrease().doubleValue()).setScale(4, RoundingMode.HALF_UP);
            newRebate = currentRatio.subtract(newRebate).setScale(4, RoundingMode.HALF_UP);
            if (newRebate.compareTo(rule.getShutDownRebate()) <= 0) {
                //此返利可投注状态为关闭
                RaceRebateInfo raceRebateInfo = new RaceRebateInfo();
                raceRebateInfo.setId(betInfo.getRebateId());
                raceRebateInfo.setOpenStatus(SysStatusEnum.INVALID0.ordinal());
                raceRebateInfoMapper.updateByPrimaryKeySelective(raceRebateInfo);
                raceRebateInfoMapperEx.updateDive(betInfo.getRebateId(), rule.getShutDownRebate(), null);
                flag = false;
            } else {
                if (newRebate.compareTo(rule.getStartRebate()) == -1) {
                    newRebate = rule.getStartRebate();
                    flag = true;
                } else {
                    flag = false;
                }
                raceRebateInfoMapperEx.updateDive(betInfo.getRebateId(), newRebate, null);
            }

            cloudRedisService.updateOutTime(key, diveAmount, expireTime); // 更新上次总交易额
        }
        return flag;
    }


    private int recursionBinarySearch(BigDecimal[] array, BigDecimal key, int low, int high) {

        if (key.compareTo(array[low]) < 0 || low > high) {
            return -1;
        }
        if (key.compareTo(array[high]) >= 0 && array.length == 1) {
            return 0;
        }
        if (key.compareTo(array[low]) < 0) {
            return -1;
        }
        if (high - low <= 1) {
            return low;
        }
        int middle = (high + low) / 2;
        if (key.compareTo(array[middle]) < 0) {
            //取左边比较
            return recursionBinarySearch(array, key, low, middle);
        } else if (key.compareTo(array[middle]) > 0) {
            //取右边比较
            return recursionBinarySearch(array, key, middle, high);
        } else if (key.compareTo(array[middle]) == 0) {
            return middle;
        }
        return -1;
    }

}

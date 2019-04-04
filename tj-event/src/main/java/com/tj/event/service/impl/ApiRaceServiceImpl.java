package com.tj.event.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.tj.dto.RedisRaceInfo;
import com.tj.dto.RedisRaceRebateInfo;
import com.tj.event.domain.RaceInfo;
import com.tj.event.domain.RaceInfoExample;
import com.tj.event.eventapi.OddsApi;
import com.tj.event.eventapi.RaceApi;
import com.tj.event.eventapi.RaceResult;
import com.tj.event.eventapi.RecordApi;
import com.tj.event.factory.RaceHandleBase;
import com.tj.event.service.RaceService;
import com.tj.event.util.CrawlUtils;
import com.tj.event.util.WinInfo;
import com.tj.util.A.SacException;
import com.tj.util.Results;
import com.tj.util.enums.DealRecordEnum;
import com.tj.util.enums.RaceApiCode;
import com.tj.util.enums.RaceStatusEnum;
import com.tj.util.enums.RedisKeys;
import com.tj.util.time.TimeUtil;
import io.jsonwebtoken.lang.Collections;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


/**
 * 两个定时任务：
 * 1. 每隔1 小时更新今天和未来 7 天的赛事信息、历史战绩
 * 2. 每两分钟更新实时赛事信息
 */
@Slf4j
@Configuration
@Service("api")
public class ApiRaceServiceImpl extends RaceHandleBase implements RaceService {

    private final RedisTemplate redisTemplate;

    private String raceUrl = "https://3s101apiu-x.argoinno66.com/api/sb/getcs?userId=a1812u01&hash=6b00870b40131e8890d24e7bd74f7239";
    private String resultUrl = "https://3s101apiu-x.argoinno66.com/api/sb/getresult?userId=a1812u01&hash=6b00870b40131e8890d24e7bd74f7239";

    @Autowired
    public ApiRaceServiceImpl(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Results.Result realMonitorTask() {
        scanLiveRace();
        checkEventResult();
        return Results.SUCCESS;
    }

    @Override
    public Results.Result searchTask() {
        addTodayRace();
        addRecent7DayRace();
        super.searchHist();
        super.searchRaceMapping();
        return Results.SUCCESS;
    }

    /**
     * 更新今天结束的比赛
     */
    private Results.Result checkEventResult() {
        RaceInfoExample example = new RaceInfoExample();
        RaceInfoExample.Criteria or = example.or().andEndTimeIsNull();
        List<RaceInfo> list = raceInfoMapper.selectByExample(example);
        List<RecordApi<RaceResult>> recordApis = Lists.newArrayList();

        if (Collections.isEmpty(list)) {
            return Results.SUCCESS;
        }
        //查昨天和今天两天的赛果数据
        for (int i = 0; i < 2; i++) {
            String date = TimeUtil.ChangeDateFormat(TimeUtil.plusDay(-i, new Date()), "yyyy-MM-dd");
            RecordApi<RaceResult> recordApi = obtainData(resultUrl, "&date=" + date, "&sport=1", "&lang=zh-cn");
            recordApis.add(recordApi);
        }

        for (RaceInfo raceInfo : list) {
            recordApis.forEach(v -> {
                dealRaceResults(v, raceInfo);
            });
        }
        return Results.SUCCESS;
    }

    /**
     * 检测正在进行的比赛
     */
    private void scanLiveRace() {
        RecordApi<RaceApi> recordApi = obtainData(raceUrl,"&marketId=0","&lang=zh-cn", null);
        dealRecords(recordApi, DealRecordEnum.LIVE.toString());
    }

    /**
     * 检测今天要进行的赛事数据
     */
    private void addTodayRace() {
        RecordApi<RecordApi> recordApi = obtainData(raceUrl,"&marketId=1","&lang=zh-cn", "");
        dealRecords(recordApi, DealRecordEnum.WAIT.toString());
    }

    /**
     * 缓存明天到 7 天后的赛事数据到 Redis
     */
    private void addRecent7DayRace() {
        for (int i = 1; i <= 7; i++) {
            RecordApi<RecordApi> recordApi = obtainData(raceUrl,"&marketId=2","&lang=zh-cn", "&dayAdd="+String.valueOf(i));
            dealRecords(recordApi, DealRecordEnum.WAIT.toString());
        }
    }

    private void dealRaceResults(RecordApi recordApi, RaceInfo race) {
        if (RaceApiCode.incorrect.getCode().equals(recordApi.getCode()) ) {
            log.info("账户/MD5 Hash 值不对", resultUrl);
            throw new SacException("User Id or Hash is Incorrect.");
        }
        if (RaceApiCode.server_error.getCode().equals(recordApi.getCode()) ) {
            log.info("API 服务器内部错误，稍后重试");
            throw new SacException("API Internal Server Error.");
        }
        if (!RaceApiCode.success.getCode().equals(recordApi.getCode()) ) {
            return;
        }

        List<?> races = recordApi.getData();
        List<RedisRaceInfo> redisRaceInfoList = new LinkedList<RedisRaceInfo>();

        for (int j=0; j<races.size(); j++) {
            JSONObject jsonObject = JSONObject.fromObject(races.get(j));
            RaceResult result = (RaceResult) JSONObject.toBean(jsonObject, RaceResult.class);

            if (!race.getId().equals(result.getMatchid())) { continue; }

            //根据 id 从 redis 获取
            Set keys = redisTemplate.keys(RedisKeys.EVENT_RACE + "*" + result.getMatchid() + "*");
            List<com.alibaba.fastjson.JSONObject> list = redisTemplate.opsForValue().multiGet(keys);
            if (list.size() < 1) {
                throw new SacException("赛事" + result.getMatchid() + "已经过期");
            }
            com.alibaba.fastjson.JSONObject json = list.get(0);
            RedisRaceInfo redisRace = JSON.parseObject(JSON.toJSONString(json), RedisRaceInfo.class);
            redisRace.setHalfResult(result.getFhscore());
            redisRace.setWinResult(result.getFtscore());

            if (!StringUtils.isEmpty(result.getFtscore())) {
                redisRace.setEndTime(new Date());
                redisRace.setRaceResult(RaceStatusEnum.end.getCode());

                WinInfo winInfo = new WinInfo(new String[]{result.getHome(), result.getAway()}, result.getFtscore(), "").compare();
                redisRace.setWinTeam(winInfo.getWinTeam());
                redisRace.setWinType(CrawlUtils.getWinType(result.getFtscore()));
            }
            redisRaceInfoList.add(redisRace);//单笔记录更新

            eventService.flushRaceInfoList(redisRaceInfoList);
        }
    }

    private void dealRecords(RecordApi recordApi, String dealType) {
        if (RaceApiCode.incorrect.getCode().equals(recordApi.getCode()) ) {
            log.info("账户/MD5 Hash 值不对", raceUrl);
            throw new SacException("User Id or Hash is Incorrect.");
        }
        if (RaceApiCode.server_error.getCode().equals(recordApi.getCode()) ) {
            log.info("API 服务器内部错误，稍后重试");
            throw new SacException("API Internal Server Error.");
        }
        if (!RaceApiCode.success.getCode().equals(recordApi.getCode()) ) {
            return;
        }

        List<?> races = recordApi.getData();
        List<RedisRaceInfo> redisRaceInfoList = new LinkedList<>();

        for (int j=0; j<races.size(); j++) {
            JSONObject jsonObject = JSONObject.fromObject(races.get(j));
            RaceApi race = (RaceApi) JSONObject.toBean(jsonObject, RaceApi.class);

            String startTime = race.getKickoffdate().replace("T", " ");
            RedisRaceInfo raceInfo = RedisRaceInfo.builder()
                    .id(String.valueOf(race.getMatchid()))
                    .category(race.getLeaguename_lang())
                    .startTime(TimeUtil.getDateFormat(startTime, "yyyy-MM-dd HH:mm:ss"))
                    .winResult(race.getHomescore() + " - " + race.getAwayscore())
                    .homeTeam(race.getHometeamname_lang())
                    .visitTeam(race.getAwayteamname_lang())
                    .enCategory(race.getLeaguename())
                    .enHomeTeam(race.getHometeamname())
                    .enVisitTeam(race.getAwayteamname())
                    .createTime(new Date())
                    .build();

            if ("5".equals(race.getMatchtimehalf())) {// 半场
                raceInfo.setHalfResult(race.getHomescore() + ":" + race.getAwayscore());
            }
            if (DealRecordEnum.LIVE.toString().equals(dealType)) {
                Set keys = redisTemplate.keys(RedisKeys.EVENT_RACE + "*" + raceInfo.getId() + "*");
                List<com.alibaba.fastjson.JSONObject> list = redisTemplate.opsForValue().multiGet(keys);
                if (list == null || list.isEmpty()) {
                    return;
                }
                com.alibaba.fastjson.JSONObject json = list.get(0);
                RedisRaceInfo redisRaceInfo = JSON.parseObject(JSON.toJSONString(json), RedisRaceInfo.class);
                raceInfo.setRebates(redisRaceInfo.getRebates());

                raceInfo.setRaceResult(RaceStatusEnum.processing.getCode());
            }

            if (DealRecordEnum.WAIT.toString().equals(dealType)) { //未开始赛事才更新赔率
                jsonObject = JSONObject.fromObject(race.getOther().getCs());
                OddsApi odds = (OddsApi) JSONObject.toBean(jsonObject, OddsApi.class);

                List<RedisRaceRebateInfo> rebateList = addRebates(race, odds).getResult();
                raceInfo.setRebates(rebateList);

                raceInfo.setRaceResult(RaceStatusEnum.un_start.getCode());
            }

            redisRaceInfoList.add(raceInfo);
            eventService.flushRaceInfoList(redisRaceInfoList); //添加，空字段后边补上
        }
    }

    private Results.Result<List<RedisRaceRebateInfo>> addRebates(RaceApi race, OddsApi odds) {
        Class clazz = OddsApi.class;
        Field[] fields = clazz.getDeclaredFields();

        List<RedisRaceRebateInfo> rebateList = Lists.newLinkedList();
        for (Field field : fields) {
            if (field.getName().startsWith("home")) { //主队比分大
                addOdds(race, odds, rebateList, field, true);
            }
            if (field.getName().startsWith("away")) {//客队比分大
                addOdds(race, odds, rebateList, field, false);
            }
            if (field.getName().startsWith("odds")) {// 平手
                addOdds(race, odds, rebateList, field, false);
            }
            if (field.getName().startsWith("aos")) {// 其他比分
                addOdds(race, odds, rebateList, field, false);
            }
        }
        return new Results.Result<>(Results.SUCCESS, rebateList);
    }

    /**
     * @param race
     * @param odds
     * @param rebateList
     * @param field
     * @param flag ：主队比分大于客队，赋值true; 否则 false
     */
    private RedisRaceRebateInfo addOdds(RaceApi race, OddsApi odds, List<RedisRaceRebateInfo> rebateList, Field field, Boolean flag) {
        String score = "";
        if (field.getName().contains("aos")) { //其他比分
            score = "其他";
        } else {
            int index = field.getName().length() - 2;
            String score1 = field.getName().substring(index, index+1);
            String score2 = field.getName().substring(index+1, index+2);
            score = score2 + " - " + score1;
            if (flag) {
                score = score1 + " - " + score2;
            }
        }

        String oddsInfo = (String) getFieldValue(odds, field.getName());
        BigDecimal rate = new BigDecimal(Double.parseDouble(oddsInfo));

        RedisRaceRebateInfo rebateInfo = RedisRaceRebateInfo.builder()
                .raceId(String.valueOf(race.getMatchid()))
                .createTime(new Date())
                .teams(race.getHometeamname_lang() + ":" + race.getAwayteamname_lang())
                .score(score)
                .normalOdds(rate.setScale(2, BigDecimal.ROUND_FLOOR))
                .build();
        rebateList.add(rebateInfo);
        return rebateInfo;
    }

    /**
     * 通过反射，用属性名称获得属性值
     * @param thisClass 需要获取属性值的类
     * @param fieldName 该类的属性名称
     * @return
     */
    private Object getFieldValue(Object thisClass, String fieldName) {
        Object value = new Object();
        Method method = null;
        try {
            String methodName = toFirstUpcase(fieldName);
            method = thisClass.getClass().getMethod("get" + methodName);
            value = method.invoke(thisClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public static String toFirstUpcase(String name) {
        char[] cs = name.toCharArray();
        cs[0] -= 32;
        return String.valueOf(cs);
    }


    public RecordApi obtainData(@NonNull String path, @NonNull String param1, @NonNull String param2, String param3) {
//        http://3s101apiu-x.argoinno66.com/api/sb/getcs?userId=a1812u01&hash=6b00870b40131e8890d24e7bd74f7239&marketId=2&lang=zh-cn&dayAdd=1
        StringBuffer url = new StringBuffer(path);
        if (StringUtils.isEmpty(param1)) {
            throw new SacException("parameter is error, marketId is null");
        }
        if (StringUtils.isEmpty(param2))  {
            throw new SacException("parameter is error, lang is null");
        }

        url.append(param1).append(param2);

        if (!StringUtils.isEmpty(param3))  {
            url.append(param3);
        }

        log.info("invoke api to handle race info, api :" + url.toString());

        RecordApi<RaceApi> record = new RecordApi<RaceApi>();
        try {
            String result = sendGet(url.toString());
            if(StringUtils.isNotEmpty(result)) {
                JSONObject jsonObject = JSONObject.fromObject(result);
                record = (RecordApi<RaceApi>) JSONObject.toBean(jsonObject, RecordApi.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return record;
    }

}

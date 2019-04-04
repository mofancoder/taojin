package com.tj.event.service;

import com.github.pagehelper.PageInfo;
import com.tj.dto.*;
import com.tj.event.domain.DiveRule;
import com.tj.util.Results;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Auther: kevin
 * @Date: 2018/11/29 11:20
 * @Description:
 */
public interface EventService {

    /**
     * 管理员查看赛事仓库
     * @return
     */
    Results.Result<AdminEventPage> redisEventList(Long firstTime, Long secondTime, String type, Integer curPage, Integer pageSize);

    /**
     * 管理员赛事落地
     * @return
     */
    Results.Result insertDBEvent(String raceId);

    /**
     * 管理员赛事利率查询
     * @param raceId
     * @return
     */
    Results.Result<AdminRebateInfoTotal> rebateEventList(String raceId);

    /**
     * 查看赛事历史信息
     * @param raceId
     * @return
     */
    Results.Result<HistEventInfo> histRaceList(String raceId);

    /**
     * 查询赛事信息
     * @return
     */
    Results.Result<PageInfo<RaceInfoDto>> selectDBEvent(Long firstTime, Long secondTime, String category, Integer curPage, Integer pageSize, String homeTeam, String visitTeam, Integer openStatus, Integer shelveStatus, Integer commandStatus,Integer resultStatus);


    /**
     * 赛事返利率集合
     * @param raceId
     * @return
     */
    Results.Result<DBRebateTotal> selectEventRebate(String raceId, Integer type);

    /**
     * 综合赛事信息和返利率
     *
     * @return
     */
    Results.Result<PageInfo<RaceDetailDto>> getEventDetail(String raceId, Long startTime, String category, Integer curPage, Integer pageSize, Integer typeInteger, Integer sortStyle);

    /**
     * 返回赛事结果
     *
     * @param startTime
     * @param category
     * @param curPage
     * @param pageSize
     * @return
     */
    Results.Result<PageInfo<RaceInfoDto>> getEventResult(Long startTime, String category, Integer curPage, Integer pageSize);

    /**
     * 设置单个比分是否可以投注
     * @param openStatus
     * @param rebateId
     * @return
     */
    Results.Result<Map<Integer, RebateInfoDto>> updateRebateOpenStatus(Integer openStatus, Integer rebateId, BigDecimal base);


    /**
     * 查询规则
     *
     * @return
     */
    Results.Result<List<DiveRuleDto>> selectDiveRule();

    /**
     * 插入规则
     * @param increase
     * @param autochangeTime
     * @param startAmount
     * @param startRebate
     * @param endRebate
     * @param ruleType
     * @param enableStatus
     * @return
     */
    Results.Result insertDiveRule(Double increase, Double autochangeTime,Integer timeRange, Double startAmount, Double shutdownRebate, Double startRebate, Double endRebate, Integer ruleType, Integer enableStatus);

    /**
     * 修改规则
     * @param id
     * @param enableStatus
     * @return
     */
    Results.Result<DiveRule> updateDiveRule(Integer id, Integer enableStatus);

    /**
     * 修改规则时间范围状态
     * @param id
     * @param timeRange
     * @return
     */
    Results.Result updateDiveTimeRange(Integer id, Integer timeRange);

    /**
     * 删除规则
     * @param id
     * @return
     */
    Results.Result deleteRule(Integer id);

    /**
     * 手动跳水、设置可下单量
     * @param rebateId
     * @param increaseRebate
     * @param validAmount
     * @return
     */
    Results.Result updateEventRebate(Integer rebateId, BigDecimal increaseRebate, BigDecimal validAmount);


//    /**
//     * 自动跳水，定时任务执行
//     * @return
//     */
//    Results.Result<Void> autoRebateDive(String raceId, Integer rebateId);


    /**
     * 刷新赛事数据，给爬虫
     * @param redisRaceInfoList
     * @return
     */
    Results.Result flushRaceInfoList(List<RedisRaceInfo> redisRaceInfoList);

    /**
     * 首页推荐赛事
     * @param pageNum
     * @param pageSize
     * @return
     */
    Results.Result<PageInfo<RedisRaceInfo>> eventRecommendList(Integer pageNum, Integer pageSize);



    Results.Result<Void> recommend(String id, Integer shelvesStatus, Integer openStatus, Integer recommend, Double weight);


    Results.Result<Void> recommend(String id, Integer commendStatus, BigDecimal weight);

    Results.Result<Void> shelvesStatus(String id, Integer shelvesStatus);

    Results.Result<Void> openStatus(String id, Integer openStatus);


    /**
     * 缓存战队趋势数据
     *
     * @param trendInfo 趋势数据
     * @return 缓存是否成功
     */
    Results.Result<Void> cacheRaceTrend(RedisTeamTrendInfo trendInfo);

    /**
     * 缓存双方交战历史
     *
     * @param histories 历史数据
     * @return 缓存是否成功
     */
    Results.Result<Void> cacheBothSide(List<RedisBothSideRaceHistory> histories);

    /**
     * 缓存单个战队历史战绩
     *
     * @param histories 历史记录
     * @return 缓存是否成功
     */
    Results.Result<Void> cacheRaceHistory(List<RedisRaceHistory> histories);

    Results.Result<Void> updateRule(DiveRuleDto dto);

    Results.Result<Void> submitOperate(List<OperateDto> list);

    /**
     * 查询所有的赛事分类
     *
     * @param curPage  当前页
     * @param pageSize 分页大小
     * @return
     */
    Results.Result<PageInfo<String>> raceCategories(Integer curPage, Integer pageSize);

    /**
     * 确认初始返利率
     *
     * @param dto
     * @return
     */
    Results.Result<Void> sumitInitRebate(InitRebateDto dto);
}

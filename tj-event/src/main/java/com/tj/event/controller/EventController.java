package com.tj.event.controller;

import com.github.pagehelper.PageInfo;
import com.tj.dto.*;
import com.tj.event.domain.DiveRule;
import com.tj.event.service.EventService;
import com.tj.util.Results;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * @program: tj-core
 * @description: ${description}
 * @author: liang.song
 * @create: 2018-11-21 15:12
 **/
@RestController
@RequestMapping("/event")
@Api(tags = "event", description = "赛事")
public class EventController {

    public final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @ApiOperation(value = "缓存赛事过滤")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "管理员令牌", required = true, dataType = "string", paramType = "header"),
            @ApiImplicitParam(name = "firstTime", value = "赛事开始时间起始(开赛时间戳)", required = false, dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "secondTime", value = "赛事开始时间结束(开赛时间戳)", required = false, dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "type", value = "赛事类型", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "curPage", value = "当前页", defaultValue = "1", required = false, dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", required = false, dataType = "long", paramType = "query"),
    })
    @GetMapping(value = "/admin/filter")
    public Results.Result<AdminEventPage> filter(@RequestParam(value = "firstTime", required = false) Long firstTime,
                                                      @RequestParam(value = "secondTime", required = false) Long secondTime,
                                                      @RequestParam(value = "type", required = false) String type,
                                                      @RequestParam(value = "curPage", required = false, defaultValue = "1") Integer curPage,
                                                      @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize) {
        return eventService.redisEventList(firstTime, secondTime, type, curPage, pageSize);
    }

    @ApiOperation(value = "赛事数据落地")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "管理员令牌", required = true, dataType = "string", paramType = "header"),
            @ApiImplicitParam(name = "raceId", value = "赛事Id", required = true, dataType = "string", paramType = "query")
     })
    @GetMapping(value = "/admin/addEvent")
    public Results.Result insertDBEvent(String raceId) {
        return eventService.insertDBEvent(raceId);
    }

    @ApiOperation(value = "赛事历史记录详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "raceId", value = "赛事Id", required = true, dataType = "string", paramType = "query")
    })
    @GetMapping(value = "/open/histRaceList")
    public Results.Result<HistEventInfo> histRaceList(@RequestParam String raceId) {
        return eventService.histRaceList(raceId);
    }

    @ApiOperation(value = "赛事比分返利率详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "管理员令牌", required = true, dataType = "string", paramType = "header"),
            @ApiImplicitParam(name = "raceId", value = "赛事Id", required = true, dataType = "string", paramType = "query")
    })
    @GetMapping(value = "/admin/rebateInfo")
    public Results.Result<AdminRebateInfoTotal> rebateList(@RequestParam String raceId) {
        return eventService.rebateEventList(raceId);
    }

    @ApiOperation(value = "落地赛事查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "category", value = "赛事类别(eg:英超)", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "firstTime", value = "赛事开始时间起始(eg:timestamp(2018-10-01))", required = false, dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "secondTime", value = "赛事开始时间结束(eg:timestamp(2018-10-01))", required = false, dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "curPage", value = "当前页", required = true, dataType = "long", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", required = true, dataType = "long", paramType = "query", defaultValue = "20"),
            @ApiImplicitParam(name = "homeTeam", value = "主队名称", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "visitTeam", value = "客队名称", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "openStatus", value = "可投注状态(0:不可投注 1:可投注)", required = false, dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "shelveStatus", value = "上下架状态(0:下架 1:上架)", required = false, dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "commandStatus", value = "推荐状态（0:不推荐 1:推荐)", required = false, dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "resultStatus", value = "赛事状态(0:取消 1:正常进行中 2:已经结束 3:未开始)", required = false, dataType = "long", paramType = "query")
    })
    @GetMapping(value = "/open/eventList")
    public Results.Result<PageInfo<RaceInfoDto>> eventList(@RequestParam(value = "firstTime", required = false) Long firstTime,
                                                           @RequestParam(value = "secondTime", required = false) Long secondTime,
                                                           @RequestParam(value = "category", required = false) String category,
                                                           @RequestParam(value = "curPage", required = false, defaultValue = "1") Integer curPage,
                                                           @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize,
                                                           @RequestParam(value = "homeTeam", required = false) String homeTeam,
                                                           @RequestParam(value = "visitTeam", required = false) String visitTeam,
                                                           @RequestParam(value = "openStatus", required = false) Integer openStatus,
                                                           @RequestParam(value = "shelveStatus", required = false) Integer shelveStatus,
                                                           @RequestParam(value = "commandStatus", required = false) Integer commandStatus,
                                                           @RequestParam(value = "resultStatus", required = false) Integer resultStatus) {
        return eventService.selectDBEvent(firstTime, secondTime, category, curPage, pageSize, homeTeam, visitTeam, openStatus, shelveStatus, commandStatus, resultStatus);
    }


    @ApiOperation(value = "赛事实时利率查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "赛事ID", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "type", value = "波胆类型(1：全场波胆 2:半场波胆)", required = true, dataType = "string", paramType = "query"),
    })
    @GetMapping(value = "/open/getRealRebate")
    public Results.Result<DBRebateTotal> getEventRebate(@RequestParam("id") String id, @RequestParam("type") Integer type) {
        return eventService.selectEventRebate(id, type);
    }

    @ApiOperation(value = "落地赛事信息与赛事利率汇总查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "raceId", value = "赛事 ID", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "category", value = "赛事类别(eg:英超)", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "赛事开始时间(eg:timestamp(2018-10-01))", required = false, dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "curPage", value = "当前页", required = true, dataType = "long", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", required = true, dataType = "long", paramType = "query", defaultValue = "20"),
            @ApiImplicitParam(name = "type", value = "波胆类型(1：全场波胆 2:半场波胆)", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "sortStyle", value = "排序风格(1：推荐赛事排序 2:列表赛事排序)", required = true, dataType = "long", defaultValue = "1", paramType = "query"),
    })
    @GetMapping(value = "/open/eventDetailList")
    public Results.Result<PageInfo<RaceDetailDto>> eventDetailList(@RequestParam(value = "raceId", required = false) String raceId,
                                                                   @RequestParam(value = "startTime", required = false) Long startTime,
                                                                   @RequestParam(value = "category", required = false) String category,
                                                                   @RequestParam(value = "curPage", required = false, defaultValue = "1") Integer curPage,
                                                                   @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize,
                                                                   @RequestParam(value = "type", required = true) Integer type,
                                                                   @RequestParam(value = "sortStyle", required = false, defaultValue = "1") Integer sortStyle) {
        return eventService.getEventDetail(raceId, startTime, category, curPage, pageSize, type, sortStyle);
    }

    @ApiOperation(value = "落地赛事结果查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "category", value = "赛事类别(eg:英超)", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "赛事开始时间(eg:timestamp(2018-10-01))", required = false, dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "curPage", value = "当前页", required = true, dataType = "long", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", required = true, dataType = "long", paramType = "query", defaultValue = "20"),
    })
    @GetMapping(value = "/open/eventResultList")
    public Results.Result<PageInfo<RaceInfoDto>> eventResultList(@RequestParam(value = "startTime", required = false) Long startTime,
                                                                 @RequestParam(value = "category", required = false) String category,
                                                                 @RequestParam(value = "curPage", required = false, defaultValue = "1") Integer curPage,
                                                                 @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize) {
        return eventService.getEventResult(startTime, category, curPage, pageSize);
    }

    @ApiOperation(value = "赛事实时利率手动跳水")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "管理员令牌", required = true, dataType = "string", paramType = "header"),
            @ApiImplicitParam(name = "rebateId", value = "利率ID", required = true, dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "increaseRebate", value = "赛事ID", required = true, dataType = "double", paramType = "query"),
            @ApiImplicitParam(name = "validAmount", value = "赛事ID", required = true, dataType = "double", paramType = "query"),
    })
    @GetMapping(value = "/open/setRealRebate")
    public Results.Result<Void> updateEventRebate(@RequestParam("rebateId")Integer rebateId, @RequestParam("increaseRebate")BigDecimal increaseRebate, @RequestParam("validAmount")BigDecimal validAmount) {
        return eventService.updateEventRebate(rebateId, increaseRebate, validAmount);
    }

    @ApiOperation(value = "查询跳水规则")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "管理员令牌", required = true, dataType = "string", paramType = "header"),
    })
    @GetMapping(value = "/admin/getRebateDiveRule")
    public Results.Result<List<DiveRuleDto>> selectDiveRule() {
        return eventService.selectDiveRule();
    }

    @ApiOperation(value = "设置跳水规则")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "管理员令牌", required = true, dataType = "string", paramType = "header"),
            @ApiImplicitParam(name = "increase", value = "跳水利率", required = true, dataType = "double", paramType = "query"),
            @ApiImplicitParam(name = "autochangeTime", value = "跳水时间", required = true, dataType = "double", paramType = "query"),
            @ApiImplicitParam(name = "timeRange", value = "跳水时间范围(0:外、1:内)", required = true, dataType = "long", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "startAmount", value = "跳水金额开始区间", required = true, dataType = "double", paramType = "query"),
            @ApiImplicitParam(name = "startRebate", value = "返利率开始区间", required = true, dataType = "double", paramType = "query"),
            @ApiImplicitParam(name = "endRebate", value = "返利率结束区间", required = true, dataType = "double", paramType = "query"),
            @ApiImplicitParam(name = "ruleType", value = "规则类型(1:波胆)", required = true, dataType = "double", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "enableStatus", value = "是否启用规则(0:否、1:是)", required = true, dataType = "long", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "shutDownRebate", value = "自动关盘利率", required = true, dataType = "double", paramType = "query"),
    })
    @GetMapping(value = "/admin/setRebateDiveRule")
    public Results.Result insertDiveRule(@RequestParam Double increase, @RequestParam Double autochangeTime, @RequestParam Integer timeRange, @RequestParam Double startAmount,
                                         @RequestParam Double startRebate, @RequestParam Double endRebate,
                                         @RequestParam Integer ruleType, @RequestParam Integer enableStatus, @RequestParam Double shutDownRebate) {
        return eventService.insertDiveRule(increase, autochangeTime, timeRange, startAmount, shutDownRebate, startRebate, endRebate, ruleType, enableStatus);
    }

    @ApiOperation(value = "跳水规则状态开启/关闭")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "管理员令牌", required = true, dataType = "string", paramType = "header"),
            @ApiImplicitParam(name = "id", value = "跳水规则ID", required = true, dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "enableStatus", value = "是否启用规则(0:否、1:是)", required = true, dataType = "long", paramType = "query"),
    })
    @GetMapping(value = "/admin/editRebateDiveRule")
    public Results.Result<DiveRule> updateDiveRule(Integer id, @RequestParam Integer enableStatus) {
        return eventService.updateDiveRule(id, enableStatus);
    }

    @ApiOperation(value = "跳水时间范围状态 内/外")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "管理员令牌", required = true, dataType = "string", paramType = "header"),
            @ApiImplicitParam(name = "id", value = "跳水规则ID", required = true, dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "timeRange", value = "跳水时间范围(0:外、1:内)", required = true, dataType = "long", paramType = "query"),
    })
    @GetMapping(value = "/admin/editDiveTimeRang")
    public Results.Result updateDiveTimeRange(Integer id, @RequestParam Integer timeRange) {
        return eventService.updateDiveTimeRange(id, timeRange);
    }

    @ApiOperation(value = "管理员更新跳水规则")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "管理员令牌", required = true, dataType = "string", paramType = "header"),
    })
    @PostMapping("/admin/updateDive")
    public Results.Result<Void> updateRule(@RequestBody DiveRuleDto dto) {
        return eventService.updateRule(dto);
    }

    @ApiOperation(value = "管理员删除跳水规则")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "管理员令牌", required = true, dataType = "string", paramType = "header"),
            @ApiImplicitParam(name = "id", value = "跳水规则ID", required = true, dataType = "long", paramType = "query"),
    })
    @GetMapping("/admin/deleteDive")
    public Results.Result<Void> deleteRule(Integer id) {
        return eventService.deleteRule(id);
    }

    @ApiOperation(value = "首页热门赛事查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "curPage", value = "页码", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "每页数量", required = true, dataType = "string", paramType = "query"),
    })
    @GetMapping(value = "/open/eventRecommendList")
    public Results.Result<PageInfo<RedisRaceInfo>> eventRecommendList(@RequestParam Integer curPage, @RequestParam Integer pageSize) {
        return eventService.eventRecommendList(curPage, pageSize);
    }

    @ApiOperation(value = "修改单个比分可投注状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "管理员令牌", required = true, dataType = "string", paramType = "header"),
            @ApiImplicitParam(name = "openStatus", value = "可投注状态(0:不可投注 1:可投注)", required = true, dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "id", value = "返利率ID", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "base", value = "基准(范围:0-1)", required = true, dataType = "string", paramType = "query"),
    })
    @GetMapping(value = "/admin/editRebateOpenStatus")
    public Results.Result editRebateOpenStatus(@RequestParam("openStatus") Integer openStatus, @RequestParam("id") Integer rebateId, @RequestParam("base") BigDecimal base) {
        return eventService.updateRebateOpenStatus(openStatus, rebateId, base);
    }

//    @ApiOperation(value = "定时执行自动跳水")
//    @GetMapping(value = "/open/autoRebateDive")
//    public Results.Result autoRebateDive() {
//        eventService.autoRebateDive(null, null);
//        return Results.SUCCESS;
//    }

    @ApiOperation(value = "管理员-推荐状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "管理员令牌", required = true, dataType = "string", paramType = "header"),
            @ApiImplicitParam(name = "id", value = "赛事ID", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "status", value = "推荐状态（0:不推荐 1:推荐)", required = true, dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "weight", value = "比重(eg:80) 当不推荐的时候可以不填", required = false, dataType = "long", paramType = "query"),
    })
    @GetMapping("/admin/recommend")
    public Results.Result<Void> recommend(@RequestParam("id") String id, @RequestParam("status") Integer commendStatus, @RequestParam(value = "weight", required = false) BigDecimal weight) {
        return eventService.recommend(id, commendStatus, weight);
    }

    @ApiOperation(value = "管理员-赛事上下架状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "管理员令牌", required = true, dataType = "string", paramType = "header"),
            @ApiImplicitParam(name = "id", value = "赛事ID", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "status", value = "上下架状态(0:下架 1:上架)", required = true, dataType = "long", paramType = "query"),
    })
    @GetMapping("/admin/shelve")
    public Results.Result<Void> shelvesStatus(@RequestParam("id") String id, @RequestParam("status") Integer shelvesStatus) {
        return eventService.shelvesStatus(id, shelvesStatus);
    }

    @ApiOperation(value = "管理员-赛事是否可以投注")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "管理员令牌", required = true, dataType = "string", paramType = "header"),
            @ApiImplicitParam(name = "id", value = "赛事ID", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "status", value = "投注状态(0:不可以投注 1:可投注)", required = true, dataType = "long", paramType = "query"),
    })
    @GetMapping("/admin/openStatus")
    public Results.Result<Void> openStatus(@RequestParam("id") String id, @RequestParam("status") Integer openStatus) {
        return eventService.openStatus(id, openStatus);
    }

    @ApiOperation(value = "管理员-操盘确认")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "管理员令牌", required = true, dataType = "string", paramType = "header"),
    })
    @PostMapping("/admin/operate/submit")
    public Results.Result<Void> submitOperate(@RequestBody OperateInfoDto dto) {
        return eventService.submitOperate(dto.getList());
    }

    @ApiOperation(value = "获取所有的赛事分类")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "curPage", value = "当前页", required = false, dataType = "long", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", required = false, dataType = "long", paramType = "query", defaultValue = "20"),
    })
    @GetMapping("/open/category")
    public Results.Result<PageInfo<String>> raceCategories(Integer curPage, Integer pageSize) {
        return eventService.raceCategories(curPage, pageSize);
    }

    @ApiOperation(value = "管理员提交确认初始返利率按钮")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dto", value = "初始化返利率", required = true, dataType = "InitRebateDto", paramType = "body", defaultValue = "1"),
    })
    @GetMapping("/admin/submitInit")
    public Results.Result<Void> submitInitRebate(@RequestBody InitRebateDto dto) {
        return eventService.sumitInitRebate(dto);

    }
}

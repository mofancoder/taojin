package com.tj.bet.controller;

import com.tj.bet.service.BetService;
import com.tj.dto.*;
import com.tj.util.Results;
import com.tj.util.aspect.CommonLogAspect;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @program: tj-core
 * @description: 投注控制器
 * @author: liang.song
 * @create: 2018-12-12-13:47
 **/
@RestController
@RequestMapping("/bet")
public class BetController {
    private final CommonLogAspect commonLogAspect;
    private final BetService betService;


    @Autowired
    public BetController(CommonLogAspect commonLogAspect, BetService betService) {
        this.commonLogAspect = commonLogAspect;
        this.betService = betService;
    }

    @ApiOperation(value = "用户投注")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "token", required = true, dataType = "string", paramType = "header"),
    })
    @PostMapping("/add")
    public Results.Result<UnbetReasonDto> bet(@RequestBody BetRequestDto betRequestDto) {
        RedisUserInfo redisUserInfo = commonLogAspect.currentUser();
        return betService.add(redisUserInfo.getUserId(), betRequestDto);
    }

    @ApiOperation(value = "用户撤单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "token", required = true, dataType = "string", paramType = "header"),
            @ApiImplicitParam(name = "betId", value = "betId", required = true, dataType = "string", paramType = "query"),
    })
    @GetMapping("/cancellations")
    public Results.Result<String> cancellations(@RequestParam(value = "betId", required = true) Long betId) {
        RedisUserInfo redisUserInfo = commonLogAspect.currentUser();
        return betService.cancellations(redisUserInfo.getUserId().longValue(), betId);
    }


    @ApiOperation(value = "用户查询投注记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "用户令牌", required = true, dataType = "string", paramType = "header"),
            @ApiImplicitParam(name = "type", value = "投注类型(1:波胆 2:反波胆)", required = false, dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "dateType", value = "日期类型(1:近一周 2:近一个月 3:近一年,4:全部)", required = false, defaultValue = "1", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "betResult", value = "投注结果(0:输,1:赢)", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "rebateStatus", value = "是否返利(0:返利失败 1:返利成功 2:未返利)", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "betStatus", value = "0赛事取消自动撤注，1已投注，2用户撤单", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "curPage", value = "当前页", required = true, defaultValue = "1", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", required = true, defaultValue = "20", dataType = "long", paramType = "query"),
    })
    @GetMapping("/list")
    public Results.Result<AdminBetRecdInfo<BetRecdDto>> list(@RequestParam(value = "type", defaultValue = "1", required = false) Integer type,
                                                             @RequestParam(value = "dateType", required = false, defaultValue = "1") Integer dateType,
                                                             @RequestParam(value = "betResult", required = false) String betResult,
                                                             @RequestParam(value = "rebateStatus", required = false) String rebateStatus,
                                                             @RequestParam(value = "betStatus", required = false) String betStatus,
                                                             @RequestParam(value = "curPage", required = false, defaultValue = "1") Integer curPage,
                                                             @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize) {
        RedisUserInfo userInfo = commonLogAspect.currentUser();
        return betService.list(userInfo.getUserId(), type, dateType, curPage, pageSize,betResult,rebateStatus,betStatus);
    }

    @ApiOperation(value = "定时任务定时结算")
    @PostMapping("/open/settle")
    public Results.Result<Void> settle() {
        return betService.settle();
    }

    @ApiOperation(value = "按新规则定时结算赛事")
    @PostMapping("/open/newSettle")
    public Results.Result<Void> newSettle() {
        return betService.newSettle();
    }
    @ApiOperation(value = "取消指定ID赛事")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "管理员令牌", required = true, dataType = "string", paramType = "header"),
            @ApiImplicitParam(name = "raceId", value = "赛事ID", required = true, dataType = "string", paramType = "query"),
    })
    @PostMapping("/cancel/raceInfo")
    public Results.Result<Void> cancelRaceInfo(@RequestParam(value = "raceId", required = true) String raceId) {
        return betService.cancelRaceInfo(raceId);
    }

    @ApiOperation(value = "返还取消赛事的投注金额")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "管理员令牌", required = true, dataType = "string", paramType = "header"),
            @ApiImplicitParam(name = "raceId", value = "赛事ID", required = true, dataType = "string", paramType = "query"),
    })
    @PostMapping("/rollback/raceInfo")
    public Results.Result<Void> rollbackForCancelRace(@RequestParam(value = "raceId", required = true) String raceId) {
        return betService.rollbackForCancelRace(raceId);
    }
    @ApiOperation(value = "定时扫描取消赛事并返还投注额")
    @PostMapping("/open/selectAllCancelRaceAndRollback")
    public Results.Result<Void> selectAllCancelRaceAndRollback() {

        return betService.selectAllCancelRaceAndRollback();
    }

    @ApiOperation(value = "管理员查看投注记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "管理员令牌", required = true, dataType = "string", paramType = "header"),
            @ApiImplicitParam(name = "betId", value = "投注ID", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "account", value = "用户手机号", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "phone", value = "用户手机号", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "raceId", value = "赛事ID", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "score", value = "比分", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "betType", value = "投注类型(1:波胆 2:半场波胆)", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "betResult", value = "投注结果(0:输 1:赢)", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "rebateStatus", value = "返利状态(0:返利失败 1:返利成功 2:未返利)", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", required = false, dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", required = false, dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "curPage", value = "当前页", required = true, defaultValue = "1", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", required = true, defaultValue = "20", dataType = "long", paramType = "query"),
    })
    @GetMapping("/admin/list")
    public Results.Result<AdminBetRecdInfo> adminList(@RequestParam(value = "betId", required = false) String betId,
                                                      @RequestParam(value = "phone", required = false) String phone,
                                                      @RequestParam(value = "account", required = false) String account,
                                                      @RequestParam(value = "raceId", required = false) String raceId,
                                                      @RequestParam(value = "score", required = false) String score,
                                                      @RequestParam(value = "betType", required = false) Integer betType,
                                                      @RequestParam(value = "betResult", required = false) Integer betResult,
                                                      @RequestParam(value = "rebateStatus", required = false) Integer rebateStatus,
                                                      @RequestParam(value = "startTime", required = false) Long startTime,
                                                      @RequestParam(value = "endTime", required = false) Long endTime,
                                                      @RequestParam(value = "curPage", required = false, defaultValue = "1") Integer curPage,
                                                      @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize) {
        return betService.adminList(betId, phone, account, raceId, score, betType, betResult, rebateStatus, startTime, endTime, curPage, pageSize);
    }

    @ApiOperation(value = "查询各个比分的交易信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "rebateIds", value = "比分返利ID", required = true, dataType = "string", paramType = "body"),
    })
    @PostMapping("/open/rebateInfo")
    public Results.Result<List<RebateBetInfo>> rebateBetInfo(@RequestBody List<Integer> rebateIds) {
        return betService.rebateBetInfo(rebateIds);
    }
}

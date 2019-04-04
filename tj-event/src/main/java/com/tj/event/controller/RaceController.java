package com.tj.event.controller;

import com.tj.event.factory.RaceHandlerFactory;
import com.tj.util.Results;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/raceMonitor")
@Api(tags = "raceMonitor", description = "获取赛事比分")
public class RaceController {

    public final RaceHandlerFactory raceHandlerFactory;
    @Value("${race.datasource.style}")
    private String style;

    @Autowired
    public RaceController(RaceHandlerFactory raceHandlerFactory) {
        this.raceHandlerFactory = raceHandlerFactory;
    }

    /**
     * 更新已结束赛事结果
     *
     * @return
     */
    @GetMapping(value = "/open/checkEventResult")
    public Results.Result checkEventResult() {
//        raceHandlerFactory.getRaceHandleByName(style).checkEventResult();
        return Results.SUCCESS;
    }

    /**
     * 实时更新赛事结果
     *
     * @return
     */
    @GetMapping(value = "/open/refreashOdds")
    public Results.Result refreashOdds() {
//        raceHandlerFactory.getRaceHandleByName(style).refreashOdds();
        return Results.SUCCESS;
    }

    /**
     * 1. 每天早上定时更新今天和未来 7 天的赛事信息
     *
     * @return
     */
    @GetMapping(value = "/open/searchEvent")
    public Results.Result searchTask() {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>:" + style);
        raceHandlerFactory.getRaceHandleByName(style).searchTask();
        return Results.SUCCESS;
    }

    /**
     * 2. 每两分钟更新实时赛事信息
     *
     * @return
     */
    @GetMapping(value = "/open/realMonitor")
    public Results.Result realMonitor() {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>:" + style);
        raceHandlerFactory.getRaceHandleByName(style).realMonitorTask();
        return Results.SUCCESS;
    }
}

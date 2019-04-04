package com.tj.task.service;

import com.tj.util.Results;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("tj-event/raceMonitor")
public interface RaceInfoService {

    /**
     * 1. 每天早上定时更新今天和未来 7 天的赛事信息
     *
     * @return
     */
    @RequestMapping(value = "/open/searchEvent", method = RequestMethod.GET)
    Results.Result searchTask();


    /**
     * 2. 每两分钟更新实时赛事信息
     *
     * @return
     */
    @RequestMapping(value = "/open/realMonitor", method = RequestMethod.GET)
    Results.Result realMonitorTask();

}


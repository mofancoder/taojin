package com.tj.task.service;

import com.tj.util.Results;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @program: tj-core
 * @description: ${description}
 * @author: liang.song
 * @create: 2018-12-14-14:13
 **/
@FeignClient("tj-bet")
public interface BetService {
    @RequestMapping(value = "/bet/open/settle", method = RequestMethod.POST)
    Results.Result<Void> settle();

    @RequestMapping(value = "/bet/open/newSettle", method = RequestMethod.POST)
    Results.Result<Void> newSettle();
    @RequestMapping(value = "/bet/open/selectAllCancelRaceAndRollback", method = RequestMethod.POST)
    Results.Result<Void> selectAllCancelRaceAndRollback();
}

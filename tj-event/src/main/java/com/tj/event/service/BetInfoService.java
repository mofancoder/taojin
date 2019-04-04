package com.tj.event.service;

import com.tj.dto.RebateBetInfo;
import com.tj.util.Results;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient("tj-bet/bet")
public interface BetInfoService {

    @RequestMapping(value = "/open/rebateInfo", method = RequestMethod.GET)
    Results.Result<List<RebateBetInfo>> findBetRebate(List<Integer> rebateIds);
}

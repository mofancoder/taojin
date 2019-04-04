package com.tj.task.service;

import com.tj.util.Results;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("tj-event/event")
public interface DiveService {
    @RequestMapping(value = "/open/autoRebateDive", method = RequestMethod.GET)
    Results.Result<Void> autoRebateDive();
}


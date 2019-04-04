package com.tj.task.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @program: tj-core
 * @description: feign客户端
 * @author: liang.song
 * @create: 2018-12-04-18:31
 **/
@FeignClient("tj-transaction")
public interface TransactionService {

    @GetMapping("/transaction/open/polling")
    void polling(@RequestParam("transactionId") String transactionId, @RequestParam("status") Integer status);
}

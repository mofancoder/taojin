package com.tj.task.schedule;

import com.alibaba.fastjson.JSON;
import com.tj.task.service.BetService;
import com.tj.util.Results;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @program: tj-core
 * @description: ${description}
 * @author: liang.song
 * @create: 2018-12-14-14:12
 **/
@Component
@Slf4j
public class BetSettleTask {

    private static final String cron = "0 0/1 * * * *";

    private final BetService betService;

    @Autowired
    public BetSettleTask(BetService betService) {
        this.betService = betService;
    }

    @Scheduled(cron = cron)
    @SchedulerLock(name = "betSettleTask", lockAtLeastForString = "${schedule.least-time.settle}", lockAtMostForString = "${schedule.most-time.settle}")
    public void exec() {
        log.info("start to auto betSettle");
        Results.Result<Void> settle = betService.settle();
        log.info("settle result:{}", JSON.toJSONString(settle));
    }
}

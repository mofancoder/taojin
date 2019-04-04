package com.tj.task.schedule;

import com.alibaba.fastjson.JSON;
import com.tj.task.service.BetService;
import com.tj.util.Results;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RealTimeSettleTask {
    private static final String cron = "0 0/1 * * * *";

    private final BetService betService;

    @Autowired
    public RealTimeSettleTask(BetService betService) {
        this.betService = betService;
    }

    @Scheduled(cron = cron)
    @SchedulerLock(name = "betSettleTask", lockAtLeastForString = "${schedule.least-time.newSettle}", lockAtMostForString = "${schedule.most-time.newSettle}")
    public void exec() {
        log.info("start to auto betNewSettle");
        Results.Result<Void> newSettle = betService.newSettle();
        log.info("newSettle result:{}", JSON.toJSONString(newSettle));
    }
}

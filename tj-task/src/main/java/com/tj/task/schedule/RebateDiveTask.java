package com.tj.task.schedule;

import com.tj.task.service.DiveService;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class RebateDiveTask {
    private static final String cron = "0 0/1 * * * *";

    private final DiveService diveService;

    @Autowired
    public RebateDiveTask(DiveService diveService) {
        this.diveService = diveService;
    }

//    @Scheduled(cron = cron)
//    @SchedulerLock(name = "rebateDiveTask", lockAtLeastForString = "${schedule.least-time.dive}", lockAtMostForString = "${schedule.most-time.dive}")
//    public void exec() {
//        log.info("start to autoRebateDive");
//        CompletableFuture.runAsync(() -> {
//            diveService.autoRebateDive();
//        });
//    }
}

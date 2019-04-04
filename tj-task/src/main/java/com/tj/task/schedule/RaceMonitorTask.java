package com.tj.task.schedule;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.tj.task.service.RaceInfoService;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class RaceMonitorTask {

    private static final String cron = "0 0/2 * * * *";

    private static final String searchCron = "0 0 0-23 * * *";

    private final RaceInfoService raceInfoService;

    @Autowired
    public RaceMonitorTask(RaceInfoService raceInfoService) {
        this.raceInfoService = raceInfoService;
    }

    @Scheduled(cron = cron)
    @SchedulerLock(name = "realMonitorTask", lockAtLeastForString = "${ts.least.time}", lockAtMostForString = "${ts.most.time}")
    @HystrixCommand(fallbackMethod = "execFallback", commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "${ts.most.time}")
    })
    public void exec() {
        log.info("start check race result ...");
        CompletableFuture.runAsync(() -> {
            raceInfoService.realMonitorTask();
        });
    }

    @Scheduled(cron = searchCron)
    @SchedulerLock(name = "searchTask", lockAtLeastForString = "${ts.least.time}", lockAtMostForString = "${ts.most.time}")
    @HystrixCommand(fallbackMethod = "execFallback", commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "${ts.most.time}")
    })
    public void execSearchTask() {
        log.info("start search recent 8 days race information ...");
        CompletableFuture.runAsync(() -> {
            raceInfoService.searchTask();
        });
    }

    public String execFallback(Integer userId, String orderNo) {
        return "爬起赛事信息失败，稍后继续！";
    }
}

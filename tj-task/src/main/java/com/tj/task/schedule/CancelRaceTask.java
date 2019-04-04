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
 * 定时扫描赛事取消数据
 * @author yangzhixin 2019-01-02
 */
@Component
@Slf4j
public class CancelRaceTask {
    private static final String cron = "0 0/1 * * * *";
    private final BetService betService;
    @Autowired
    public CancelRaceTask(BetService betService){
        this.betService = betService;
    }
    @Scheduled(cron = cron)
    @SchedulerLock(name = "betselectAllCancelRaceAndRollbackTask", lockAtLeastForString = "${schedule.least-time.selectAllCancelRaceAndRollback}", lockAtMostForString = "${schedule.most-time.selectAllCancelRaceAndRollback}")
    public void exec() {
        log.info("start to auto betselectAllCancelRaceAndRollbackTask");
        Results.Result<Void> selectAllCancelRaceAndRollback = betService.selectAllCancelRaceAndRollback();
        log.info("selectAllCancelRaceAndRollback result:{}", JSON.toJSONString(selectAllCancelRaceAndRollback));
    }
}

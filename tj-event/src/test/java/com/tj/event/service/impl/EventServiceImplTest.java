package com.tj.event.service.impl;

import com.github.pagehelper.PageInfo;
import com.tj.dto.*;
import com.tj.event.service.EventService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * @program: tj-core
 * @description: ${description}
 * @author: liang.song
 * @create: 2018-12-05-11:31
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
@ComponentScan(basePackages = {"com.tj"})
@MapperScan(basePackages = {"com.tj.event.dao"})
@ActiveProfiles(value = {"dev"})
public class EventServiceImplTest {
    @Autowired
    private EventService eventService;


    @Test
    public void flushRaceInfoList() {
        RedisRaceInfo build = RedisRaceInfo.builder()
                .category("欧青赛")
                .createTime(new Date())
                .homeTeam("萨格勒布戴拿模U19")
                .endTime(null)
                .id("ev_263675")
                .isExist(0)
//                .oddsInfos(new ArrayList<RedisRaceOddsInfo>() {
//                    {
//                        add(RedisRaceOddsInfo.builder().createTime(new Date()).odds(new BigDecimal(22)).score("1:0").updateTime(new Date()).raceId("ev_263675").teams("萨格勒布戴拿模U19:艾斯坦拿U19").build());
//                    }
//                })
                .startTime(new Date())
                .visitTeam("艾斯坦拿U19")
                .halfResult("1:0")
                .build();
        List<RedisRaceInfo> list = new ArrayList<>();
        list.add(build);
        eventService.flushRaceInfoList(list);
    }

    @Test
    public void eventFilter() {
//        Results.Result<List<RedisRaceInfo>> eventfilter = eventService.eventfilter("", null, null, 1, 20);
//        System.out.println(JSON.toJSON(eventfilter));
    }

    @Test
    public void cacheTrend() {
        RedisTeamTrendInfo build = RedisTeamTrendInfo.builder().raceId("111").team("ABC").trends(new ArrayList<String>() {
            {
                add("L");
                add("W");
            }
        }).build();
        eventService.cacheRaceTrend(build);
    }

    @Test
    public void cacheBothSide() {
        List<RedisBothSideRaceHistory> histories = new ArrayList<RedisBothSideRaceHistory>() {
            {
                add(new RedisBothSideRaceHistory() {
                    {
                        setDate(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.YYYY")));
                        setHalfResult("1:0");
                        setPanLu("输");
                        setRaceId("111");
                        setRaceName("英超");
                        setRangQiu("[+1/+1.5]");
                        setTeamA("萨格勒布戴拿模U19");
                        setTeamAScore("2");
                        setTeamB("艾斯坦拿U19");
                        setTeamBScore("0");
                    }
                });
            }
        };
        eventService.cacheBothSide(histories);
    }

    @Test
    public void cacheHistory() {
        List<RedisRaceHistory> histories = new ArrayList<RedisRaceHistory>() {
            {
                add(new RedisRaceHistory() {
                    {
                        setDate(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.YYYY")));
                        setHomeOrVisit("客队");
                        setPanLu("赢");
                        setPlayAgainst("艾斯坦拿U19");
                        setRaceId("111");
                        setRaceName("英超");
                        setRangQiu("[+1/+1.5]");
                        setResult("2:0");
                        setTeam("萨格勒布戴拿模U19");
                        setWinOrLose("赢");

                    }
                });
            }
        };
        eventService.cacheRaceHistory(histories);
    }

    @Test
    public void testRecommend() {
        eventService.eventRecommendList(1, 3);
    }

    @Test
    public void testsearchHist() {
        eventService.selectEventRebate("909541", 1);
    }

    @Test
    public void testEvent() {
        PageInfo<RaceDetailDto> raceDetailDtoPageInfo = eventService.getEventDetail(null, 1547222400000L,"墨西哥超级联赛", 1, 10, 1, 2).getResult();
        System.out.println(raceDetailDtoPageInfo.toString());
    }

//    @Test
//    public void testedisEventListR() {
//        eventService.redisEventList(1546531200000L, null, 1, 10);
//    }

//    @Test
//    public void testHistResult() {
//        crawlService.refreashOdds();
//    }
//
//    @Test
//    public void testCrawScore() throws MalformedURLException, DocumentException {
//        crawlService.crawScore();
//    }
//
//    @Test
//    public void checkEventResult() {
//        crawlService.checkEventResult();
//    }

    @Test
    public void testRebate() {
        eventService.rebateEventList("257425");
    }

//    @Test
//    public void testAutoDive() {
//        eventService.autoRebateDive(null, null);
//    }

    @Test
    public void testDe() {
//        eventService.getEventDetail(null, 1547395200000, null, 1,10, 2);
//        Results.Result<PageInfo<RaceDetailDto>> dd = eventService.getEventDetail(null, 1547395200000L, null, 1,10, null, 2);
////        PageInfo<RaceDetailDto> pp= dd.getResult();
//        System.out.println(dd.getResult() );
        ;
        System.out.println(eventService.redisEventList(1547488800000L, 1548259320000L, "2", 1,20).getResult().toString());
    }


    @Test
    public void TestSub() {
        eventService.selectDiveRule();
    }

    @Test
    public void TestOpenStatus() {
        eventService.updateRebateOpenStatus(1, 5781, BigDecimal.ONE);
    }

    @Test
    public void testSubmit() {
//        {"rebateId":2965,"":,"":,"":null,"oppositeOdd":null,"initRebateRatio":null,"score":"1 - 0","openStatus":1},
        List<OperateDto> list = new LinkedList<>();
        OperateDto operateDto = OperateDto.builder()
                .rebateId(2965)
                .rebateRatio(new BigDecimal(0.0681))
                .validAmount(new BigDecimal(10000))
                .oppositeRebate(null)
                .oppositeOdd(null)
                .initRebateRatio(null)
                .score("1 - 0")
                .openStatus(1).build();
        list.add(operateDto);
        eventService.submitOperate(list);
    }

    @Test
    public void testFRebate() {
        eventService.selectEventRebate( "918661",  1);
    }
}
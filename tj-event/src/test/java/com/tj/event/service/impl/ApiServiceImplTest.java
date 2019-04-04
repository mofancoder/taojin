package com.tj.event.service.impl;

import com.tj.event.factory.RaceHandlerFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
@ComponentScan(basePackages = {"com.tj"})
@MapperScan(basePackages = {"com.tj.event.dao"})
@ActiveProfiles(value = {"dev"})
public class ApiServiceImplTest {
//    @Autowired
//    private ApiService apiService;
//    @Autowired
//    private CrawlService crawlService;

    @Value("${race.datasource.style}")
    private String style;

    @Autowired
    public RaceHandlerFactory raceHandlerFactory;

    @Test
    public void testSearchTask() {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>:" + style);
        raceHandlerFactory.getRaceHandleByName(style).searchTask();
    }

    @Test
    public void testRealTask() {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>:" + style);
        raceHandlerFactory.getRaceHandleByName(style).realMonitorTask();
    }
}
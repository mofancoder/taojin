package com.tj.event.service.impl;

import com.tj.event.service.ExcelService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;

@SpringBootTest
@RunWith(SpringRunner.class)
@ComponentScan(basePackages = {"com.tj"})
@MapperScan(basePackages = {"com.tj.event.dao"})
@ActiveProfiles(value = {"dev"})
public class ExcelServiceImplTest {

    @Autowired
    private ExcelService excelService;



    @Test
    public void  importScoreInfoFromExcel() {

        String file = "H:\\05_公司\\Test.xlsx";
        excelService.importScoreInfoFromExcel2(new File(file));
    }


//    @Test
//    public void testCrawScore() throws MalformedURLException, DocumentException {
//        CrawlService crawlService = new CrawlRaceServiceImpl();
//        crawlService.crawScore();
//    }
//
//    @Test
//    public void testCrawLiveResult() {
//        CrawlService crawlService = new CrawlRaceServiceImpl();
//        crawlService.crawLiveResult();
//    }
//
//    @Test
//    public void testHistResult() {
//        CrawlService crawlService = new CrawlRaceServiceImpl();
//        crawlService.refreashOdds();
//    }

}

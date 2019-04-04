package com.tj.event.service.impl;

import com.google.common.collect.Lists;
import com.tj.dto.RedisRaceInfo;
import com.tj.dto.RedisRaceRebateInfo;
import com.tj.event.domain.RaceInfo;
import com.tj.event.domain.RaceInfoExample;
import com.tj.event.factory.RaceHandleBase;
import com.tj.event.service.RaceService;
import com.tj.event.singleton.SingleDriver;
import com.tj.event.util.CrawlUtils;
import com.tj.event.util.WinInfo;
import com.tj.util.Results;
import com.tj.util.enums.RaceStatusEnum;
import com.tj.util.time.TimeUtil;
import io.jsonwebtoken.lang.Collections;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Configuration
@Data
@Slf4j
@Service("crawl")
public class CrawlRaceServiceImpl extends RaceHandleBase implements RaceService {

    @Value("${crawl.url.score}")
    private String SITEURL;
    @Value("${crawl.url.live}")
    private String LIVEURL;

    @Override
    public Results.Result realMonitorTask() {
        checkEvent();
        checkResult();
        return Results.SUCCESS;
    }

    @Override
    public Results.Result searchTask() {
        super.searchHist();
        return Results.SUCCESS;
    }

    /**
     * 检查数据库中未结束的比赛状态
     * @return
     */
    @Transactional
    public Results.Result checkResult() {
        RaceInfoExample example = new RaceInfoExample();
        RaceInfoExample.Criteria or = example.or().andEndTimeIsNull();
        List<RaceInfo> list = raceInfoMapper.selectByExample(example);

        if (Collections.isEmpty(list)) {
            return Results.SUCCESS;
        }
        List<RaceInfo> results = crawLastResult().getResult();
        for (RaceInfo raceInfo : list) {
            for (RaceInfo result : results) {
                if (raceInfo.getHomeTeam().equals(result.getHomeTeam())
                        && raceInfo.getVisitTeam().equals(result.getVisitTeam())
                        && raceInfo.getStartTime().equals(result.getStartTime())) {
                    //update
                    Integer status = result.getRaceStatus();
                    raceInfo.setEndTime(result.getEndTime());
                    raceInfo.setHalfResult(result.getHalfResult());
                    raceInfo.setRaceStatus(status);
                    raceInfo.setWinResult(result.getWinResult());
                    raceInfo.setWinTeam(result.getWinTeam());
                    raceInfo.setWinType(result.getWinType());

                    if (RaceStatusEnum.end.getCode() == result.getRaceStatus() ) {
                        raceInfo.setEndTime(new Date());
                    }
                    raceInfoMapper.updateByPrimaryKey(raceInfo);
                    log.debug("update race result :" + raceInfo.getId() + " ==>" + raceInfo.toString());
                    break;
                }
            }
        }
        return Results.SUCCESS;
    }

    /**
     * 爬取最近几天结束的赛事
     * @return
     */
    private Results.Result<List<RaceInfo>> crawLastResult() {
        WebDriver driver = null;
        try {
            org.jsoup.nodes.Document document = openSite(LIVEURL, true);
            if (document == null) {
                log.error("can not get any doc from remote website");
                return new Results.Result<>(Results.SUCCESS, new ArrayList<>());
            }
            System.out.println();

            Element tbody = document.select("div.overflow_y").first();
            Thread.sleep(2000L);
            Elements trs = tbody.getElementsByTag("tr"); //每一行
            Elements elements = new Elements();
            String date = ""; //一个日期下会有多场比赛，全局保存

            List<RaceInfo> raceInfoList = new ArrayList<>();

            for (Element row : trs) {
                if (!(elements = row.select(".date")).isEmpty()) {//跳过date
                    date = replaceDate(elements.first().select("td").text()).replace(":00", "");
                    continue;
                }
                if (!(elements = row.select(".styleone")).isEmpty()) {
                    Elements tds = elements.first().getElementsByTag("td");
                    RedisRaceInfo crawlRace = new RedisRaceInfo();
                    crawlRace.setCategory(tds.get(0).text());
                    crawlRace.setStartTime(TimeUtil.getDateFormat(date + tds.get(1).text() + ":00", "yyyy-MM-dd HH:mm:ss"));

                    String[] temp = tds.get(3).text().split(" ");
                    crawlRace.setHomeTeam( (temp.length > 1) ? temp[1].trim() : temp[0].trim() ) ;
                    temp = tds.get(5).text().split(" ");
                    crawlRace.setVisitTeam(temp[0].trim());

                    crawlRace.setWinResult(tds.get(4).text());
                    crawlRace.setWinType(CrawlUtils.getWinType(crawlRace.getWinResult()));
                    crawlRace.setRaceResult(CrawlUtils.getStatusIndex(tds.get(7).text()));
                    String text = tds.get(8).text();
                    crawlRace.setHalfResult((text.length() > 7) ? text.substring(0, text.indexOf(" ", 6)) : text);

                    WinInfo winInfo = new WinInfo(new String[]{crawlRace.getHomeTeam(), crawlRace.getVisitTeam()}, crawlRace.getWinResult(), "").compare();

                    RaceInfo raceInfo = new RaceInfo();
                    BeanUtils.copyProperties(crawlRace, raceInfo);
                    raceInfo.setRaceStatus(crawlRace.getRaceResult());
                    if (!(raceInfo.getRaceStatus() == RaceStatusEnum.processing.getCode())) {//正在进行中的比赛比分是未完赛比分
                        raceInfo.setWinTeam(winInfo.getWinTeam());
                        raceInfo.setWinResult(crawlRace.getWinResult());
                    }
                    raceInfo.setEndTime(new Date());

                    raceInfoList.add(raceInfo);
                    System.out.println(raceInfo.toString());
                }
            }
            return new Results.Result<>(Results.SUCCESS, raceInfoList);
        } catch (Exception e) {
            log.error("try to parse doc got exception", e);
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
        return new Results.Result<>(Results.SUCCESS, null);
    }

    /**
     * 正在进行中的比赛
     * @return
     */
    private Results.Result<List<RedisRaceInfo>> crawLiveResult() {
        Integer retryCount = 3;
        org.jsoup.nodes.Document document = null;
        try {
            document = openSite(LIVEURL, false);
        } catch (Exception e) {
            log.error("get living race status fail.", e);
            while (retryCount >= 0) {
                document = openSite(LIVEURL, false);
                if (document != null) {
                    break;
                }
                retryCount--;
            }
        }

        List<RedisRaceInfo> redisRaceInfoList = new ArrayList<>();
        Element tbody = document.select("#tableMain").first();  //.getElementsByTag("tbody").first();
        Elements trs = tbody.getElementsByTag("tr"); //每一行
        Elements dateElements = new Elements();
        String date = ""; //一个日期下会有多场比赛，全局保存
        for (Element row : trs) {
            if (!(dateElements = row.select(".styledate")).isEmpty()) {//跳过date
                Elements td = dateElements.first().select("td").select("font");
                date = replaceDate(td.text());
                continue;
            }
            if (!(dateElements = row.select(".FBRow")).isEmpty()) {
                Element tr = dateElements.first();
                Elements aligns = tr.select("td[align]");
                if (aligns.isEmpty() || aligns.size() < 2) {
                    continue;
                }
                RedisRaceInfo crawlRace = new RedisRaceInfo();
                crawlRace.setId(tr.select("label").attr("for"));
                crawlRace.setCategory(tr.select("td[bgcolor]").select("font").text());
                crawlRace.setStartTime(TimeUtil.getDateFormat(date.replace(":00", "") + aligns.get(2).text() + ":00", "yyyy-MM-dd HH:mm:ss"));

                String[] temp = tr.select("td.NoLeftBorder").first().text().split(" ");
                crawlRace.setHomeTeam( (temp.length > 1) ? temp[1].trim() : temp[0].trim() ) ;
                temp = tr.select("td.NoRightBorder").last().text().split(" ");
                crawlRace.setVisitTeam(temp[0].trim());

                crawlRace.setWinResult(aligns.get(3).text());
                crawlRace.setWinType(CrawlUtils.getWinType(crawlRace.getWinResult()));
                crawlRace.setRaceResult(CrawlUtils.getStatusIndex(aligns.get(4).text()));
                crawlRace.setHalfResult(tr.select("td[colspan='2']").text());

                WinInfo winInfo = new WinInfo(new String[]{crawlRace.getHomeTeam(), crawlRace.getVisitTeam()}, crawlRace.getWinResult(), "").compare();
                crawlRace.setWinTeam(winInfo.getWinTeam());

                redisRaceInfoList.add(crawlRace);
            }
        }
        log.debug("processing race :" + redisRaceInfoList.toArray());
        return new Results.Result<>(Results.SUCCESS, redisRaceInfoList);
    }

    /**
     * 打开指定页面
     */
    public org.jsoup.nodes.Document openSite(String siteUrl, Boolean clickFlag) {
        int tryCount = 0;

        WebDriver driver = null;
        WebElement element = null;

        try {
            driver = SingleDriver.getInstance();
            driver.get(siteUrl);//打开指定的网站

            driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);//WebDriver自带了一个智能等待的方法。
            //driver.manage().timeouts().pageLoadTimeout(80, TimeUnit.SECONDS);

            if (siteUrl.contains("livescore")) {
                Thread.sleep(3000L);
                if (clickFlag) {
                    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
                    element = driver.findElement(By.linkText("完场赛果"));
//                    element = driver.findElement(By.xpath("//a[@href='javascript:GetResult_scoer();']"));
                    element.click();
                    Thread.sleep(3000L);
                }
                return Jsoup.parse(driver.getPageSource());
            }
            try {
                element = driver.findElement(By.xpath("//option[@value='8']"));
            } catch (Exception e) {
                log.error("Click Page to handle elements fail.", e);
                while (tryCount < 3) {
                    Thread.sleep(5000L);//5秒之后再次获取
                    element = driver.findElement(By.xpath("//option[@value='8']"));
                    if (element != null) {
                        tryCount = 0;
                        break;
                    }
                    tryCount++;
                }
            }
        } catch (Exception e) {
            log.error("get living race status fail.", e);
//            while (retryCount >= 0) {
//                document = openSite(LIVEURL, false);
//                if (document != null) {
//                    break;
//                }
//                retryCount--;
//            }
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
        element.click();
        return Jsoup.parse(driver.getPageSource());
    }


    /**
     * 添加比赛的所有比分赔率 eg;1:0 4.2
     *
     * @param id
     * @param oddsInfos
     * @param oddsLinks
     * @param teamName
     * @param oddsInfoType
     * @return
     */
    private Results.Result<List<RedisRaceRebateInfo>> addRaceRebate(String id, List<RedisRaceRebateInfo> oddsInfos, Elements oddsLinks, String[] teamName, String oddsInfoType) {
        String rate;
        String score;
        RedisRaceRebateInfo raceRebate;
        for (Element odds : oddsLinks) {
            //<a title="仙台维加泰1:0" href="javascript:addOpenerOdds(5.2,'仙台维加泰 1:0','仙台维加泰 vs 山形山神','日本天皇杯','波胆')">5.2</a>
            rate = odds.text();
            int index = odds.attr("title").length();
            score = odds.attr("title").substring(index - 3, index);
            WinInfo winInfo = new WinInfo(teamName, oddsInfoType, score, "").invoke();

            BigDecimal bRate = new BigDecimal(Double.parseDouble(rate));
            raceRebate = RedisRaceRebateInfo.builder()
                    .raceId(id)
                    .teams(teamName[0] + ":"+teamName[1])
                    .score(winInfo.getScore())
                    .startOdds(bRate.setScale(2, BigDecimal.ROUND_FLOOR))
                    .normalOdds(bRate.setScale(2, BigDecimal.ROUND_FLOOR))
                    .createTime(new Date())
                    .build();
            oddsInfos.add(raceRebate);
        }
        return new Results.Result<>(Results.SUCCESS, oddsInfos);
    }

    private String getTextByClass(Element td, String className, String textCount) {
        Elements temp = new Elements();
        if ((temp = td.getElementsByClass(className).first().select("font")).isEmpty())
            return "";
        if ("ALL".equals(textCount))//ALL 代表 temp 不止一个元素
            return temp.text();
        return temp.first().text();
    }

    private String replaceDate(String date) {
        //title="2018 年 12 月 06 日 (undefined) 01:00"
        int index = date.indexOf("(");
        StringBuffer temp = new StringBuffer(date.substring(0, index));
        index = date.indexOf(")") + 1;
        temp.append(date.substring(index, date.length()))
                .append(":00");
        return temp.toString().replace(" ", "").replace("年", "-")
                .replace("月", "-").replaceFirst("日", " ");
    }

    static {
        //禁用Selenium的日志
        Logger logger = Logger.getLogger("com.gargoylesoftware.htmlunit");
        logger.setLevel(Level.OFF);
    }

//



    /**
     * 刷新赛事数据
     *
     * @return
     */
    private Results.Result checkEvent() {
        List<RedisRaceInfo> liveRaceList = crawLiveResult().getResult();
        List<RedisRaceInfo> scoreList = crawScore().getResult();

        List<RedisRaceInfo> redisRaceInfoList = Lists.newArrayList();
        if (scoreList == null || scoreList.isEmpty())
            return Results.QUERY_FAILURE; // 爬取失败

        org.dom4j.Element root = handleDoc(TRADEINFO);
        for (RedisRaceInfo scoreInfo : scoreList) {
            analysisTrendInfo(scoreInfo.getId(), root);
            analysisRaceHistory(scoreInfo);

            for (RedisRaceInfo liveRace : liveRaceList) {
                if (liveRace.getId().equals(scoreInfo.getId())) {
                    RedisRaceInfo saveRace = new RedisRaceInfo();
                    BeanUtils.copyProperties(scoreInfo, saveRace);
                    saveRace.setRebates(scoreInfo.getRebates());

                    saveRace.setEndTime(liveRace.getEndTime());
                    saveRace.setRaceResult(liveRace.getRaceResult());
                    saveRace.setWinTeam(liveRace.getWinTeam());
                    saveRace.setWinType(liveRace.getWinType());
                    saveRace.setHalfResult(liveRace.getHalfResult());
                    saveRace.setWinResult(liveRace.getWinResult());

                    redisRaceInfoList.add(saveRace);
                    break;
                }
            }
        }
        eventService.flushRaceInfoList(redisRaceInfoList);

        realMonitorTask(); // 检查比赛结果
        return Results.SUCCESS;
    }


    /**
     * 爬取赛事信息、赔率
     *
     * @return
     */
    private Results.Result<List<RedisRaceInfo>> crawScore() {
        WebDriver driver = null;
        try {
            org.jsoup.nodes.Document document = openSite(SITEURL, true);

            Element tbody = document.getElementsByTag("tbody").first();
            Elements trs = tbody.getElementsByTag("tr"); //每一行

            Elements dateElements = new Elements();
            Elements oddsElements = new Elements();

            List<RedisRaceInfo> redisRaceInfoList = new LinkedList<RedisRaceInfo>();
            RedisRaceInfo redisRaceInfo = new RedisRaceInfo();

            Iterator it = trs.iterator();
            int count = 0;
            while (it.hasNext()) {
                Element tr = (Element) it.next();
                if (!(dateElements = tr.select(".styletitle")).isEmpty()) {//跳过title
                    continue;
                }
                if (!(dateElements = tr.select(".styledate")).isEmpty()) {//跳过date
                    continue;
                }
                if (!(oddsElements = tr.select(".styleoddss,.styleoddss2")).isEmpty()) {
                    List<RedisRaceRebateInfo> rebateInfos = new ArrayList<RedisRaceRebateInfo>();
                    Element td = oddsElements.first();
                    //赛事类别
                    String event = getTextByClass(td, "styleevent", "");
                    Element e = td.getElementsByClass("styletime").first();
                    String startTime = replaceDate(e.attr("title"));

                    //id
                    Elements links = td.select("a");//  "javascript:getPredictionTextAndCount(262707,'event');"
                    String id = links.get(17).attr("href").substring(24, 30);//6位
                    //对战团队
                    String[] names = getTextByClass(td, "styleteam", "ALL").split(" ");

                    Elements oddsLinks = td.select("a").select("[href^=javascript:addOpenerOdds]");
                    rebateInfos = addRaceRebate(id, rebateInfos, oddsLinks, names, "0").getResult(); //0 : 主场方赔率

                    redisRaceInfo = RedisRaceInfo.builder()
                            .id(id)
                            .category(event)
                            .startTime(TimeUtil.getDateFormat(startTime, "yyyy-MM-dd HH:mm:ss"))
                            .homeTeam(names[0])
                            .visitTeam(names[1])
                            .createTime(new Date())
                            .build();

                    tr = (Element) it.next(); //比分 1:0 类型一个赛事占用两个 tr 行, 游标控制一次循环步长为2
                    td = tr.select(".styleoddss,.styleoddss2").first();
                    oddsLinks = td.select("a").select("[href^=javascript:addOpenerOdds]");
                    rebateInfos = addRaceRebate(id, rebateInfos, oddsLinks, names, "1").getResult(); //1: 客场方赔率
                    redisRaceInfo.setRebates(rebateInfos);

                    redisRaceInfoList.add(redisRaceInfo);
                }
            }
            eventService.flushRaceInfoList(redisRaceInfoList); //添加，空字段后边补上
            return new Results.Result<>(Results.SUCCESS, redisRaceInfoList);
        } catch (Exception e) {
            log.error("try to parse doc got exception", e);
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
        return new Results.Result<>(Results.SUCCESS, null);
    }

}

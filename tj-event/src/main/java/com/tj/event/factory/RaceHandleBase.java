package com.tj.event.factory;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.tj.dto.RedisBothSideRaceHistory;
import com.tj.dto.RedisRaceHistory;
import com.tj.dto.RedisRaceInfo;
import com.tj.dto.RedisTeamTrendInfo;
import com.tj.event.dao.CategoryMappingMapper;
import com.tj.event.dao.RaceInfoMapper;
import com.tj.event.dao.TeamMappingMapper;
import com.tj.event.domain.CategoryMapping;
import com.tj.event.domain.TeamMapping;
import com.tj.event.service.EventService;
import com.tj.util.Results;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.xml.sax.InputSource;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Set;

@Slf4j
public class RaceHandleBase {

    @Autowired
    protected EventService eventService;
    @Resource
    protected RaceInfoMapper raceInfoMapper;
    @Resource
    protected TeamMappingMapper teamMappingMapper;
    @Resource
    protected CategoryMappingMapper categoryMappingMapper;

    @Value("${crawl.url.pre_hist}")
    private String INDEXHIST;
    @Value("${crawl.url.trend}")
    public String TRADEINFO;
    @Value("${crawl.url.oddeven}")
    private String ODDEVEN;

    /**
     * 增加主客方的历史对战胜负状态
     * @param raceId
     * @param root
     * @return
     */
    protected Results.Result<RedisRaceInfo> analysisTrendInfo(String raceId, org.dom4j.Element root) {
        List<org.dom4j.Element> list = root.elements("PREDICTIONS");
        //遍历属性节点
        for (org.dom4j.Element ele : list) {
            if (raceId.equals(ele.elementTextTrim("PREDICTION_ID"))) {
                List<String> trends = splitString(ele.elementTextTrim("HOME_RECENT"));
                trends.addAll(splitString(ele.elementTextTrim("HOME_HANDICAP")));
                RedisTeamTrendInfo trendInfo = RedisTeamTrendInfo.builder()
                        .raceId(ele.elementTextTrim("PREDICTION_ID"))
                        .team(ele.elementTextTrim("HOME"))
                        .trends(trends)
                        .build();
                eventService.cacheRaceTrend(trendInfo);

                List<String> awayTrends = splitString(ele.elementTextTrim("AWAY_RECENT"));
                awayTrends.addAll(splitString(ele.elementTextTrim("AWAY_HANDICAP")));
                RedisTeamTrendInfo awayTrendInfo = RedisTeamTrendInfo.builder()
                        .raceId(ele.elementTextTrim("PREDICTION_ID"))
                        .team(ele.elementTextTrim("AWAY"))
                        .trends(awayTrends)
                        .build();
                eventService.cacheRaceTrend(awayTrendInfo);

                RedisRaceInfo redisRaceInfo = RedisRaceInfo.builder()
                        .id(raceId)
                        .homeTeam(ele.elementTextTrim("HOME"))
                        .visitTeam(ele.elementTextTrim("AWAY"))
                        .build();
                return new Results.Result(Results.SUCCESS, redisRaceInfo);
            }
        }
        return new Results.Result(Results.SUCCESS, null);
    }

    /**
     * 对战双方的历史战绩
     * @param raceInfo
     * @return
     */
    protected Results.Result analysisRaceHistory(RedisRaceInfo raceInfo) {
        org.dom4j.Element root = handleDoc(INDEXHIST + raceInfo.getId() + ".xml");
        Set<String> types = getRaceType(root).getResult();
        List<Attribute> list = root.element("INFO_LIST").element("INFO").attributes();
        //遍历属性节点
        for(Attribute attribute : list){
            if ("RECENT_MATCHUP".equals(attribute.getName())) {
                String[] s = packAttr(types, attribute);
                addBothRace(raceInfo, s);
            }
            if ("HOME_RECENT".equals(attribute.getName())) {
                String[] s = packAttr(types, attribute);
                addRace(raceInfo, s, raceInfo.getHomeTeam(), raceInfo.getHomeTeam() + "|" + raceInfo.getVisitTeam());
            }
            if ("AWAY_RECENT".equals(attribute.getName())) {
                String[] s = packAttr(types, attribute);
                addRace(raceInfo, s, raceInfo.getVisitTeam(), raceInfo.getHomeTeam() + "|" + raceInfo.getVisitTeam());
            }
        }
        return new Results.Result(Results.SUCCESS, null);//raceInfo
    }

    /**
     * 战队最近6 场对战结果
     * @param raceInfo
     * @param s
     * @param homeTeam
     */
    private void addRace(RedisRaceInfo raceInfo, String[] s, String homeTeam, String teams) {
        List<RedisRaceHistory> raceList = Lists.newLinkedList();
        for (int i = 1; i < s.length; i++) {
            String[] raceTemp = s[i].split("-");
            String[] race = new String[8];
            for (int arrayLength = 0; arrayLength < raceTemp.length; arrayLength++) {
                if (arrayLength >= 8) {
                    break;
                }
                race[arrayLength] = raceTemp[arrayLength];
            }
            if (raceTemp.length < 8) {
                if (raceTemp.length < 7) {
                    race[6] = "无";
                }
                race[7] = "无";
            }

            RedisRaceHistory raceHistory = RedisRaceHistory.builder()
                    .raceId(raceInfo.getId())
                    .raceName(race[0])
                    .date(race[1])
                    .team(homeTeam)
                    .homeOrVisit(race[2])
                    .winOrLose(race[3])
                    .playAgainst(race[5])
                    .result(race[4])
                    .rangQiu(race[6])
                    .panLu(race[7])
                    .teams(teams)
                    .build();
            raceList.add(raceHistory);
        }
        log.debug("recent six race result:", raceList.toArray());
        eventService.cacheRaceHistory(raceList);
    }

    /**
     * 对战双方前几次对战结果
     * @param raceInfo
     * @param s
     */
    private void addBothRace(RedisRaceInfo raceInfo, String[] s) {
        List<RedisBothSideRaceHistory> bothSideRace = Lists.newLinkedList();
        for (int i=1; i<s.length; i++) {
            String [] race = s[i].split("-");
            if (race.length < 2) {
                continue;
            }
            RedisBothSideRaceHistory bothSideRaceHistory = RedisBothSideRaceHistory.builder()
                    .raceId(raceInfo.getId())
                    .raceName(race[0])
                    .date(race[1])
                    .teamA(raceInfo.getHomeTeam())
                    .teamB(raceInfo.getVisitTeam())
                    .teamAScore(race[3].equals("(主)") ? race[2] : race[3])
                    .teamBScore(race[4].equals("(主)") ? race[3] : race[4])
                    .halfResult(race[5])
                    .rangQiu(race[7])
                    .panLu(race[6])
                    .build();
            bothSideRace.add(bothSideRaceHistory);
        }
        log.debug("Both history race result:", bothSideRace.toArray());
        eventService.cacheBothSide(bothSideRace);
    }

    /**
     * 切割 attr 的 value ，返回格式化的数据
     * @param types
     * @param attribute
     * @return
     */
    private String[] packAttr(Set<String> types, Attribute attribute) {
        String [] s = attribute.getValue().split(" ");
        StringBuffer info = new StringBuffer();

        for (String type : types) {
            for (int i = 0; i < s.length; i++) {
                if (s[i].contains(type)) {
                    s[i] = ";" + type;
                }
            }
        }
        for (int i=0; i<s.length; i++) {
            info.append(s[i]).append("-");
        }
        String s1 = info.toString();
        s = s1.trim().split(";");
        return s;
    }

    private List<String> splitString(String s) {
        if (s.length() < 0) {
            return java.util.Collections.EMPTY_LIST;
        }
        List<String> result = Lists.newArrayList();
        for (int i=0; i<s.length(); i++) {
            result.add(String.valueOf(s.charAt(i)));
        }
        return result;
    }

    /**
     * 所有比赛的赛事类别
     * @param root
     * @return
     */
    protected Results.Result<Set<String>> getRaceType(org.dom4j.Element root) {
        List<org.dom4j.Element> typeList = root.element("TOURNAMENT_TYPE_LIST").elements("TOURNAMENT_TYPE");
        Set<String> types = Sets.newHashSet();
        for (org.dom4j.Element e : typeList) {
            types.add(String.valueOf(e.attribute("TOURNAMENT_SHORT").getValue()));
        }
        return new Results.Result(Results.SUCCESS, types);
    }


    /**
     * 爬取历史赛事
     *
     * @return
     */
    protected Results.Result searchHist() {
        org.dom4j.Element root = handleDoc(ODDEVEN);
        List<org.dom4j.Element> list = root.elements("Fixture");
        org.dom4j.Element tradeRoot = handleDoc(TRADEINFO);
        for (org.dom4j.Element ele : list) {
            List<Attribute> attrs = ele.attributes();
            attrs.forEach(v -> {
                if (!"id".equals(v.getName())) {
                    return;
                }
                String raceId = v.getValue();
                RedisRaceInfo redisRace = analysisTrendInfo(raceId, tradeRoot).getResult();
                if (redisRace == null) {
                    return;
                }
                analysisRaceHistory(redisRace);
            });
        }
        return Results.SUCCESS;
    }

    /**
     * 爬取赛事映射关系
     *
     * @return
     */
    protected Results.Result searchRaceMapping() {
        org.dom4j.Element root = handleDoc("https://www.macauslot.com/fjt/content/data/soccer/xml/odds/odds_config.xml");
        //    https://www.macauslot.com/fjt/content/data/soccer/images/team/team158.jpg
        List<org.dom4j.Element> list = root.elements("Fixture");

        for (org.dom4j.Element ele : list) {
            TeamMapping homeMapping = new TeamMapping();
            TeamMapping visitMapping = new TeamMapping();
            CategoryMapping categoryMapping = new CategoryMapping();

            List<Attribute> attrs = ele.attributes();
            attrs.forEach(v->{
                switch (v.getName()) {
                    case "tid":
                        categoryMapping.setId(v.getValue());
                        break;
                    case "tt":
                        categoryMapping.setComplexFullCategory(v.getValue());
                        break;
                    case "st":
                        categoryMapping.setSimpleFullCategory(v.getValue());
                        break;
                    case "et":
                        categoryMapping.setEnglishFullCategory(v.getValue());
                        break;
                    case "ts":
                        categoryMapping.setComplexShortCategory(v.getValue());
                        break;
                    case "ss":
                        categoryMapping.setSimpleShortCategory(v.getValue());
                        break;
                    case "es":
                        categoryMapping.setEnglishShortCategory(v.getValue());
                        break;

                    case "thid":
                        homeMapping.setId(v.getValue());
                        break;
                    case "th":
                        homeMapping.setComplexTeam(v.getValue());
                        break;
                    case "sh":
                        homeMapping.setSimpleTeam(v.getValue());
                        break;
                    case "eh":
                        homeMapping.setEnglishTeam(v.getValue());
                        break;
                    case "himg":
                        homeMapping.setImgPath(v.getValue());
                        break;

                    case "taid":
                        visitMapping.setId(v.getValue());
                        break;
                    case "ta":
                        visitMapping.setComplexTeam(v.getValue());
                        break;
                    case "sa":
                        visitMapping.setSimpleTeam(v.getValue());
                        break;
                    case "ea":
                        visitMapping.setEnglishTeam(v.getValue());
                        break;
                    case "aimg":
                        visitMapping.setImgPath(v.getValue());
                        break;

                }
            });

            TeamMapping team = teamMappingMapper.selectByPrimaryKey(homeMapping.getId());
            if (team == null) {
                teamMappingMapper.insert(homeMapping);
            }
            team = teamMappingMapper.selectByPrimaryKey(visitMapping.getId());
            if (team == null) {
                teamMappingMapper.insert(visitMapping);
            }
            CategoryMapping category = categoryMappingMapper.selectByPrimaryKey(categoryMapping.getId());
            if (category == null) {
                categoryMappingMapper.insert(categoryMapping);
            }
        }
        return Results.SUCCESS;
    }

    /**
     * 向指定URL发送GET方法的请求
     * 
     * @param url  发送请求的URL
     */
    public String sendGet(String url){
        String result = "";
        try{
            String urlName = "";
            urlName = new String((url).getBytes("UTF-8"),"UTF-8");
            URL _url = new URL(urlName);
            HttpURLConnection connection = (HttpURLConnection)_url.openConnection();
            //设置通用的请求属性  
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            //设置连接超时10s
            connection.setConnectTimeout(20 * 1000);
            //读取数据超时10s
            connection.setReadTimeout(10*1000);
            connection.setUseCaches(false);
            connection.connect();
//            Thread.sleep(3L);
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(),"UTF-8"));
            String line;
            while ((line = in.readLine())!= null){
                result += line;
            }
            in.close();
            connection.disconnect();
        }catch(Exception e){
            System.out.println("没有结果！" + e);
        }
        return result;
    }

    /**
     * 获取网络 xml
     * @param path
     * @return
     */
    public org.dom4j.Element handleDoc(String path) {
        URL url;
        Document document = null;
        try {
            String result = sendGet(path);
            if(StringUtils.isNotEmpty(result)) {
                SAXReader reader = new SAXReader();
                reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
                StringReader sr = new StringReader(result);
                InputSource is = new InputSource(sr);
                document = reader.read(is);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return document.getRootElement();
    }

}

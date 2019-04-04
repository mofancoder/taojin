package com.tj.event.service.impl;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.handler.impl.ExcelDataHandlerDefaultImpl;
import com.google.common.collect.Lists;
import com.tj.event.dao.RaceInfoMapper;
import com.tj.event.domain.*;
import com.tj.event.service.ExcelService;
import com.tj.event.util.CrawlUtils;
import com.tj.util.Results;
import com.tj.util.time.TimeUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ExcelServiceImpl implements ExcelService{

    @Resource
    private RaceInfoMapper raceInfoMapper;

//    @Resource
//    private RaceOddsInfoMapper raceOddsInfoMapper;

    @Override
    public Results.Result importScoreInfoFromExcel(MultipartFile file) {
        Class clazz = ExcelRace.class;
        Field[] fields = clazz.getDeclaredFields();

        List<ExcelRace> excelRaces = Lists.newLinkedList();
        try {
            ImportParams params = new ImportParams();
            params.setTitleRows(1);
            params.setHeadRows(1);
            params.setNeedVerfiy(true);  //校验

            excelRaces = ExcelImportUtil.importExcel(file.getInputStream(), ExcelRace.class, params);
        } catch (Exception e) {
            e.printStackTrace();
            return Results.PARAMETER_INCORRENT;
        }

        for (ExcelRace excelRace : excelRaces) {
            RaceInfo raceInfo = new RaceInfo();
//            RaceOddsInfo raceOddsInfo = new RaceOddsInfo();

            raceInfo.setId(excelRace.getId());
            raceInfo.setCategory(excelRace.getCategory());
            raceInfo.setStartTime(TimeUtil.getDateFormat(excelRace.getStartTime(), "yyyy-MM-dd HH:mm:ss"));
            raceInfo.setHomeTeam(excelRace.getHomeTeam());
            raceInfo.setVisitTeam(excelRace.getVisitTeam());
            raceInfo.setCreateTime(new Date());
            raceInfo.setShelvesStatus("是".equals(excelRace.getShelvesStatus()) ? 1 : 0);
//            raceInfo.setRaceResult(CrawlUtils.getStatusIndex(excelRace.getStatus()));
            raceInfo.setWinTeam(excelRace.getWinTeam());
            raceInfo.setWinResult(excelRace.getWinResult());
            raceInfo.setWinType(CrawlUtils.getWinType(excelRace.getWinResult()));
            raceInfo.setHalfResult(excelRace.getHalfResult());
            raceInfo.setIsRecommend("是".equals(excelRace.getIsRecommend())? 1: 0);
            raceInfo.setWeight(Double.parseDouble(raceInfo.getWeight()+"") > 0.0 ? raceInfo.getWeight(): new BigDecimal(0));
            raceInfoMapper.insert(raceInfo);

            System.out.println(excelRace.toString());
            for (Field field : fields) {
                if (field.getName().startsWith("score")) {
                    System.out.println("方法名：" + field.getName());
//                    raceOddsInfo = packOdds(excelRace, field.getName());
//                    raceOddsInfoMapper.insert(raceOddsInfo);
                }
            }
        }
        return Results.SUCCESS;
    }


//    public RaceOddsInfo packOdds(ExcelRace excelRace, String score) {
//        RaceOddsInfo raceOddsInfo = new RaceOddsInfo();
//        raceOddsInfo.setRaceId(excelRace.getId());
////        raceOddsInfo.setWinTeam(excelRace.getWinTeam());
//
//        String odds = (String) getFieldValue(excelRace, score);
//        BigDecimal rate = new BigDecimal(Double.parseDouble(odds));
//        raceOddsInfo.setScore(score.replace("score", "").replace("to", ":"));
//        raceOddsInfo.setOdds(rate.setScale(2, BigDecimal.ROUND_FLOOR));
//        raceOddsInfo.setCreateTime(new Date());
//        return  raceOddsInfo;
//    }

    /**
     * 得到Workbook对象
     * @param file
     * @return
     * @throws IOException
     */
    public Workbook getWorkBook(MultipartFile file) throws IOException{
        //这样写  excel 能兼容03和07
        InputStream is = file.getInputStream();
        Workbook hssfWorkbook = null;
        try {
            hssfWorkbook = new HSSFWorkbook(is);
        } catch (Exception ex) {
            is =file.getInputStream();
            hssfWorkbook = new XSSFWorkbook(is);
        }
        return hssfWorkbook;
    }

    /**
     * 通过反射，用属性名称获得属性值
     * @param thisClass 需要获取属性值的类
     * @param fieldName 该类的属性名称
     * @return
     */
    private Object getFieldValue(Object thisClass, String fieldName) {
        Object value = new Object();
        Method method = null;
        try {
            String methodName = toFirstUpcase(fieldName);
            method = thisClass.getClass().getMethod("get" + methodName);
            value = method.invoke(thisClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public static String toFirstUpcase(String name) {
        char[] cs = name.toCharArray();
        cs[0] -= 32;
        return String.valueOf(cs);
    }

    public Results.Result importScoreInfoFromExcel2(File file) {
        Class clazz = ExcelRace.class;
        Field[] fields = clazz.getDeclaredFields();

        List<ExcelRace> excelRaces = Lists.newLinkedList();
        try {
            ImportParams params = new ImportParams();
            params.setTitleRows(1);
            params.setHeadRows(1);
            params.setNeedVerfiy(true);  //校验
            params.setDataHandler(new ExcelDataHandlerDefaultImpl() {
                @Override
                public Object importHandler(Object obj, String name, Object value) {
                    this.setNeedHandlerFields(new String [] {"赛事编号","赛事类别","开赛时间","主队名称","客队名称","是否上架","获胜团队","最终比分","赛事状态","半场比分","是否推荐","比重","比分1：0","比分2：0","比分2：1","比分3：0","比分3：1","比分3：2","比分4：0","比分4：1","比分4：2","比分4：3","比分0：0","比分1：1","比分2：2","比分3：3","比分4：4","其他比分"});
                    return super.importHandler(obj, name, value);
                }
            });

            excelRaces = ExcelImportUtil.importExcel(file, ExcelRace.class, params);
        } catch (Exception e) {
            e.printStackTrace();
            return Results.PARAMETER_INCORRENT;
        }

        for (ExcelRace excelRace : excelRaces) {
            RaceInfo raceInfo = new RaceInfo();
//            RaceOddsInfo raceOddsInfo = new RaceOddsInfo();

            raceInfo.setId(excelRace.getId());
            raceInfo.setCategory(excelRace.getCategory());
            raceInfo.setStartTime(TimeUtil.getDateFormat(excelRace.getStartTime(), "yyyy-MM-dd HH:mm:ss"));
            raceInfo.setHomeTeam(excelRace.getHomeTeam());
            raceInfo.setVisitTeam(excelRace.getVisitTeam());
            raceInfo.setCreateTime(new Date());
            raceInfo.setShelvesStatus("是".equals(excelRace.getShelvesStatus()) ? 1 : 0);
//            raceInfo.setRaceResult(CrawlUtils.getStatusIndex(excelRace.getStatus()));
            raceInfo.setWinTeam(excelRace.getWinTeam());
            raceInfo.setWinResult(excelRace.getWinResult());
            raceInfo.setWinType(CrawlUtils.getWinType(excelRace.getWinResult()));
            raceInfo.setHalfResult(excelRace.getHalfResult());
            raceInfo.setIsRecommend("是".equals(excelRace.getIsRecommend())? 1: 0);
            raceInfo.setWeight(Double.parseDouble(raceInfo.getWeight()+"") > 0.0 ? raceInfo.getWeight(): new BigDecimal(0));
            raceInfoMapper.insert(raceInfo);

            for (Field field : fields) {
                if (field.getName().startsWith("score")) {
                    System.out.println("方法名：" + field.getName());
//                    raceOddsInfo = packOdds(excelRace, field.getName());
//                    raceOddsInfoMapper.insert(raceOddsInfo);
                }
            }
        }
        return Results.SUCCESS;
    }
}

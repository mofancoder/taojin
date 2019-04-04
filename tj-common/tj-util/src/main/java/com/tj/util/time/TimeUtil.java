package com.tj.util.time;

import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Stream;

/**
 * 时间工具类
 * Created by hui on 2017/3/10.
 */
public class TimeUtil {

    public static final String START_OF_DAY = "0";
    public static final String END_OF_DAY = "1";
    final static Integer initYear = 2018;
    final static Integer initMon = 9;
    public static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * 返回两个时间点的相差天数
     *
     * @param startDate 开始时间 字符串格式yyyy-MM-dd HH:mm:ss
     * @param endDate   结束时间  字符串格式yyyy-MM-dd HH:mm:ss
     * @return
     * @throws RuntimeException
     */
    public static int getDateInterval(String startDate, String endDate) throws RuntimeException {

        Date date1, date2;
        try {
            date1 = formatter.parse(startDate);
            date2 = formatter.parse(endDate);
            long times = date2.getTime() - date1.getTime();
            return Math.abs((int) (times / (1000 * 60 * 60 * 24)));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 返回两个时间点的相差天数
     *
     * @param startDate
     * @param endDate
     * @return
     * @throws RuntimeException
     */
    public static int getDateInterval(Date startDate, Date endDate) throws RuntimeException {


        try {
            String date1 = formatter.format(startDate);
            String date2 = formatter.format(endDate);
            endDate = formatter.parse(date1);
            startDate = formatter.parse(date2);
            long times = endDate.getTime() - startDate.getTime();
            return Math.abs((int) (times / (1000 * 60 * 60 * 24)));
        } catch (ParseException e) {
            return 0;
        }
    }

    public static String ChangeDateFormat(Date date, String formatType) {
        if (formatType == null) {
            formatType = "yyyy-MM-dd HH:mm:ss";
        }

        SimpleDateFormat sdf = new SimpleDateFormat(formatType);
        return sdf.format(date);
    }

    public static String ChangeTimeStrFormat(Date date, String formatType) {
        if (formatType == null) {
            formatType = "HH时mm分";
        }

        SimpleDateFormat sdf = new SimpleDateFormat(formatType);
        return sdf.format(date);
    }

    public static Date getDateFormat(String date, String formatType) {
        if (formatType == null) {
            formatType = "yyyy-MM-dd HH:mm:ss";
        }

        SimpleDateFormat sdf = new SimpleDateFormat(formatType);
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String getTodayDate() {
        Date now = TimeUtil.getCurrentTime();

        return formatter.format(now);
    }

    public static long getSecondsToNextDate() {
        try {
            Date now = TimeUtil.getCurrentTime();

            String endDate = formatter.format(now) + " 23:59:59";
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date endTime = fmt.parse(endDate);
            return (endTime.getTime() - now.getTime()) / 1000;
        } catch (Exception e) {
            return 0;
        }
    }


    public static long getSecondsToNextDate(Date endtime) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date now = TimeUtil.getCurrentTime();
            String nowDate = fmt.format(now);
            String endDate=fmt.format(endtime);
            return (fmt.parse(endDate).getTime() - fmt.parse(nowDate).getTime()) / 1000;
        } catch (Exception e) {
            return 0;
        }
    }
    /**
     * 格式化时间为一天的开始或者结尾
     *
     * @param time
     * @param type 0:开始时间 (xxxx-xx-xx 00:00:00) 1：结束时间(xxxx-xx-xx 23:59:59)
     * @return
     */
    public static Date formatToStartDateOrEndDate(Date time, String type) {

        Date result = null;
        try {
            Calendar c = Calendar.getInstance();
            if ("0".equals(type)) {
                c.setTime(time);
                c.set(Calendar.HOUR_OF_DAY, 0);
                c.set(Calendar.MINUTE, 0);
                c.set(Calendar.SECOND, 0);
                c.set(Calendar.MILLISECOND, 0);
                result = c.getTime();
            } else if ("1".equals(type)) {
                c.setTime(time);
                c.set(Calendar.HOUR_OF_DAY, 23);
                c.set(Calendar.MINUTE, 59);
                c.set(Calendar.SECOND, 59);
                c.set(Calendar.MILLISECOND, 999);
                result = c.getTime();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 判断某一时间是否在一个区间内
     *
     * @param sourceTime 时间区间,闭合,如[10:00-20:00]
     * @param curTime    需要判断的时间 如10:00
     * @return
     * @throws IllegalArgumentException
     */
    public static boolean isInTime(String sourceTime, String curTime) {
        //System.out.println("s:" + sourceTime + " cur:" + curTime);
        if (sourceTime == null || !sourceTime.contains("-") || !sourceTime.contains(":")) {
            throw new IllegalArgumentException("Illegal Argument arg:" + sourceTime);
        }
        if (curTime == null || !curTime.contains(":")) {
            throw new IllegalArgumentException("Illegal Argument arg:" + curTime);
        }
        String[] args = sourceTime.split("-");
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        try {
            long now = sdf.parse(curTime).getTime();
            long start = sdf.parse(args[0]).getTime();
            long end = sdf.parse(args[1]).getTime();
            //System.err.println("now:" + now);
            //System.err.println("start:" + start);
            //System.err.println("end:" + end);
            if (args[1].equals("00:00")) {
                args[1] = "24:00";
            }
            if (end < start) {
                if (now > end && now < start) {
                    return false;
                } else {
                    return true;
                }
            } else {
                if (now >= start && now <= end) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Illegal Argument arg:" + sourceTime);
        }
    }

    public static Date getYesterdayOfBegin() {
        Calendar c = Calendar.getInstance();
        Date date = TimeUtil.getCurrentTime();
        c.setTime(date);
        c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR) - 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        return c.getTime();
    }

    public static Date getTodayOfBegin() {
        Calendar c = Calendar.getInstance();
        Date date = TimeUtil.getCurrentTime();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        return c.getTime();
    }

    /**
     * 指定日期加上天数后的日期
     *
     * @param num  为增加的天数
     * @param date 创建时间
     * @return
     * @throws ParseException
     */
    public static Date plusDay(int num, Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        ca.add(Calendar.DATE, num);// num为增加的天数，可以改变的
        date = ca.getTime();
//        String enddate = format.format(date);
        return date;
    }


    /**
     * 获取本周第一天
     *
     * @param date
     * @return
     */
    public static Date getThisWeekMonday(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int d = 0;
        if (cal.get(Calendar.DAY_OF_WEEK) == 1) {
            d = -6;
        } else {
            d = 2 - cal.get(Calendar.DAY_OF_WEEK);
        }
        cal.add(Calendar.DAY_OF_WEEK, d);
        return date;
    }

    /**
     * 获取n月前的第一天
     *
     * @param num
     * @param date
     * @return
     */
    public static Date getFirstMonthDay(Integer num, Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, num);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        date = cal.getTime();
        return date;
    }

    /**
     * 获取n月前的最后一天
     *
     * @param num
     * @param date
     * @return
     */
    public static Date getLastMonthDay(Integer num, Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, num);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        date = cal.getTime();
        return date;
    }

    public static void main(String[] args) {
        Date date = TimeUtil.getCurrentTime();
//        System.out.println("DATE:"+getLastMonthDay(-1,date));
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        LocalDate today = LocalDate.now();
        LocalDate b = LocalDate.now();
        //本月的第一天
        LocalDate firstday = LocalDate.of(today.getYear(), today.getMonth(), 1);
        //本月的最后一天
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate lastDay = today.with(TemporalAdjusters.lastDayOfMonth());
        System.out.println("本月的第一天" + firstday.format(formatter));
        System.out.println("本月的最后一天" + lastDay);
        System.out.println(date.compareTo(cal.getTime()));
        LocalDate startDate = LocalDate.parse("2018-06-10");
        Period period = Period.between(startDate, today);
        System.out.println(period.getMonths());
        System.out.println(today.getMonthValue());
        System.out.println(TimeUtil.plusDay(-1, date));
        System.out.println(today);
        System.out.println(today.compareTo(lastDay) == 0);
        System.out.println(today.minusMonths(1));
        String statTimeStr = TimeUtil.ChangeDateFormat(TimeUtil.plusDay(-30, date), "yyyy-MM-dd");
        LocalDate endTime = LocalDate.now();
        System.out.println(getBetweenDate(statTimeStr, endTime.toString()));
        LocalTime now = LocalTime.now();
        Random random = new Random();

//        random.nextInt(1)%(max-min+1) + min;
        System.out.println(random.nextInt(2));
        System.out.println(today.minusMonths(1));


    }

    /**
     * 获取期数
     *
     * @return
     */
    public static Integer getCurrentNum() {
        LocalDate today = LocalDate.now();
        Integer year = today.getYear();
        Integer mon = today.getMonth().getValue();
        Integer num = (year - initYear) * 12 + (mon - initMon) + 1;
        return num;
    }

    /**
     * 获取某期数的月份
     *
     * @return
     */
    public static Integer getCreateMonByNum(Integer num) {
        LocalDate date = LocalDate.of(initYear, initMon, 1);
        date = date.minusMonths(1 - num);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
        String createMonStr = date.format(formatter);
        Integer createMon = Integer.parseInt(createMonStr);
        return createMon;
    }

    /**
     * 获取两时间差的日期list
     *
     * @param start
     * @param end
     * @return
     */
    public static List<String> getBetweenDate(String start, String end) {
        List<String> list = new ArrayList<>();
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        long distance = ChronoUnit.DAYS.between(startDate, endDate);
        if (distance < 1) {
            return list;
        }

        Stream.iterate(startDate, d -> {
            return d.plusDays(1);
        }).limit(distance + 1).forEach(f -> {
            list.add(f.format(formatter).toString());
        });
        return list;
    }

    /**
     *  获取指定时间前某个时间返回，按照传入格式
     * @param date 指定时间，默认为当前时间
     * @param type 往前(-N)或往后(N)推算的时间类型(year month day hour)
     * @param number 具体数字
     * @param formatStr  需要返回的格式，默认yyyy-MM-dd HH:mm:ss
     * @return 格式话日期字符串
     * @author yangzhixin
     * @time 2019-01-02
     */
    public static String formatTimeToAssign(Date date,String type,Integer number,String formatStr){
        if(formatStr==null){
            formatStr = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        if(date==null){
            try {
                date = format.parse(TimeUtil.getBeijingTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        //要返回的字符串
        String timeStr = "";
        //如果类型和数字参数为空，则返回当前时间字符串
        if(type==null||number==null){
            return format.format(date);
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        //推算N年后时间
        if("year".equalsIgnoreCase(type)||"y".equalsIgnoreCase(type)){
            c.add(Calendar.YEAR,number);
            Date y = c.getTime();
            timeStr = format.format(y);
        }else
        //推算N月
        if("month".equalsIgnoreCase(type)||"m".equalsIgnoreCase(type)){
            c.add(Calendar.MONTH,number);
            Date m = c.getTime();
            timeStr = format.format(m);
        }else
        //推算N天
        if("day".equalsIgnoreCase(type)||"d".equalsIgnoreCase(type)){
            c.add(Calendar.DATE, number);
            Date d = c.getTime();
            timeStr = format.format(d);
        }else
        //推算N小时
            if ("hour".equalsIgnoreCase(type) || "h".equalsIgnoreCase(type)) {
                c.add(Calendar.HOUR, number);
                Date h = c.getTime();
                timeStr = format.format(h);
            } else
                //推算N分钟
                if ("minute".equalsIgnoreCase(type) || "min".equalsIgnoreCase(type)) {
                    c.add(Calendar.MINUTE, number);
                    Date min = c.getTime();
                    timeStr = format.format(min);
                } else {
                    timeStr = format.format(date);
                }

        return timeStr;
    }

    public static String addTime(String timeStr, String addnumber) {
        String str = null;
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = df.parse(timeStr);
            //时间累计
            Calendar gc = new GregorianCalendar();
            gc.setTime(date);
            gc.add(GregorianCalendar.MINUTE, Integer.parseInt(addnumber));
            str = df.format(gc.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     *
     * @return 返回number天后的时间距离当前传入时间多少分钟
     */
    public static Long getSpecificTime(){
        String formatStr = "yyyy-MM-dd HH:mm:ss";
        Date date = TimeUtil.getCurrentTime();
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        LocalDateTime ldt = LocalDateTime.parse(format.format(date), formatter);
        int hour = ldt.getHour();
        String timeStr = "";
        if(hour<12){
            Date d = c.getTime();
            timeStr = format.format(d).substring(0,11)+"12:00:00";
        }else{
            c.add(Calendar.DATE, 1);
            Date d = c.getTime();
            timeStr = format.format(d).substring(0,11)+"12:00:00";
        }
        Date endTime = TimeUtil.getCurrentTime();
        try{
            endTime = format.parse(timeStr);

        }catch (ParseException e){
            e.printStackTrace();
        }
        Instant instantDate = date.toInstant();
        Instant instantEndDate = endTime.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime startTime = instantDate.atZone(zoneId).toLocalDateTime();
        LocalDateTime localDateTime = instantEndDate.atZone(zoneId).toLocalDateTime();
        long duration = Duration.between(startTime, localDateTime).toMinutes();
        return duration;
    }

    /**
     * 取北京时间
     * @return
     */
    public static String getBeijingTime(){
        return getFormatedDateString(8);
    }

    /**
     * 取班加罗尔时间
     * @return
     */
    public static String getBangaloreTime(){
        return getFormatedDateString(5.5f);
    }

    /**
     * 取纽约时间
     * @return
     */
    public static String getNewyorkTime(){
        return getFormatedDateString(-5);
    }

    /**
     * 此函数非原创，从网上搜索而来，timeZoneOffset原为int类型，为班加罗尔调整成float类型
     * timeZoneOffset表示时区，如中国一般使用东八区，因此timeZoneOffset就是8
     * @param timeZoneOffset
     * @return
     */
    public static String getFormatedDateString(float timeZoneOffset){
        if (timeZoneOffset > 13 || timeZoneOffset < -12) {
            timeZoneOffset = 0;
        }

        int newTime=(int)(timeZoneOffset * 60 * 60 * 1000);
        TimeZone timeZone;
        String[] ids = TimeZone.getAvailableIDs(newTime);
        if (ids.length == 0) {
            timeZone = TimeZone.getDefault();
        } else {
            timeZone = new SimpleTimeZone(newTime, ids[0]);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(timeZone);
        return sdf.format(new Date());
    }

    /**
     * 此函数非原创，从网上搜索而来，timeZoneOffset原为int类型，为班加罗尔调整成float类型
     * timeZoneOffset表示时区，如中国一般使用东八区，因此timeZoneOffset就是8
     * @param timeZoneOffset
     * @return
     */
    public static String getFormatedDateString(float timeZoneOffset,Date date){
        if (timeZoneOffset > 13 || timeZoneOffset < -12) {
            timeZoneOffset = 0;
        }

        int newTime=(int)(timeZoneOffset * 60 * 60 * 1000);
        TimeZone timeZone;
        String[] ids = TimeZone.getAvailableIDs(newTime);
        if (ids.length == 0) {
            timeZone = TimeZone.getDefault();
        } else {
            timeZone = new SimpleTimeZone(newTime, ids[0]);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(timeZone);
        return sdf.format(date);
    }

    /**
     * 获取当前北京时间
     * @return
     */

    public static Date getCurrentTime(){
        String formatStr = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        Date date = null;
        try {
            date = format.parse(TimeUtil.getBeijingTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 将指定时间转换为指定国家时间
     * @param date
     * @param country
     * @return
     */
    public static Date getAssignCountry(Date date ,String country){
        if(StringUtils.isEmpty(country)){
            return getCurrentTime();
        }
        String formatStr = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        if("beijing".equals(country)){
            String timeStr = getFormatedDateString(8,date);
            try {
                date = format.parse(timeStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if("newyork".equals(country)){
            String timeStr = getFormatedDateString(-5,date);
            try {
                date = format.parse(timeStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return date;
    }

    public static Date localDateTimeToDate(LocalDateTime localDateTime){
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = localDateTime.atZone(zoneId);//Combines this date-time with a time-zone to create a  ZonedDateTime.
        Date date = Date.from(zdt.toInstant());
        return date;
    }
    public static LocalDateTime dateTimeToLocalDateTime(Date date){
        Instant instant = date.toInstant();//An instantaneous point on the time-line.(时间线上的一个瞬时点。)
        ZoneId zoneId = ZoneId.systemDefault();//A time-zone ID, such as {@code Europe/Paris}.(时区)
        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
        return localDateTime;
    }

    public static Long betweenTime(LocalDateTime date1,LocalDateTime date2){
        long duration = Duration.between(date1, date2).toMinutes();
        return duration;
    }
}

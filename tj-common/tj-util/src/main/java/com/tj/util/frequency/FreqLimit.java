package com.tj.util.frequency;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by ldh on 2017/7/3.
 */

@Service
public class FreqLimit {
    private static final String preKey = "FreqLimit:";
    private static HashMap<String, MaxTimeCount> hsMaxTimeCount = new HashMap<String, MaxTimeCount>();
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private FreqLimitConf freqLimitConf;
    @Value("${limit_comm_time:20}")
    private int limitCommTime;

    @Value("${limit_comm_count:10}")
    private int limitCommCount;

    /**
     * @param path
     * @param uid
     * @param maxTime  单位秒
     * @param maxCount
     * @return
     */
    private boolean isAchieveLimit(String path, String uid, int maxTime, final int maxCount) {
        if (null == path || null == uid) {
            return false;
        }
        final String redisKey = preKey + path + ":" + uid;
        Date date = new Date();
        long timeMS = date.getTime();//milliseconds
        final String newTimeStr = String.valueOf(timeMS);

        Date dateTemp = new Date();
        dateTemp.setTime(timeMS);

        Long len = getQueueLen(redisKey);
        if (len < maxCount) {
            push(redisKey, newTimeStr);
            //设置key有效期
            setKeyTimeOut(redisKey, maxTime);
            return false;
        }
        //设置key有效期
        setKeyTimeOut(redisKey, maxTime);
        Long oldestTime = getOldestTime(redisKey);
        Long diff = timeMS - oldestTime;
        if (diff < maxTime * 1000) {
            return true;
        }

        push(redisKey, newTimeStr);
        trim(redisKey, maxCount);
        return false;
    }

    private void setKeyTimeOut(String redisKey, int maxTime) {
        redisTemplate.expire(redisKey, maxTime, TimeUnit.SECONDS);
    }

    private void trim(String key, int maxCount) {
        redisTemplate.opsForList().trim(key, 0, maxCount - 1);
    }

    private void push(String key, String value) {
        redisTemplate.opsForList().leftPush(key, value);
    }

    private Long getQueueLen(String key) {
        Long len = redisTemplate.opsForList().size(key);
        if (len == null) {
            return 0L;
        }
        return len;
    }

    private Long getOldestTime(String key) {
        String val = redisTemplate.opsForList().index(key, -1L);
        if (val == null || "".equals(val)) {
            return 0L;
        }
        return Long.valueOf(val);
    }

    public boolean isAchieveLimit(String uid, String reqUri) {
        if (null == uid || null == reqUri) {
            return false;
        }
        reqUri = reqUri.replaceAll("//", "/").toLowerCase();
        MaxTimeCount mtc = getMaxTimeCount(reqUri);
        if (null == mtc) {
            return false;
        }
        return isAchieveLimit(reqUri, uid, mtc.maxTime, mtc.maxCount);
    }

    private MaxTimeCount getMaxTimeCount(String path) {
        if (null == path || "".equals(path)) {
            return null;
        }
        if (freqLimitConf.isNotLimit(path)) {
            return null;
        }

        if (hsMaxTimeCount.containsKey(path)) {
            return hsMaxTimeCount.get(path);
        }

        MaxTimeCount mtc = null;
        switch (path) {
            case "/dingdang/client/sale/querygoodlistwithcategory.do"://都要小写
                mtc = new MaxTimeCount(10, 20);
                break;
            case "/dingdang/app/order/createorders.do":
            case "/dingdang/app/order/create.do":
            case "/dingdang/app/cust/addintegralbysignin.do":
            case "/dingdang/app/integral/customerexchange.do":
            case "/dingdang/app/integral/activity/duobaojoinactivity.do":
            case "/dingdang/app/open/unionepay/getcard.do":
                mtc = new MaxTimeCount(1, 1);
                break;
            /*
            case "/dingdang/app/order/reminderdelivery.do":
                mtc = new MaxTimeCount(600,1);//10分一次
                break;*/
            default:
                mtc = new MaxTimeCount(60, 60);//默认都是10秒内点7次
                break;
        }

        if (null != mtc) {
            hsMaxTimeCount.put(path, mtc);
        }
        return mtc;
    }

    class MaxTimeCount {
        protected int maxTime;
        protected int maxCount;

        public MaxTimeCount(int maxTime, int maxCount) {
            this.maxCount = maxCount;
            this.maxTime = maxTime;
        }
    }
}

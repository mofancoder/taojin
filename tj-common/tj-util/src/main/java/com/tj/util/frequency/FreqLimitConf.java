package com.tj.util.frequency;

import org.springframework.stereotype.Service;

/**
 * Created by ldh on 2018-02-01.
 */
@Service
public class FreqLimitConf {

    /**
     * 判断一个接口是否频率控制
     *
     * @param url
     * @return true-不限制,false-限制
     */
    public boolean isNotLimit(String url) {
        return true;//getHashSetNotFreqLimitUrls().containsKey(url);
    }
}

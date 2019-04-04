package com.tj.bet.config;

import com.alibaba.fastjson.JSON;
import com.tj.util.enums.BetTypeEnum;

import java.lang.reflect.ParameterizedType;

/**
 * @program: tj-core
 * @description: ${description}
 * @author: liang.song
 * @create: 2018-12-13-13:40
 **/
public abstract class BetStrategy<T> {

    public abstract BetTypeEnum type();

    public abstract void settle(String t);

    public abstract void newSettle(String t, String score);

    public T serialize(String t) {
        return JSON.parseObject(t, (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
    }

    public abstract void rollbackForCancelRace(String t);

    public abstract void selectAllCancelRace();
}

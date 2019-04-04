package com.tj.util;

import com.alibaba.fastjson.JSON;
import com.tj.util.enums.BetTypeEnum;

import java.lang.reflect.ParameterizedType;

/**
 * @program: tj-core
 * @description: ${description}
 * @author: liang.song
 * @create: 2018-12-14-16:02
 **/
public abstract class AbstractRule<T> {

    public abstract BetTypeEnum type();

    public T serialize(String t) {
        return JSON.parseObject(t, (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
    }

}

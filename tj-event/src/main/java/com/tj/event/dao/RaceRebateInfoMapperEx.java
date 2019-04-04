package com.tj.event.dao;

import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 * @program: tj-core
 * @description:
 * @author: liang.song
 * @create: 2018-12-25-17:12
 **/
public interface RaceRebateInfoMapperEx {
    int updateDive(@Param("rebateId") Integer rebateId, @Param("ratio") BigDecimal ratio, @Param("openStatus") Integer openStatus);
}

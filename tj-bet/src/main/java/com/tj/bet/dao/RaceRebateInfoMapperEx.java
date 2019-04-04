package com.tj.bet.dao;

import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 * @program: tj-core
 * @description: ${description}
 * @author: liang.song
 * @create: 2018-12-14-14:03
 **/
public interface RaceRebateInfoMapperEx {
    int subValidAmount(@Param("rebateId") Integer rebateId, @Param("amount") BigDecimal amount);
    int addValidAmount(@Param("rebateId") Integer rebateId, @Param("amount") BigDecimal amount);
    int updateDive(@Param("rebateId") Integer rebateId, @Param("ratio") BigDecimal ratio, @Param("openStatus") Integer openStatus);
}

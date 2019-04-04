package com.tj.bet.dao;

import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 * @program: tj-core
 * @description: ${description}
 * @author: liang.song
 * @create: 2018-12-12-16:19
 **/
public interface UserBalanceInfoMapperEx {
    /**
     * 扣除投注金额
     *
     * @param userId
     * @param amount
     * @return
     */
    int cutBetAmount(@Param("userId") Integer userId, @Param("amount") BigDecimal amount);

    /**
     * 添加返利金额
     *
     * @param userId
     * @param amount
     * @return
     */
    int addRebateAmount(@Param("userId") Integer userId, @Param("amount") BigDecimal amount);

    int addSysBetAmount(@Param("userId") Integer userId, @Param("amount") BigDecimal amount);


}

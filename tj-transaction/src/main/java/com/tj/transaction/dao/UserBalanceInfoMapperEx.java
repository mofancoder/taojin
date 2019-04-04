package com.tj.transaction.dao;

import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 * @program: tj-core
 * @description: 用户余额dao扩展接口
 * @author: liang.song
 * @create: 2018-11-28-15:46
 **/
public interface UserBalanceInfoMapperEx {

    int subSysMerchantAmount(@Param("userId") Integer userId, @Param("amount") BigDecimal amount);

    int addChargeAmount(@Param("userId") Integer userId, @Param("amount") BigDecimal amount);

    int freezeWithdrawAmount(@Param("userId") Integer userId, @Param("amount") BigDecimal amount);

    //解冻
    int releaseWithdrawAmount(@Param("userId") Integer userId, @Param("amount") BigDecimal amount);

    //回滚冻结金额:用户提现失败
    int rollbackAmount(@Param("userId") Integer userId, @Param("amount") BigDecimal amount);

    int addSysMerchantAmount(@Param("userId") Integer userId, @Param("amount") BigDecimal amount);
}

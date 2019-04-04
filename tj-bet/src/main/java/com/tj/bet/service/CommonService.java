package com.tj.bet.service;

import com.tj.util.enums.otc.SubOrAddEnum;

import java.math.BigDecimal;

/**
 * @program: tj-core
 * @description: ${description}
 * @author: liang.song
 * @create: 2018-12-18-14:18
 **/
public interface CommonService {
    void insertUserBalanceChange(Long transactionId, BigDecimal amount, SubOrAddEnum subOrAddEnum, Integer userId);

    void insertUserBalanceChange(Long transactionId, BigDecimal amount, SubOrAddEnum subOrAddEnum, Integer userId,Integer code);

}

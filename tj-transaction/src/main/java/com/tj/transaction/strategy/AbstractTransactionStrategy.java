package com.tj.transaction.strategy;

import com.tj.dto.RemoteChargeResult;
import com.tj.dto.RemoteWithdrawResult;
import com.tj.util.enums.PlatformTypeEnum;

/**
 * @program: tj-core
 * @description: 抽象充值策略
 * @author: liang.song
 * @create: 2018-11-28-10:56
 **/
public abstract class AbstractTransactionStrategy {

    public abstract PlatformTypeEnum type();

    public abstract RemoteChargeResult charge(Long transactionId);

    public abstract RemoteWithdrawResult withdraw(Long transactionId);
}

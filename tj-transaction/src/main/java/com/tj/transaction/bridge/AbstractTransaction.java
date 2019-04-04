package com.tj.transaction.bridge;

import com.tj.util.TransactionTypeEnum;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: tj-core
 * @description: 抽象交易类型
 * @author: liang.song
 * @create: 2018-11-29-11:33
 **/
public abstract class AbstractTransaction {
    /**
     * 定义实现类接口
     */
    protected List<TransactionStatus> transactionStatuses;
    /**
     * transaction 状态map
     */
    protected Map<String, TransactionStatus> transactionStatusMap = new ConcurrentHashMap<>();

    public void setTransactionStatus(List<TransactionStatus> transactionStatuses) {
        this.transactionStatuses = transactionStatuses;
        transactionStatuses.forEach(v -> {
            transactionStatusMap.put(v.code(), v);
        });
    }

    /**
     * 交易类型{@link TransactionTypeEnum}
     *
     * @return 交易类型
     */
    public abstract TransactionTypeEnum type();

    /**
     * 业务方法
     */
    public abstract void handlerStatus(String transactionId, String thirdPartyId, String amount, String code);
}

package com.tj.transaction.bridge;

/**
 * @program: tj-core
 * @description: 状态接口
 * @author: liang.song
 * @create: 2018-11-29-11:36
 **/
public interface TransactionStatus {
    /**
     * transaction 状态码
     *
     * @return
     */
    String code();

    /**
     * 状态处理接口
     */
    void handler(Long transactionId);

}

package com.tj.transaction.service;

import com.tj.dto.RemoteChargeResult;
import com.tj.dto.RemoteWithdrawResult;
import com.tj.dto.TransactionRequestDto;
import com.tj.dto.TransactionResultDto;
import com.tj.util.enums.otc.SubOrAddEnum;

import java.math.BigDecimal;

/**
 * @program: tj-core
 * @description: 公共服务
 * @author: liang.song
 * @create: 2018-11-27-18:44
 **/
public interface CommonService {
    /**
     * 插入交易记录
     *
     * @param requestDto 交易请求
     * @return 交易记录ID
     */
    Long insertTransaction(TransactionRequestDto requestDto);

    /**
     * 更新交易的第三方流水号
     *
     * @param remoteChargeResult 远程交易结果
     * @return 交易ID
     */
    Long updateTransaction(RemoteChargeResult remoteChargeResult);

    /**
     * 获取交易结果
     *
     * @param transactionId 交易记录
     * @param payUrl        支付链接
     * @return 交易结果
     */
    TransactionResultDto getTransactionResult(Long transactionId, String payUrl);


    /**
     * 冻结提现金额并插入一条提现记录
     *
     * @return 交易记录ID
     */
    Long insertWithdrawAndFreezeAmount(TransactionRequestDto requestDto);

    /**
     * 冻结用户提现金额
     *
     * @param userId 用户ID
     * @param amount 提现金额
     */
    void freezeWithdrawAmount(Integer userId, BigDecimal amount);

    /**
     * 更新提现状态
     *
     * @param remoteWithdrawResult 第三方提现结果
     */
    void updateWithdrawStatus(RemoteWithdrawResult remoteWithdrawResult);

    /**
     * 插入用户积分变动记录
     *
     * @param transactionId 交易ID
     * @param amount        交易金额
     * @param subOrAddEnum  添加/减少
     * @param userId        用户ID
     */
    void insertUserBalanceChange(Long transactionId, BigDecimal amount, SubOrAddEnum subOrAddEnum, Integer userId);
}

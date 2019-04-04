package com.tj.transaction.service;

import com.tj.dto.*;
import com.tj.util.Results;

import java.util.Date;

/**
 * @program: tj-core
 * @description: 交易服务
 * @author: liang.song
 * @create: 2018-11-27-17:17
 **/
public interface TransactionService {
    /**
     * 充值
     *
     * @param requestDto 充值请求
     * @return 充值结果
     */
    Results.Result<TransactionResultDto> charge(TransactionRequestDto requestDto);

    /**
     * 交易回调
     *
     * @return 回调结果
     */
    CallbackReturnDto transactionCallback(CallbackDto callbackDto);
    /**
     * 提现
     *
     * @param requestDto 交易请求
     * @return 提现结果
     */
    Results.Result<TransactionResultDto> withdraw(TransactionRequestDto requestDto);

    Results.Result<UserBalanceInfoDto> userBalance();

    Results.Result<UserTransactionSumDto> list(Integer type, Integer dateType, Integer platform, Integer recdStatus, Integer curPage, Integer pageSize);

    /**
     * 管理员查看订单历史记录
     *
     * @param curPage         管理员令牌
     * @param type          交易类型
     * @param recdStatus    记录状态
     * @param username      用户名
     * @param phone         手机号
     * @param transactionId 交易记录ID
     * @param startTime     开始时间
     * @param endTime       结束时间
     * @return 订单历史记录
     */
    Results.Result<AdminTransactionSumDto> adminRecdList(
                                                                    Integer type,
                                                                    Integer recdStatus,
                                                                    Integer auditStatus,
                                                                    String username,
                                                                    String phone,
                                                                    String transactionId,
                                                                    Date startTime,
                                                                    Date endTime,
                                                                    Integer curPage,
                                                                    Integer pageSize);

    /**
     * 管理员审核订单
     *
     * @param transactionId 交易ID
     * @param auditStatus   审核状态
     * @param auditRemark   审核备注
     * @return 审核结果
     */
    Results.Result<Void> auditWithdraw(Long transactionId, Integer auditStatus, String auditRemark);

    /**
     * 创建线下订单
     *
     * @param requestDto 线下订单
     * @return 创建是否成功
     */
    Results.Result<Void> createOfflineTransaction(OfflineTransactionRequestDto requestDto);

    Results.Result<Void> auditOfflineTransaction(AdminAuditOfflineTransaction request);

    /**
     * 用户提交提现申请
     *
     * @param request 提现请求
     * @return 提现申请提交成功
     */
    Results.Result<Void> submitWithdraw(UserWithdrawRequest request);

    /**
     * 管理员确认提现
     *
     * @param request 确认提现
     * @return
     */
    Results.Result<Void> adminConfirmWithdraw(AuditWithdrawRequest request);

    void polling(String transactionId, Integer status);
}

package com.tj.util.enums;

/**
 * Created by ldh on 2018-06-07.
 */
public enum CrmBuySacRecdStatus {
    None,
    Submit,//1 提交
    BtcReceived,//2 接收
    Auditing,//3 审核中,金额比较大可能需要审核
    TxidTooLate,// 4-提交txid晚于区块到账时间,需客服转SAC
    SacSend,// 5-基金会发出SAC
    RejectSendSac,// 6-拒绝转币

}

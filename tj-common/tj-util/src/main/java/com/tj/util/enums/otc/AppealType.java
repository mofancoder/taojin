package com.tj.util.enums.otc;

/**
 * Created by ldh on 2018-02-22.
 */
public enum AppealType {
    //0-其他、1-对方未付款、2-对方未放行、3-对方无应答、4-对方有欺诈行为 参考 t_legal_trade_appeal_recd
    OTHER,
    THE_OTHER_NOT_PAY,//1-未付款
    THE_OTHER_NOT_PERMIT,//2-对方未放行
    THE_OTHER_NOT_REPLY,//3-对方无应答
    THE_OTHER_CHEAT //4-对方有欺诈行为

}

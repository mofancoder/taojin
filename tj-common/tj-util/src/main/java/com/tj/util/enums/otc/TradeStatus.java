package com.tj.util.enums.otc;

/**
 * Created by ldh on 2018-02-22.
 */
public enum TradeStatus {
    //t_legal_tender_trade_recd,法币交易记录状态:未付款、已付款(等待放行)、已完成、已取消、申诉中
    UNKNOWN,
    WAITING_PAY,//1-等待支付
    PAYED,//2-已付款
    NORMAL_FINISHED,//3-正常付款完成交易
    CANCEL,//4-已取消
    APPEALING,//5-申诉中
    APPEAL_DEAL_FIN,//6-申诉后交易最终完成
    APPEAL_CANCELED_FIN,//7-申诉后取消交易
    PAY_TIMEOUT; //8 - 支付超时取消

    /**
     * @param status
     * @param type   //0表示在线出售，1表示在线购买
     * @return
     */
    public static String getStatusStr(int status, int type) {
        String val = "状态未知";
        switch (status) {
            case 1:
                val = "等待支付";
                break;
            case 2:
                val = "已付款";
                break;
            case 3:
                val = "已完成";
                break;
            case 4:
                val = "已取消";
                break;
            case 5:
                val = "申诉中";
                break;
            case 6:
                val = "已完成";
                break;
            case 7:
                val = "已取消";
                break;
            case 8:
                val = "超时取消";
                break;
            default:
                break;
        }
        return val;
    }
}

package com.tj.dto;

import lombok.Data;

@Data
//TODO 定义提现结果数据
public class RemoteWithdrawData {

    private String company_id;

    private String player_id;

    private String amount_money;

    private String company_order_id;

    private String dora_order_id;

    private String due_date;

    private String scalper_url;
}

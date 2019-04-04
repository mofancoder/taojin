package com.tj.dto;

import lombok.Data;
import lombok.Getter;

@Getter
@Data
public class RemoteChargeData {

    private String company_id;

    private String player_id;

    private String amount_money;

    private String company_order_id;

    private String dora_order_id;

    private String due_date;

    private String scalper_url;
}

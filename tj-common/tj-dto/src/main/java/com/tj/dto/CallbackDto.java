package com.tj.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CallbackDto {

    private String timestamp;

    private String sign;

    private String api_version;

    private String company_id;

    private String player_id;

    private String company_order_id;

    private String trade_no;

    private BigDecimal original_amount;

    private BigDecimal actual_amount;

    private String operating_time;

    private String notify_url;
}

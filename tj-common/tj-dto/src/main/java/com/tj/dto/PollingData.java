package com.tj.dto;

import lombok.Data;

@Data
public class PollingData {

    private String company_id;
    private String company_order_no;
    private String error_msg;
    //(1:处理成功，2：订单处理中3：订单失败)
    private Integer status;

}

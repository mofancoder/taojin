package com.tj.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

/**
 * @program: tj-core
 * @description: 用户提现请求
 * @author: liang.song
 * @create: 2018-12-19-11:33
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class UserWithdrawRequest {
    @NotEmpty
    @ApiModelProperty(value = "用户提现金额", required = true)
    private String amount;
    @NotEmpty
    @ApiModelProperty(value = "提现账号", required = true)
    private String withdrawAccount;
    @NotEmpty
    @ApiModelProperty(value = "提现平台类型(4:线下支付宝 5:线下微信 6:线下银行卡)", required = true)
    private Integer withdrawType;

    @ApiModelProperty("用户备注(银行开户行，开户姓名 等备注信息)")
    private String userRemark;
}

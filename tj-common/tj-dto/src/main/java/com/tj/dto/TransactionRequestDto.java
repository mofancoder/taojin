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
 * @description: 交易请求
 * @author: liang.song
 * @create: 2018-11-27-17:42
 **/
@Data
@ApiModel
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequestDto {

    @NotEmpty(message = "交易金额不许为空")
    @ApiModelProperty(value = "交易金额", required = true)
    private String amount;
    @NotEmpty(message = "交易类型不许为空")
    @ApiModelProperty(value = "交易类型(1:充值 2:提现)", required = true)
    private Integer type;
    @NotEmpty(message = "支付平台不许为空")
    @ApiModelProperty(value = "平台(1:支付宝 2:微信 3:银行卡)", required = true)
    private Integer platform;
    @ApiModelProperty(value = "提现目标地址")
    private String targetAddr;
}

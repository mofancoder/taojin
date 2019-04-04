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
 * @description: ${description}
 * @author: liang.song
 * @create: 2018-12-12-11:00
 **/
@Data
@ApiModel
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OfflineTransactionRequestDto {
    @NotEmpty(message = "交易金额不许为空")
    @ApiModelProperty(value = "交易金额", required = true)
    private String amount;
    @ApiModelProperty(value = "客户手机号", required = true)
    private String phone;
    @ApiModelProperty(value = "第三方流水ID", required = true)
    private String thirdPartyId;
    @ApiModelProperty(value = "用户备注", required = false)
    private String userRemark;
    @ApiModelProperty(value = "线下订单类型", required = true)
    private Integer platformType;
    @ApiModelProperty("线下交易快照")
    private String snapshot;
}

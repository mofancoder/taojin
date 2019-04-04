package com.tj.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @program: tj-core
 * @description: 用户交易记录
 * @author: liang.song
 * @create: 2018-12-03-14:00
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class UserTransactionRecdDto {
    @ApiModelProperty("用户ID")
    private Integer userId;
    @ApiModelProperty("交易流水号")
    private String transactionId;
    @ApiModelProperty("交易类型(1:充值 2:提现)")
    private String transactionType;
    @ApiModelProperty("交易金额")
    private BigDecimal amount;
    @ApiModelProperty("实际交易金额")
    private BigDecimal actualAmount;
    @ApiModelProperty("交易状态 0-失败,1-成功,2-处理中(正在与第三方交互/等待结果返回),3-交易超时")
    private Integer recdStatus;
    @ApiModelProperty("交易时间")
    private Date createTime;
    @ApiModelProperty("交易手续费")
    private BigDecimal fee;
    @ApiModelProperty("交易手续费")
    private BigDecimal feeRatio;
    @ApiModelProperty("审核状态 0：审核失败 1:审核成功  2：审核中")
    private String auditStatus;
    @ApiModelProperty("审核备注")
    private String auditRemark;

    @ApiModelProperty("平台 1:支付宝 2:微信 3:银行卡")
    private Integer platform;

    @ApiModelProperty("目标地址")
    private String targetAddr;

}

package com.tj.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @program: tj-core
 * @description: ${description}
 * @author: liang.song
 * @create: 2018-12-14-14:29
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class RebateBetInfo {
    @ApiModelProperty("返利率ID")
    private Integer rebateId;
    @ApiModelProperty("下单总金额")
    private BigDecimal betAmount;
    @ApiModelProperty("下单总记录数")
    private Integer betCount;
    @ApiModelProperty("投注结算-赢总金额")
    private BigDecimal betWinAmount;
    @ApiModelProperty("投注结算-输总金额")
    private BigDecimal betLoseAmount;
    @ApiModelProperty("盈亏")
    private BigDecimal balanceAmount;
    @ApiModelProperty("平均赔率")
    private BigDecimal avgRebateRatio;
}

package com.tj.dto;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @program: tj-core
 * @description: 管理员查看投注记录
 * @author: liang.song
 * @create: 2018-12-14-10:37
 **/
@Builder
@ApiModel
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AdminBetRecdInfo<T> {
    @ApiModelProperty("投注记录")
    PageInfo<T> page;
    @ApiModelProperty("投注总金额")
    private BigDecimal betAmount;
    @ApiModelProperty("结算输-总金额")
    private BigDecimal betLoseAmount;
    @ApiModelProperty("结算赢-总金额")
    private BigDecimal betWinAmount;
    @ApiModelProperty("营收总金额(赢为正数-输为负数)")
    private BigDecimal balanceAmount;
    @ApiModelProperty("投注总记录数")
    private Integer countNum;
}

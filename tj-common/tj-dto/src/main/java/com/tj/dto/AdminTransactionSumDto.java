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
 * @description: 管理员统计用户交易记录
 * @author: liang.song
 * @create: 2018-12-19-10:54
 **/
@Data
@Builder
@ApiModel
@NoArgsConstructor
@AllArgsConstructor
public class AdminTransactionSumDto {
    @ApiModelProperty("分页结果")
    PageInfo<AdminTransactionRecdDto> pages;
    @ApiModelProperty("总记录数")
    private Integer totalCount;
    @ApiModelProperty("总金额")
    private BigDecimal totalAmount;
    @ApiModelProperty("充值记录数")
    private Integer chargeCount;
    @ApiModelProperty("充值金额")
    private BigDecimal chargeAmount;
    @ApiModelProperty("提现记录数")
    private Integer withdrawCount;
    @ApiModelProperty("提现金额")
    private BigDecimal withdrawAmount;
}

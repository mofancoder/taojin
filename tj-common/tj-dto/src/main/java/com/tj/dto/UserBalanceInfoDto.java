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
 * @description: 用户钱包信息实体
 * @author: liang.song
 * @create: 2018-12-03-13:47
 **/
@Data
@Builder
@ApiModel
@AllArgsConstructor
@NoArgsConstructor
public class UserBalanceInfoDto {
    @ApiModelProperty("用户ID")
    private Integer userId;
    @ApiModelProperty("用户可用余额")
    private BigDecimal amount;
    @ApiModelProperty("冻结金额")
    private BigDecimal freazonAmount;

}

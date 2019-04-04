package com.tj.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;

/**
 * @program: tj-core
 * @description: 操盘确认
 * @author: liang.song
 * @create: 2018-12-26-00:23
 **/
@Data
@Builder
@ApiModel
@AllArgsConstructor
@NoArgsConstructor
public class OperateDto {
    @NotEmpty
    @ApiModelProperty(value = "返利率ID", required = true)
    private Integer rebateId;
    @ApiModelProperty(value = "返利率", required = true)
    private BigDecimal rebateRatio;
    @ApiModelProperty(value = "可下单量", required = true)
    private BigDecimal validAmount;
    @ApiModelProperty(value = "初始返利率", readOnly = true)
    private BigDecimal initRebateRatio;
    @ApiModelProperty("反波胆赔率")
    private BigDecimal oppositeOdd;
    @ApiModelProperty("反波胆返利率")
    private BigDecimal oppositeRebate;
    @ApiModelProperty("比分")
    private String score;
    @ApiModelProperty("是否可以投注(1:可以 2:不可投)")
    private Integer openStatus;
}

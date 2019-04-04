package com.tj.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@ApiModel
@AllArgsConstructor
@NoArgsConstructor
public class InitRebateDetailDto {
    @ApiModelProperty("返利率ID")
    private Integer rebateId;
    @ApiModelProperty("比分")
    private String score;
    @ApiModelProperty("初始返利率")
    private BigDecimal rebateRatio;
    @ApiModelProperty("比分开关")
    private Integer openStatus;
}

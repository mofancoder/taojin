package com.tj.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class RebateInfoDto {
    @ApiModelProperty("赔率")
    private BigDecimal odd;
    @ApiModelProperty("返利率")
    private BigDecimal rebate;
}

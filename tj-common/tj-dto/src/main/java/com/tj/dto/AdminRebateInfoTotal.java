package com.tj.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class AdminRebateInfoTotal {
    @ApiModelProperty("返利率集合")
    List<AdminRebateInfo> list;
    @ApiModelProperty("波胆返利率总和")

    private BigDecimal normalRebateTotal;
    @ApiModelProperty("反波胆返利率总和")
    private BigDecimal oppositeRebateTotal;
}

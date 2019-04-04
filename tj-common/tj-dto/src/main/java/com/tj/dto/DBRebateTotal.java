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
@ApiModel
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DBRebateTotal {

    @ApiModelProperty("返利率集合")
    List<RedisRaceRebateInfo> list;
    @ApiModelProperty("波胆返利率总和")

    private BigDecimal normalRebateTotal;
    @ApiModelProperty("反波胆返利率总和")
    private BigDecimal oppositeRebateTotal;
    @ApiModelProperty("初始化反利率总和")
    private BigDecimal initOppositeRebateTotal;
    @ApiModelProperty("平均返利率总和")

    private BigDecimal avgRebateTotal;
    @ApiModelProperty("前端返利率总和")
    private BigDecimal frontRebateTotal;

}

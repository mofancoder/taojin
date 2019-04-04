package com.tj.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Builder
@ApiModel
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AdminRebateInfo {

    @ApiModelProperty("主队")
    private String homeTeam;

    @ApiModelProperty("客队")
    private String visitTeam;

    @ApiModelProperty(value="比分",required=true)
    private String score;

    @ApiModelProperty("初始反波胆赔率")
    private BigDecimal startOdds;

    @ApiModelProperty("正波胆赔率")
    private BigDecimal normalOdds;
    @ApiModelProperty(value = "正波胆返利利率", required = true)
    private BigDecimal normalRebate;

    @ApiModelProperty("反波胆赔率")
    private BigDecimal oppositeOdds;
    @ApiModelProperty(value = "反波胆返利利率", required = true)
    private BigDecimal oppositeRebate;
}

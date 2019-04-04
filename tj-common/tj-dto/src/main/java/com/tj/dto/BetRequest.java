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
 * @description: 投注请求
 * @author: liang.song
 * @create: 2018-12-12-13:49
 **/
@Data
@ApiModel
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BetRequest {
    @ApiModelProperty(value = "投注的赛事ID", required = true)
    private String raceId;
    @ApiModelProperty(value = "返利率ID", required = true)
    private Integer rebateId;
    @ApiModelProperty(value = "投注金额", required = true)
    private BigDecimal amount;
    @ApiModelProperty(value = "投注利率", required = true)
    private BigDecimal rebateRatio;
}

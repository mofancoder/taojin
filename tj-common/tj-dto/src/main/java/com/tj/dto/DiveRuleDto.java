package com.tj.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @program: tj-core
 * @description: ${description}
 * @author: liang.song
 * @create: 2018-12-25-18:43
 **/
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class DiveRuleDto {
    @ApiModelProperty("id")
    private Integer id;
    @ApiModelProperty("上浮比率")
    private BigDecimal increase;

    @ApiModelProperty("赛前间隔(单位小时)")
    private BigDecimal autochangeTime;

    @ApiModelProperty("时间范围(1:范围内 0:范围外)")
    private Integer timeRange;

    @ApiModelProperty("投注金额-起始")
    private BigDecimal startAmount;

    @ApiModelProperty("返利率-起始")
    private BigDecimal startRebate;
    @ApiModelProperty("返利率-结束")
    private BigDecimal endRebate;
    @ApiModelProperty("规则类型(1:波胆)")
    private Integer ruleType;
    @ApiModelProperty("开启状态(1:开启 0:未开启)")
    private Integer enableStatus;
    @ApiModelProperty("创建时间")
    private Date createTime;
    @ApiModelProperty("自动关盘利率")
    private BigDecimal shutDownRebate;

}

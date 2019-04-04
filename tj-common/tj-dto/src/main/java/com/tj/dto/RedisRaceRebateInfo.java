package com.tj.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/** 返利率
 * @Auther: kevin
 * @Date: 2018/11/28 15:02
 * @Description:
 */
@Data
@ApiModel
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RedisRaceRebateInfo {
    @ApiModelProperty("返利率ID")
    private Integer id;
    @ApiModelProperty("赛事ID")
    private String raceId;

    @ApiModelProperty(value = "团队(主:客)", required = true)
    private String teams;

    @ApiModelProperty(value="比分",required=true)
    private String score;

    @ApiModelProperty("初始反波胆赔率")
    private BigDecimal startOdds;
    @ApiModelProperty(value = "初始反波胆返利利率", required = true)
    private BigDecimal startRebate;

    @ApiModelProperty("正波胆赔率")
    private BigDecimal normalOdds;
    @ApiModelProperty(value = "正波胆返利利率", required = true)
    private BigDecimal normalRebate;

    @ApiModelProperty("反波胆赔率")
    private BigDecimal oppositeOdds;
    @ApiModelProperty(value = "反波胆返利利率", required = true)
    private BigDecimal oppositeRebate;

    @ApiModelProperty("是否可以投注(0: 否 1：是)")
    private int openStatus;

    @ApiModelProperty("创建时间")
    private Date createTime;
    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("返利规则")
    private String rule;
    @ApiModelProperty("返利规则类型")
    private int ruleType;

    @ApiModelProperty("可下单量")
    private BigDecimal validAmount;

    //-------------这些字段仅为展示所用---------------
    @ApiModelProperty(value = "平均返利利率", required = true)
    private BigDecimal avgRebate;

    @ApiModelProperty("总计下单量")
    private BigDecimal sumAmount;

    @ApiModelProperty("已下单量")
    private BigDecimal usedAmount;

    @ApiModelProperty("前端返利率")
    private String rebateRatio;
    @ApiModelProperty("盈亏")
    private BigDecimal balance;
}

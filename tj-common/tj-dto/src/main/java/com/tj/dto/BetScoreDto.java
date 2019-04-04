package com.tj.dto;

import com.alibaba.fastjson.JSON;
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
 * @description: 投注猜比分JSON
 * @author: liang.song
 * @create: 2018-12-12-14:13
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class BetScoreDto {
    @ApiModelProperty(value = "用户ID", required = true)
    private Integer userId;
    @ApiModelProperty(value = "比赛ID", required = true)
    private String raceId;
    @ApiModelProperty(value = "返利率ID", required = true)
    private Integer rebateId;
    @ApiModelProperty(value = "战队(主:客)", required = true)
    private String teams;
    @ApiModelProperty(value = "下注的比分", required = true)
    private String score;
    @ApiModelProperty(value = "当时下注的返利率", required = true)
    private BigDecimal rebateRatio;
    @ApiModelProperty(value = "下注时间", required = true)
    private Date createTime;
    @ApiModelProperty(value = "下注预期返利收益", required = true)
    private BigDecimal expectRebateAmount;
    @ApiModelProperty(value = "预期赢本息收益", required = true)
    private BigDecimal expecAmount;
    @ApiModelProperty(value = "投注ID", required = true)
    private String betId;
    @ApiModelProperty("下注金额")
    private BigDecimal betAmount;
    @ApiModelProperty("赛事类别")
    private String category;
    @ApiModelProperty(value = "开赛时间", required = true)
    private Date startTime;
    public static void main(String[] args) {
        BetScoreDto build = BetScoreDto.builder().
                userId(1).
                rebateId(1).
                raceId("1").
                teams("A:B").
                score("2:0").
                betAmount(new BigDecimal(100)).
                rebateRatio(new BigDecimal(0.15d).setScale(2, BigDecimal.ROUND_HALF_UP)).createTime(new Date()).expectRebateAmount(new BigDecimal(15))
                .expecAmount(new BigDecimal(115)).betId("1").build();

        System.out.println(JSON.toJSONString(build));

    }

}

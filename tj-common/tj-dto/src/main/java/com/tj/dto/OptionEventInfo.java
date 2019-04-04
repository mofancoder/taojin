package com.tj.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Builder
@ApiModel
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OptionEventInfo {
    @ApiModelProperty(value = "类别")
    private String category;

    @ApiModelProperty("开赛时间")
    private Date startTime;

    @ApiModelProperty("结束时间")
    private Date endTime;

    @ApiModelProperty("主队")
    private String homeTeam;

    @ApiModelProperty("客队")
    private String visitTeam;

    @ApiModelProperty("赛事状态:(0:取消1:正常进行中 2:已经结束3:未开始)")
    private Integer raceStatus;

    @ApiModelProperty("获胜队伍(如果平局 填写两个队的名称,以英文,隔开)")
    private String winTeam;

    @ApiModelProperty("半场结果")
    private String halfResult;

    @ApiModelProperty("比赛结果")
    private String winResult;

    @ApiModelProperty("赛果类型:(0:平局 1:主队胜 2:客队胜)")
    private Integer winType;

    @ApiModelProperty("上架状态 1:上架 0:下架")
    private Integer shelvesStatus;

    @ApiModelProperty("是否推荐（0不推荐，1推荐）")
    private Integer isRecommend;

    @ApiModelProperty("推荐比重")
    private BigDecimal weight;

    @ApiModelProperty("是否可以投注（0不可以，1可以")
    private Integer openStatus;

}

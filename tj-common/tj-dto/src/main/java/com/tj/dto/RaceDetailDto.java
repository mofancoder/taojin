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
 * @description: 落地赛事查询
 * @author: liang.song
 * @create: 2018-12-25-11:07
 **/
@Data
@Builder
@ApiModel
@AllArgsConstructor
@NoArgsConstructor
public class RaceDetailDto {
    @ApiModelProperty("赛事ID")
    private String raceId;
    @ApiModelProperty("赛事类别")
    private String category;
    @ApiModelProperty("赛事开始时间")
    private Date startTime;
    @ApiModelProperty("赛事结束时间")
    private Date endTime;
    @ApiModelProperty("主队")
    private String homeTeam;
    @ApiModelProperty("客队")
    private String visitTeam;
    @ApiModelProperty("上下架状态(0:未上架 1:上架)")
    private Integer shelvesStatus;
    @ApiModelProperty("赛事状态(0:取消(取消的赛事进行撤注操作) 1:正常进行中 2:已经结束(定时任务爬取赛事结果)3:未开始)")
    private Integer raceStatus;
    @ApiModelProperty("获胜队伍")
    private String winTeam;
    @ApiModelProperty("比赛结果")
    private String winResult;
    @ApiModelProperty("赛果类型:(0:平局 1:主队胜 2:客队胜 3:暂无结果)")
    private Integer winType;
    @ApiModelProperty("半场结果")
    private String halfResult;
    @ApiModelProperty("是否推荐（0不推荐，1推荐）")
    private Integer isRecommend;
    @ApiModelProperty("推荐比重")
    private BigDecimal weight;
    @ApiModelProperty("投注状态（0不可以，1可以）")
    private Integer openStatus;

    @ApiModelProperty("赛事比分与赔率详情")
    private DBRebateTotal dbRebateTotal;
}

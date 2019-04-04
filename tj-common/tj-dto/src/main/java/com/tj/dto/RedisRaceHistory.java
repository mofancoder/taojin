package com.tj.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: tj-core
 * @description: redis 双方交战历史记录
 * @author: liang.song
 * @create: 2018-12-07-13:55
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel

/**
 * redis key 规则: raceId:raceName:date:team   key生命周期: 暂定30天
 */
public class RedisRaceHistory {
    @ApiModelProperty("关联赛事ID")
    private String raceId;
    @ApiModelProperty("赛事")
    private String raceName;
    @ApiModelProperty("赛事日期")
    private String date;
    @ApiModelProperty("球队")
    private String team;
    @ApiModelProperty("主客")
    private String homeOrVisit;
    @ApiModelProperty("胜负")
    private String winOrLose;
    @ApiModelProperty("对手")
    private String playAgainst;
    @ApiModelProperty("比数")
    private String result;
    @ApiModelProperty("让球 eg([+0.5/+1])")
    private String rangQiu;
    @ApiModelProperty("盘路 eg(输半)")
    private String panLu;
    //-------------------
    @ApiModelProperty("本次赛事双方战队")
    private String teams;
}

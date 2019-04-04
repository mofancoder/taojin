package com.tj.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: tj-core
 * @description: 对战双方近6场比赛
 * @author: liang.song
 * @create: 2018-12-07-14:27
 **/
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
/**
 * redis key:  raceId:raceName:date:teamA:teamB
 */
public class RedisBothSideRaceHistory {
    @ApiModelProperty("关联赛事ID")
    private String raceId;
    @ApiModelProperty("赛事名称")
    private String raceName;
    @ApiModelProperty("日期")
    private String date;
    @ApiModelProperty("战队A(按照爬虫顺序)")
    private String teamA;
    @ApiModelProperty("战队B(按照爬虫顺序)")
    private String teamB;
    @ApiModelProperty("战队A得分 eg(0)")
    private String teamAScore;
    @ApiModelProperty("战队B得分 eg(2 主队)")
    private String teamBScore;
    @ApiModelProperty("半场比数 eg(0:0)")
    private String halfResult;
    @ApiModelProperty("让球")
    private String rangQiu;
    @ApiModelProperty("盘路")
    private String panLu;
}

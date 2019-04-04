package com.tj.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: tj-core
 * @description: ${description}
 * @author: liang.song
 * @create: 2018-12-17-10:11
 **/
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class ScoreRule {
    @ApiModelProperty("赛事ID")
    private String raceId;
    @ApiModelProperty("比分(主vs客)")
    private String score;
    @ApiModelProperty("客队")
    private String teams;
    @ApiModelProperty("返利率")
    private String rebateRatio;
    /**
     * 赛事类型 参考 BetTypeEnum
     *
     * @see
     */
    @ApiModelProperty("赛事类型 ")
    private Integer ruleType;

    @ApiModelProperty("跳水状态（0:否 1:是）")
    private Integer diveType;
}

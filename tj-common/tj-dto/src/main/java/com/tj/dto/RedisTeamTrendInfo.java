package com.tj.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @program: tj-core
 * @description: redis 团队近况走势
 * @author: liang.song
 * @create: 2018-12-07-14:38
 **/
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
/**
 * redis key: raceId:team  redis value: [trend, trend]  key expired:30 days
 */
public class RedisTeamTrendInfo {
    @ApiModelProperty("趋势列表 [L,L,W,D,-]")
    List<String> trends;
    @ApiModelProperty("管理赛事ID")
    private String raceId;
    @ApiModelProperty("战队")
    private String team;

}

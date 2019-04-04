package com.tj.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ProjectName: TJ
 * @Author: MOFAN889
 * @Description: 返利率请求
 * @Date: 2018/11/30 18:21
 */
@Data
@ApiModel
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RaceRebateRequestDto {
    @ApiModelProperty(value = "需要落地赛事信息", required = true)
    private List<RedisRaceRebateInfo> redisRaceRebateInfos;
}

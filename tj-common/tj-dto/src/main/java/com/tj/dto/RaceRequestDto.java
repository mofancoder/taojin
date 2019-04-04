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
 * @Description: 赛事请求Dto
 * @Date: 2018/11/30 16:52
 */
@Data
@ApiModel
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RaceRequestDto {
    @ApiModelProperty(value = "赛事信息集合", required = true)
    private List<RedisRaceInfo> redisRaceInfos;

}

package com.tj.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @program: tj-core
 * @description: 历史战绩视图类
 * @author: liang.song
 * @create: 2018-12-07-15:11
 **/
@Data
@ApiModel
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RedisRaceHistoryDto {
    List<RedisRaceHistory> teamA;
}

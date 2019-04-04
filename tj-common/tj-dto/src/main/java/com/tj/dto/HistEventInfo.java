package com.tj.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Builder
@ApiModel
@AllArgsConstructor
@NoArgsConstructor
@Data
public class HistEventInfo {

    @ApiModelProperty("对战双方的走势图")
    private Map<String, RedisTeamTrendInfo> teamTrendInfos;

    @ApiModelProperty("战队历史战绩")
    private Map<String, List<RedisRaceHistory>> selfHistories;

    @ApiModelProperty("对战双方交战记录")
    private List<RedisBothSideRaceHistory> bothHistories;
}

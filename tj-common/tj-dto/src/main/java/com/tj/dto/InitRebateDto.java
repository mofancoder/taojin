package com.tj.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class InitRebateDto {
    @ApiModelProperty("赛事ID")
    private String raceId;
    @ApiModelProperty("详情")
    private List<InitRebateDetailDto> list;
}

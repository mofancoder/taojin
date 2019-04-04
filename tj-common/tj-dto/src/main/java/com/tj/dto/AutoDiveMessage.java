package com.tj.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
@Builder
public class AutoDiveMessage {
    @ApiModelProperty("赛事ID")
    private String raceId;
    @ApiModelProperty("返利ID")
    private Integer rebateId;
}

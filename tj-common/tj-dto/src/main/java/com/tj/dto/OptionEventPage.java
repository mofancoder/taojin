package com.tj.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@ApiModel
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OptionEventPage {

    @ApiModelProperty("赛事信息列表")
    private List<OptionEventInfo> optionInfos;

    @ApiModelProperty("赛事总记录数")
    private Integer countNum;
}

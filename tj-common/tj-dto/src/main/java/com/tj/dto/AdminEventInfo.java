package com.tj.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@ApiModel
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AdminEventInfo {

    @ApiModelProperty(value = "赛事ID")
    private String raceId;

    @ApiModelProperty(value = "类别")
    private String category;

    @ApiModelProperty("开赛时间")
    private Date startTime;

    @ApiModelProperty("主队")
    private String homeTeam;

    @ApiModelProperty("客队")
    private String visitTeam;

    @ApiModelProperty("赛事状态:(0:取消1:正常进行中 2:已经结束3:未开始)")
    private Integer raceResult;

    @ApiModelProperty("数据库是否存在 1:存在 0不存在")
    private int isExist;

}

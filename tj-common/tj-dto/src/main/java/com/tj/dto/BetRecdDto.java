package com.tj.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @program: tj-core
 * @description: 投注记录ID
 * @author: liang.song
 * @create: 2018-12-13-10:13
 **/
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class BetRecdDto<T> {
    @ApiModelProperty(value = "投注ID", required = true)
    private String id;
    @ApiModelProperty(value = "用户ID", required = true)
    private Integer userId;
    @ApiModelProperty(value = "赛事ID", required = true)
    private String raceId;
    @ApiModelProperty(value = "投注类型(1:波胆 2:反波胆)", required = true)
    private Integer betType;
    @ApiModelProperty(value = "投注内容", required = true)
    private String content;
    @ApiModelProperty(value = "投注结果(0:输 1:赢)", required = false)
    private Integer betResult;
    @ApiModelProperty(value = "投注时间", required = true)
    private Date createTime;
    @ApiModelProperty(value = "返利状态(0:返利失败 1:返利成功 2:未返利)", required = true)
    private Integer rebateStatus;
    @ApiModelProperty("投注内容实体")
    private T json;
    @ApiModelProperty("投注状态(0赛事取消自动撤注，1已投注，2用户撤单，默认1)")
    private Integer betStatus;

}

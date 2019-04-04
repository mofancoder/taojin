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
 * @description: ${description}
 * @author: liang.song
 * @create: 2018-12-17-13:44
 **/
@Data
@ApiModel
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChildInfo {
    @ApiModelProperty("用户Id")
    private Integer childId;
    @ApiModelProperty("账户名称")
    private String childAccount;
    @ApiModelProperty("手机号")
    private String childPhone;
    @ApiModelProperty("注册时间")
    private Date registTime;
}

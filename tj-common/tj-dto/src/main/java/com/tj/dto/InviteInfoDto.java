package com.tj.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @program: tj-core
 * @description: ${description}
 * @author: liang.song
 * @create: 2018-12-17-13:42
 **/
@ApiModel
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class InviteInfoDto {
    @ApiModelProperty("用户ID")
    private Integer userId;
    @ApiModelProperty("父辈ID")
    private Integer parentId;
    @ApiModelProperty("邀请人")
    private String parentAccount;
    @ApiModelProperty("父辈的电话")
    private String parentPhone;
    @ApiModelProperty("被邀请信息")
    private List<ChildInfo> children;

}

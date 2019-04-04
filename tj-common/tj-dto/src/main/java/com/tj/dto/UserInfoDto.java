package com.tj.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @program: tj-core
 * @description: ${description}
 * @author: liang.song
 * @create: 2018-12-17-13:18
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class UserInfoDto {
    @ApiModelProperty("用户ID")
    private Integer userId;
    @ApiModelProperty("用户账号")
    private String account;
    @ApiModelProperty("用户手机号")
    private String phone;
    @ApiModelProperty("手机号区号")
    private String phoneAreaCode;
    @ApiModelProperty("用户邀请码")
    private String inviteCode;
    @ApiModelProperty("国度")
    private String nationality;
    @ApiModelProperty("系统状态(1：正常 0删除)")
    private Byte sysStatus;
    @ApiModelProperty("注册时间")
    private Date registTime;
    @ApiModelProperty("上次登录的时间")
    private Date lastLoginTime;
    @ApiModelProperty("是否是代理(1:代理 0:非代理 2:平台商户 3:系统管理员)")
    private Integer proxy;
    @ApiModelProperty("操作状态")
    private Byte optStatus;
    @ApiModelProperty("账户余额")
    private BigDecimal amount;
    @ApiModelProperty("冻结金额")
    private BigDecimal freezeAmount;
}

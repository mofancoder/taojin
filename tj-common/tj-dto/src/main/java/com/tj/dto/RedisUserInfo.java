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
 * @description: 用户信息redis缓存类
 * @author: liang.song
 * @create: 2018-11-21 11:21
 **/
@Data
@ApiModel
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RedisUserInfo {
    @ApiModelProperty("用户令牌")
    private String token;
    @ApiModelProperty("用户ID")
    private Integer userId;
    @ApiModelProperty("用户昵称")
    private String nickName;
    @ApiModelProperty("头像URL")
    private String iconUrl;
    @ApiModelProperty("用户手机号")
    private String phone;
    @ApiModelProperty("用户邮箱")
    private String email;
    @ApiModelProperty("用户手机号所属的区域")
    private String phoneAreaCode;
    @ApiModelProperty("邀请码")
    private String inviteCode;
    @ApiModelProperty("国籍")
    private String nationality;
    @ApiModelProperty("用户状态(1:有效 0:失效)")
    private Byte sysStatus;
    @ApiModelProperty("注册时间")
    private Date registTime;
    @ApiModelProperty("上次登录的时间")
    private Date lastLoginTime;
    @ApiModelProperty("更新时间")
    private Date updateTime;
    @ApiModelProperty("是否是代理(1:是 0:不是)")
    private Integer proxy;
}

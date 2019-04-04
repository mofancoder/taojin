package com.tj.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: tj-core
 * @description: 远程提现dto
 * @author: liang.song
 * @create: 2018-11-29-10:57
 **/
@Data
@Builder
@ApiModel
@AllArgsConstructor
@NoArgsConstructor
public class RemoteWithdrawResult {
    private Integer code;
    private String msg;
    private RemoteWithdrawData data;
}

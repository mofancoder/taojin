package com.tj.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: tj-core
 * @description: 远程充值结果实体类
 * @author: liang.song
 * @create: 2018-11-28-10:58
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class RemoteChargeResult {
    private Integer code;
    private String msg;
    private RemoteChargeData data;
}

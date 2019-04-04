package com.tj.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

/**
 * @program: tj-core
 * @description: ${description}
 * @author: liang.song
 * @create: 2018-12-12-11:38
 **/
@Data
@ApiModel
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminAuditOfflineTransaction {
    @NotEmpty
    @ApiModelProperty(value = "交易Id", required = true)
    private String transactionId;
    @ApiModelProperty(value = "审核状态(0：审核失败 1:审核成功)", required = true)
    private Integer auditStatus;
    @ApiModelProperty(value = "审核备注", required = false)
    private String auditRemark;
    @ApiModelProperty("交易快照")
    private String snapshot;
}

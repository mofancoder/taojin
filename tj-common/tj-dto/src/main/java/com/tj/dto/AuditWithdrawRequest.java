package com.tj.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: tj-core
 * @description: 提现审核
 * @author: liang.song
 * @create: 2018-12-19-14:08
 **/
@Data
@Builder
@ApiModel
@AllArgsConstructor
@NoArgsConstructor
public class AuditWithdrawRequest {
    @ApiModelProperty(value = "提现ID", required = true)
    private String withdrawId;
    @ApiModelProperty(value = "审核状态(0：审核失败 1:审核成功)", required = true)
    private Integer auditStatus;
    @ApiModelProperty("第三方流水号")
    private String thirdPartyId;
    @ApiModelProperty("审核备注")
    private String auditRemark;
    @ApiModelProperty("审核快照")
    private String auditSnapshot;
}

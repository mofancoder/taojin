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
 * @description: 管理员查询交易记录
 * @author: liang.song
 * @create: 2018-12-03-18:21
 **/
@Data
@ApiModel
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminTransactionRecdDto {
    @ApiModelProperty("交易记录ID")
    private String transactionId;
    @ApiModelProperty("用户ID")
    private Integer userId;
    @ApiModelProperty("用户名")
    private String username;
    @ApiModelProperty("手机号")
    private String phone;
    @ApiModelProperty("交易金额")
    private BigDecimal amount;
    @ApiModelProperty("实际交易金额")
    private BigDecimal actualAmount;
    @ApiModelProperty("交易费率")
    private BigDecimal fee;
    @ApiModelProperty("费率")
    private BigDecimal feeRatio;
    @ApiModelProperty("提现目标地址")
    private String targetAddr;
    @ApiModelProperty("创建时间")
    private Date createTime;
    @ApiModelProperty("认证状态(0：审核失败 1:审核成功  2：审核中)")
    private Integer auditStatus;
    @ApiModelProperty("交易状态(0-失败,1-成功,2-处理中,3-交易超时)")
    private Integer recdStatus;
    @ApiModelProperty("审核备注")
    private String auditRemark;
    @ApiModelProperty("交易备注")
    private String sysRemark;
    @ApiModelProperty("最后审核人")
    private String auditUser;
    @ApiModelProperty("审核时间")
    private Date auditTime;
    @ApiModelProperty("交易类型:(1:充值 2:提现)")
    private Integer recdType;
    @ApiModelProperty("交易快照")
    private String snapshot;
    @ApiModelProperty("第三方流水号")
    private String thirdPartyId;
    @ApiModelProperty("交易平台(1:支付宝 2:微信 3:银行卡,4:线下支付宝 5线下微信 6线下银行卡)")
    private Integer platform;
    @ApiModelProperty("用户备注")
    private String userRemark;

}

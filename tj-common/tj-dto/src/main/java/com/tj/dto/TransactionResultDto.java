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
 * @description: 交易结果
 * @author: liang.song
 * @create: 2018-11-27-17:05
 **/
@Data
@Builder
@ApiModel
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResultDto {
    @ApiModelProperty("交易流水号")
    private String id;
    @ApiModelProperty("交易用户ID")
    private Integer userId;
    @ApiModelProperty("交易金额")
    private BigDecimal amount;
    @ApiModelProperty("交易状态(0:失败 1:成功 2:处理中 3:超时未支付)")
    private Integer status;
    @ApiModelProperty("交易时间")
    private Date createTime;
    @ApiModelProperty("交易类型(1:充值 2:提现)")
    private Integer type;
    @ApiModelProperty("支付链接")
    private String payUrl;

}

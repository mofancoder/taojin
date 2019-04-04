package com.tj.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 赛事信息redis缓存类
 *
 * @Auther: kevin
 * @Date: 2018/11/28 10:24
 * @Description:
 */
@Data
@ApiModel
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RedisRaceInfo {
    @ApiModelProperty(value = "赛事ID")
    private String id;
    @ApiModelProperty(value = "类别")
    private String category;
    @ApiModelProperty("开赛时间")
    private Date startTime;
    @ApiModelProperty("结束时间")
    private Date endTime;
    @ApiModelProperty("主队")
    private String homeTeam;
    @ApiModelProperty("客队")
    private String visitTeam;
    @ApiModelProperty("创建时间")
    private Date createTime;
    @ApiModelProperty("更新时间")
    private Date updateTime;
    @ApiModelProperty("上架状态(0:未上架 1:已上架)")
    private Integer shelvesStatus;
    @ApiModelProperty("赛事状态:(0:取消1:正常进行中 2:已经结束3:未开始)")
    private Integer raceResult;
    @ApiModelProperty("获胜队伍")
    private String winTeam;
    @ApiModelProperty("赛果")
    private String winResult;
    @ApiModelProperty("赛果类型")
    private Integer winType;
    @ApiModelProperty("半场结果")
    private String halfResult;
    @ApiModelProperty("数据库是否存在 1:存在 0不存在")
    private int isExist;

    @ApiModelProperty("赛事赔率、返利利率")
    private List<RedisRaceRebateInfo> rebates;
    @ApiModelProperty("是否推荐 1:推荐 0：不推荐")
    private Integer isRecommend;
    @ApiModelProperty("推荐权重")
    private BigDecimal weight;
    @ApiModelProperty("可下单量")
    private BigDecimal validAmount;
    @ApiModelProperty("总交易量")
    private BigDecimal totalValidAmount;
    @ApiModelProperty("进度")
    private BigDecimal process;

    @ApiModelProperty("总已下注")
    private BigDecimal totalBetAmount;

    @ApiModelProperty("英文类别")
    private String enCategory;
    @ApiModelProperty("英文主队")
    private String enHomeTeam;
    @ApiModelProperty("英文客队")
    private String enVisitTeam;
}

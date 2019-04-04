package com.tj.event.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
public class ExcelRace {

    @Excel(name = "赛事编号")
    @NotBlank
    private String id;

    @Excel(name = "赛事类别")
    @NotBlank
    private String category;

    @Excel(name = "开赛时间")
    @NotBlank
    private String startTime;

    @Excel(name = "主队名称")
    @NotBlank
    private String homeTeam;

    @Excel(name = "客队名称")
    @NotBlank
    private String visitTeam;

    @Excel(name = "是否上架")
    private String shelvesStatus;

    @Excel(name = "获胜团队")
    private String winTeam;

    @Excel(name = "最终比分")
    private String winResult;

    @Excel(name = "赛事状态")
    private String status;

    @Excel(name = "半场比分")
    private String halfResult;

    @Excel(name = "是否推荐")
    private Integer isRecommend;

    @Excel(name = "比重")
    private BigDecimal weight;


    @Excel(name = "比分1：0")
    private String score1to0;

    @Excel(name = "比分2：0")
    private String score2to0;

    @Excel(name = "比分2：1")
    private String score2to1;

    @Excel(name = "比分3：0")
    private String score3to0;

    @Excel(name = "比分3：1")
    private String score3to1;

    @Excel(name = "比分3：2")
    private String score3to2;

    @Excel(name = "比分4：0")
    private String score4to0;

    @Excel(name = "比分4：1")
    private String score4to1;

    @Excel(name = "比分4：2")
    private String score4to2;

    @Excel(name = "比分4：3")
    private String score4to3;

    @Excel(name = "比分0：0")
    private String score0to0;

    @Excel(name = "比分1：1")
    private String score1to1;

    @Excel(name = "比分2：2")
    private String score2to2;

    @Excel(name = "比分3：3")
    private String score3to3;

    @Excel(name = "比分4：4")
    private String score4to4;

    @Excel(name = "其他比分")
    private  String scoreOthers;


    public ExcelRace() {}

    public ExcelRace(@NotBlank String id, @NotBlank String category, @NotBlank String startTime, @NotBlank String homeTeam, @NotBlank String visitTeam, String shelvesStatus, String winTeam, String winResult, String status, String halfResult, Integer isRecommend, BigDecimal weight, String score1to0, String score2to0, String score2to1, String score3to0, String score3to1, String score3to2, String score4to0, String score4to1, String score4to2, String score4to3, String score0to0, String score1to1, String score2to2, String score3to3, String score4to4, String scoreOthers) {
        this.id = id;
        this.category = category;
        this.startTime = startTime;
        this.homeTeam = homeTeam;
        this.visitTeam = visitTeam;
        this.shelvesStatus = shelvesStatus;
        this.winTeam = winTeam;
        this.winResult = winResult;
        this.status = status;
        this.halfResult = halfResult;
        this.isRecommend = isRecommend;
        this.weight = weight;
        this.score1to0 = score1to0;
        this.score2to0 = score2to0;
        this.score2to1 = score2to1;
        this.score3to0 = score3to0;
        this.score3to1 = score3to1;
        this.score3to2 = score3to2;
        this.score4to0 = score4to0;
        this.score4to1 = score4to1;
        this.score4to2 = score4to2;
        this.score4to3 = score4to3;
        this.score0to0 = score0to0;
        this.score1to1 = score1to1;
        this.score2to2 = score2to2;
        this.score3to3 = score3to3;
        this.score4to4 = score4to4;
        this.scoreOthers = scoreOthers;
    }
}

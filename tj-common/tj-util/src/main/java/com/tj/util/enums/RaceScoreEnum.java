package com.tj.util.enums;

import com.tj.util.A.SacException;
import lombok.Getter;

/**
 * @program: tj-core
 * @description: 比赛分数枚举
 * @author: yangzhixin
 * @create: 2019-01-04
 **/
@Getter
public enum RaceScoreEnum {
    score_0_0("0:0", "0:0"), score_1_0("1:0", "1:0"),
    score_0_1("0:1", "0:1"), score_1_1("1:1", "1:1"),
    score_0_2("0:2", "0:2"), score_1_2("1:2", "1:2"),
    score_0_3("0:3", "0:3"), score_1_3("1:3", "1:3"),
    score_0_4("0:4", "0:4"), score_1_4("1:4", "1:4"),
    score_0_5("0:5", "0:5"), score_1_5("1:5", "1:5"),
    score_2_0("2:0", "2:0"), score_3_0("3:0", "3:0"),
    score_2_1("2:1", "2:1"), score_3_1("3:1", "3:1"),
    score_2_2("2:2", "2:2"), score_3_2("3:2", "3:2"),
    score_2_3("2:3", "2:3"), score_3_3("3:3", "3:3"),
    score_2_4("2:4", "2:4"), score_3_4("3:4", "3:4"),
    score_2_5("2:5", "2:5"), score_3_5("3:5", "3:5"),
    score_4_0("4:0", "4:0"), score_5_0("5:0", "5:0"),
    score_4_1("4:1", "4:1"), score_5_1("5:1", "5:1"),
    score_4_2("4:2", "4:2"), score_5_2("5:2", "5:2"),
    score_4_3("4:3", "4:3"), score_5_3("5:3", "5:3"),
    score_4_4("4:4", "4:4"), score_5_4("5:4", "5:4"),
    score_4_5("4:5", "4:5"), score_5_5("5:5", "5:5");
    private String code;
    private String desc;

    RaceScoreEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static RaceScoreEnum codeOf(String code) {
        for (RaceScoreEnum v : values()) {
            if (v.code.equals(code)) {
                return v;
            }
        }
        throw new SacException("不支持的赛事状态");
    }
}

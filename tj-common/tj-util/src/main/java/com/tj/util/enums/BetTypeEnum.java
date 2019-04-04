package com.tj.util.enums;

import com.tj.util.A.SacException;
import lombok.Getter;

/**
 * @program: tj-core
 * @description: 投注类型
 * @author: liang.song
 * @create: 2018-12-12-15:55
 **/
@Getter
public enum BetTypeEnum {
    score(1, "波胆"), half_bet(2, "半场波胆"),
    bet_cancel(0,"赛事取消自动撤注"),bet_normal(1,"已投注"),
    user_cancel(2,"用户取消投注");
    private Integer code;
    private String desc;


    BetTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static BetTypeEnum codeOf(Integer code) {
        for (BetTypeEnum v : values()) {
            if (v.code.equals(code)) {
                return v;
            }
        }
        throw new SacException("不支持的投注类型");
    }
}

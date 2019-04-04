package com.tj.util.enums;

import com.tj.util.A.SacException;
import lombok.Getter;

/**
 * @program: tj-core
 * @description: 投注结果枚举
 * @author: liang.song
 * @create: 2018-12-13-15:58
 **/
@Getter
public enum BetResultEnum {
    lose(0, "输"), win(1, "赢");
    private Integer code;
    private String desc;

    BetResultEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static BetResultEnum codeOf(Integer code) {
        for (BetResultEnum v : values()) {
            if (v.code.equals(code)) {
                return v;
            }
        }
        throw new SacException("不支持的投注结果类型");
    }
}

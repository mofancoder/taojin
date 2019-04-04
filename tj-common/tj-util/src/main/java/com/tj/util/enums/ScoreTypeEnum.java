package com.tj.util.enums;

import com.tj.util.A.SacException;
import lombok.Getter;

@Getter
public enum ScoreTypeEnum {
    closed(0, "比分关闭"), open(1, "比分开启");
    private Integer code;
    private String desc;

    ScoreTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ScoreTypeEnum codeOf(Integer code) {
        for (ScoreTypeEnum v : values()) {
            if (v.code.equals(code)) {
                return v;
            }
        }
        throw new SacException("不支持的投注结果类型");
    }

}

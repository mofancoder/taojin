package com.tj.util.enums;

import com.tj.util.A.SacException;
import lombok.Getter;

@Getter
public enum DiveTypeEnum {
    closed(0, "跳水关闭"), open(1, "跳水开启");
    private Integer code;
    private String desc;

    DiveTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static DiveTypeEnum codeOf(Integer code) {
        for (DiveTypeEnum v : values()) {
            if (v.code.equals(code)) {
                return v;
            }
        }
        throw new SacException("不支持的投注结果类型");
    }

}

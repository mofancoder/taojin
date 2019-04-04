package com.tj.util.enums;

import lombok.Getter;

@Getter
public enum OptStatusEnum {

    NO_LOCKED(0),

    LOCKED(1);
    private Integer code;

    OptStatusEnum(Integer code) {
        this.code = code;
    }

    public static OptStatusEnum codeOf(Integer code) {
        if (code == null || code >= values().length) {
            throw new RuntimeException("参数不正确");
        }
        return values()[code];
    }
}

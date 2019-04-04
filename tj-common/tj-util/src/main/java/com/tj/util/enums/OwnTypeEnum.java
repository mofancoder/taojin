package com.tj.util.enums;

import lombok.Getter;

@Getter
public enum OwnTypeEnum {
    SELF(1, "自营"),
    NOT_SELF(0, "非自营");
    private Integer code;
    private String desc;

    OwnTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static OwnTypeEnum valueOf(Integer code) {
        for (OwnTypeEnum v : values()) {
            if (v.code == code) {
                return v;
            }
        }
        return SELF;
    }
}

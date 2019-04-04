package com.tj.util.enums;

import lombok.Getter;

@Getter
public enum TimeEnum {
    year(0), week(1), month(2), month3(3), total(4);
    private Integer code;

    TimeEnum(Integer code) {
        this.code = code;
    }

    public static TimeEnum codeOf(Integer code) {
        for (TimeEnum v : values()) {
            if (v.code.equals(code)) {
                return v;
            }
        }
        return week;
    }

}

package com.tj.util.enums;

import com.tj.util.A.SacException;
import lombok.Getter;

@Getter
public enum TimeRangeEnum {
    out(0, "时间范围外"), inner(1, "时间范围内");
    private Integer code;
    private String desc;

    TimeRangeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static TimeRangeEnum codeOf(Integer code) {
        for (TimeRangeEnum v : values()) {
            if (v.code.equals(code)) {
                return v;
            }
        }
        throw new SacException("不支持的时间范围类型");
    }

}

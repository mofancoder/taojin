package com.tj.util.enums;

import com.tj.util.A.SacException;
import lombok.Getter;

@Getter
public enum TerminalTypeEnum {
    //1.mobile 2. pc 3.H5-mobile
    MOBILE(1, "手机端"), PC(2, "PC"), H5_MOBILE(3, "H5_mobile");
    private Integer code;
    private String desc;

    TerminalTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static TerminalTypeEnum codeOf(Integer code) {
        for (TerminalTypeEnum v : values()) {
            if (v.code.equals(code)) {
                return v;
            }
        }
        throw new SacException("不支持的终端类型");
    }
}

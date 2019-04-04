package com.tj.util;

import lombok.Getter;

@Getter
public enum SacRecdStatusEnum {
    fail(0, "失败"),
    success(1, "成功"),
    processing(2, "处理中"),
    timeout(3, "超时未支付");
    private Integer code;
    private String desc;

    SacRecdStatusEnum(Integer code, String desc) {

        this.code = code;
        this.desc = desc;
    }

    public static SacRecdStatusEnum codeOf(Integer code) {
        for (SacRecdStatusEnum v : values()) {
            if (v.code.equals(code)) {
                return v;
            }
        }
        return fail;
    }
}

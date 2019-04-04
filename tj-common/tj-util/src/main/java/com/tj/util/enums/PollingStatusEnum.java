package com.tj.util.enums;

import com.tj.util.A.SacException;
import lombok.Getter;

@Getter
public enum PollingStatusEnum {
    OK(1, "处理成功"), PROCESS(2, "处理中"), FAIL(3, "失败"), NOT_EXISTED(4, "订单不存在");
    private Integer code;
    private String desc;

    PollingStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static PollingStatusEnum codeOf(Integer code) {
        for (PollingStatusEnum v : values()) {
            if (v.code.equals(code)) {
                return v;
            }
        }
        throw new SacException("不支持的订单状态");
    }
}

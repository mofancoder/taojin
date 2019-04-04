package com.tj.util.enums;

import lombok.Getter;

@Getter
public enum AuditStatusEnum {
    FAIL(0, "审核失败"), SUCCESS(1, "审核成功"), AUDITING(2, "审核中");
    private Integer code;
    private String desc;

    AuditStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static AuditStatusEnum codeOf(Integer code) {
        for (AuditStatusEnum v : values()) {
            if (v.code == code) {
                return v;
            }
        }
        return AUDITING;
    }
}

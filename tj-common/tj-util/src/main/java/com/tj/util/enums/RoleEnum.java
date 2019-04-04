package com.tj.util.enums;

import lombok.Getter;

@Getter
public enum RoleEnum {
    SUPER_ADMIN(1, "超级管理员"), ADMIN(2, "普通管理员");
    private Integer code;
    private String desc;

    RoleEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static RoleEnum codeOf(Integer code) {
        for (RoleEnum v : values()) {
            if (v.code.equals(code)) {
                return v;
            }
        }
        throw new RuntimeException("没有此角色");
    }
}

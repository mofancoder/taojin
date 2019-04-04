package com.tj.util.enums;

import lombok.Getter;

@Getter
public enum RaceApiCode {
    //{1:Success 成功,
    // 101: User Id or Hash is Incorrect 账户/MD5 Hash 值不对
    // 500：Internal Server Error 系统内部问题}
    success("1", "成功"), incorrect("101", "账户/MD5 Hash 值不对"), server_error("500", "系统内部问题");

    private String code;
    private String desc;

    RaceApiCode(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static RaceApiCode codeOf(String code) {
        for (RaceApiCode v : values()) {
            if (v.code.equals(code)) {
                return v;
            }
        }
        throw new RuntimeException("不支持的code类型");
    }
}



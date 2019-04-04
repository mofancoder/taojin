package com.tj.util.enums;

import lombok.Getter;

@Getter
public enum CaptchaType {
    register(0, "注册"), forgetPassword(1, "忘记密码");
    private Integer code;
    private String name;

    CaptchaType(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static CaptchaType codeOf(Integer code) {
        for (CaptchaType v : values()) {
            if (v.code.equals(code)) {
                return v;
            }
        }
        return register;
    }

    public String toString() {
        return String.valueOf(this.code);
    }

}

package com.tj.util.enums;

import lombok.Getter;

@Getter
public enum ProxyEnum {
    //{0:非代理,1:代理,2:平台商户 3：系统管理员}
    not_proxy(0), proxy(1), sys_merchant(2), admin(3);

    private Integer code;

    ProxyEnum(Integer code) {
        this.code = code;
    }

    public static ProxyEnum codeOf(Integer code) {
        for (ProxyEnum v : values()) {
            if (v.code.equals(code)) {
                return v;
            }
        }
        throw new RuntimeException("不支持的代理类型");
    }
}

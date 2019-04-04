package com.tj.util.enums;

import lombok.Getter;

@Getter
public enum ChannelCodeType {
    alipay(1, "支付宝"),
    wechat(2, "微信"),
    credit(3, "银行卡");
    private Integer code;
    private String desc;

    ChannelCodeType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ChannelCodeType codeOf(Integer code) {
        for (ChannelCodeType v : values()) {
            if (v.code.equals(code)) {
                return v;
            }
        }
        throw new RuntimeException("不支持的交易平台");
    }
}

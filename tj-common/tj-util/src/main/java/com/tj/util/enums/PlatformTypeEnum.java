package com.tj.util.enums;

import lombok.Getter;

/**
 * @program: tj-core
 * @description: 交易平台类型
 * @author: liang.song
 * @create: 2018-11-28-11:00
 **/
@Getter
public enum PlatformTypeEnum {
    alipay(1, "支付宝"),
    wechat(2, "微信"),
    credit(3, "银行卡"),
    offlineAlipay(4, "线下支付宝"),
    offlieWechat(5, "线下微信"),
    offlineCredit(6, "线下银行卡");
    private Integer code;
    private String desc;

    PlatformTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static PlatformTypeEnum codeOf(Integer code) {
        for (PlatformTypeEnum v : values()) {
            if (v.code.equals(code)) {
                return v;
            }
        }
        throw new RuntimeException("不支持的交易平台");
    }
}

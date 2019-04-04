package com.tj.util;

import lombok.Getter;

/**
 * @program: tj-core
 * @description: 交易类型
 * @author: liang.song
 * @create: 2018-11-27-19:42
 **/
@Getter
public enum TransactionTypeEnum {

    charge(1, "充值"), withdraw(2, "提现");

    private Integer code;

    private String desc;

    TransactionTypeEnum(Integer code, String desc) {
        this.code = code;

    }

    public static TransactionTypeEnum codeOf(Integer code) {
        for (TransactionTypeEnum v : values()) {
            if (v.code.equals(code)) {
                return v;
            }
        }
        throw new RuntimeException("不支持的交易类型");
    }
}

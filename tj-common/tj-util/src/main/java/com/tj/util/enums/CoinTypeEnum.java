package com.tj.util.enums;

import lombok.Getter;

@Getter
public enum CoinTypeEnum {
    SAC(1), PNB(2), PNB_BITGO(3);
    private Integer code;

    CoinTypeEnum(Integer code) {
        this.code = code;
    }

    public static CoinTypeEnum codeOf(Integer code) {
        for (CoinTypeEnum v : values()) {
            if (v.getCode().equals(code)) {
                return v;
            }
        }
        return SAC;
    }

}

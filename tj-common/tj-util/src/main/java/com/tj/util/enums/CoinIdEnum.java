package com.tj.util.enums;

import lombok.Getter;

/**
 * Created by ldh on 2018-02-06.
 */
@Getter
public enum CoinIdEnum {
    NONE(1),
    SAC(1000),//安币
    BTC(1),//2
    ETH(1),//3
    USDT(1),   //泰达币
    SSAC(1),//恒币  5
    CHEX(1000),//6
    FGC(10000);//7
    private Integer unit;

    CoinIdEnum(Integer unit) {
        this.unit = unit;
    }

    public static CoinIdEnum valueOf(Integer origin) {
        for (CoinIdEnum v : values()) {
            if (v.ordinal() == origin) {
                return v;
            }
        }
        return NONE;
    }
}

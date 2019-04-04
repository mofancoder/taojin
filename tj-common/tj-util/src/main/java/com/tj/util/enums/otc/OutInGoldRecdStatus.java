package com.tj.util.enums.otc;

import com.tj.util.SacRecdStatusEnum;
import lombok.Getter;

/**
 * Created by ldh on 2018-03-08.
 */
@Getter
public enum OutInGoldRecdStatus {
    Unknown(0, SacRecdStatusEnum.processing),
    Processing(1, SacRecdStatusEnum.processing),//处理中
    Suc(2, SacRecdStatusEnum.success),//成功
    Fail(3, SacRecdStatusEnum.fail);//失败
    private Integer code;
    private SacRecdStatusEnum sacRecdStatusEnum;

    OutInGoldRecdStatus(Integer code, SacRecdStatusEnum sacRecdStatusEnum) {
        this.code = code;
        this.sacRecdStatusEnum = sacRecdStatusEnum;
    }

    public static OutInGoldRecdStatus codeOf(Integer code) {
        for (OutInGoldRecdStatus v : values()) {
            if (v.code.equals(code)) {
                return v;
            }
        }
        throw new RuntimeException("不支持的订单状态:" + code);
    }
}

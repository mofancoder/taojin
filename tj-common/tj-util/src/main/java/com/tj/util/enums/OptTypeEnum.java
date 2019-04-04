package com.tj.util.enums;

import com.tj.util.A.SacException;
import lombok.Getter;

/**
 * @program: tj-core
 * @description: 用户积分变动操作类型
 * @author: liang.song
 * @create: 2018-11-28-15:57
 **/
@Getter
public enum OptTypeEnum {
    charge(1, "充值"), withdraw(2, "提现"), bet(3, "下注"),
    cancel_bet(4, "赛事取消"), rebate(5, "开奖"),
    cancel_user(6,"用户撤注");

    private Integer code;
    private String desc;

    OptTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static OptTypeEnum codeOf(Integer code) {
        for (OptTypeEnum v : values()) {
            if (v.code.equals(code)) {
                return v;
            }
        }
        throw new SacException("不支持的类型");
    }
}

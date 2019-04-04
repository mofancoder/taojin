package com.tj.util.enums;

import com.tj.util.A.SacException;
import lombok.Getter;

/**
 * @program: tj-core
 * @description: 返利状态
 * @author: liang.song
 * @create: 2018-12-12-16:13
 **/
@Getter
public enum RebateStatusEnum {
    fail(0, "返利失败"), success(1, "返利成功"), un_rebate(2, "未返利");
    private Integer code;
    private String desc;

    RebateStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static RebateStatusEnum codeOf(Integer code) {
        for (RebateStatusEnum v : values()) {
            if (v.code.equals(code)) {
                return v;
            }
        }
        throw new SacException("不支持的返利状态");
    }
}

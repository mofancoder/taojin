package com.tj.util.enums;

import com.tj.util.A.SacException;
import lombok.Getter;

/**
 * @program: tj-core
 * @description: ${description}
 * @author: liang.song
 * @create: 2018-12-03-14:13
 **/
@Getter
public enum DateTypeEnum {
    Week(1, "近一周"), month(2, "近一个月"), year(3, "近一年"), all(4, "全部");

    private Integer code;

    private String desc;

    DateTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static DateTypeEnum codeOf(Integer code) {
        for (DateTypeEnum v : values()) {
            if (v.code.equals(code)) {
                return v;
            }
        }
        throw new SacException("不支持的日期类型");
    }
}

package com.tj.util.enums;

import com.tj.util.A.SacException;
import lombok.Getter;

@Getter
public enum SortStyleEnum {
    flat(1, "推荐赛事排序风格"), complex(2, "列表赛事排序风格");
    private Integer code;
    private String desc;

    SortStyleEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static SortStyleEnum codeOf(Integer code) {
        for (SortStyleEnum v : values()) {
            if (v.code.equals(code)) {
                return v;
            }
        }
        throw new SacException("不支持的排序风格类型");
    }

}

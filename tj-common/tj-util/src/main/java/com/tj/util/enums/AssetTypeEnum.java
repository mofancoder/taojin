package com.tj.util.enums;

import lombok.Getter;

@Getter
public enum AssetTypeEnum {
    ALL(0, "全部"),
    PERSONAL(-1, "个人"),
    MERCHANT(-4, "商户"),
    SELF(-2, "自营"),
    NOT_SELF(-3, "第三方"),
    COMPANY(-5, "公司"),
    FIXED(-6, "固定资金"),
    WORKING(-7, "运营资金"),
    TESTING(-8, "测试资金");
    private Integer code;
    private String desc;

    AssetTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static AssetTypeEnum valueOf(Integer code) {
        if (code == null) {
            return ALL;
        }
        for (AssetTypeEnum value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        return ALL;
    }
}

package com.tj.util.enums;

/**
 * created by lh 2018-11-30
 */
public enum SupBannerTypeEnum {
    TeamRank(0, "团队排行");//团队排行
    private Integer code;
    private String type;

    private SupBannerTypeEnum(Integer code, String type) {
        this.code = code;
        this.type = type;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

package com.tj.util.enums;

/**
 * 汇率种类
 */
public enum RateTypeEnum {
    RMB(0, "人民币"),//人民币
    USD(1, "美元"),//美元
    EUR(3, "欧元"),//欧元
    GBP(4, "英镑"),//英镑
    JPY(5, "日元"),//日元
    HKD(6, "港币");//港币

    private Integer code;
    private String rateName;

    private RateTypeEnum(Integer code, String rateName) {
        this.code = code;
        this.rateName = rateName;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getRateName() {
        return rateName;
    }

    public void setRateName(String rateName) {
        this.rateName = rateName;
    }
}

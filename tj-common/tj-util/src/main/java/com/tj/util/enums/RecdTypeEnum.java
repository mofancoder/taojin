package com.tj.util.enums;

public enum RecdTypeEnum {
    NONE(0, "无"),
    REDEEM(1, "赎回"),
    VOTE(2, "投票");

    private Integer code;
    private String typeName;

    private RecdTypeEnum(Integer code, String typeName) {
        this.code = code;
        this.typeName = typeName;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}

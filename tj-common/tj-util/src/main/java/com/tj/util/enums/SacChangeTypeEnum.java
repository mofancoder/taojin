package com.tj.util.enums;

public enum SacChangeTypeEnum {
    Application(0, "应用互转"),
    BlockChain(1, "链上互转"),
    InsideTransfer(2, "用户互转");
    private Integer code;
    private String typeName;

    private SacChangeTypeEnum(Integer code, String typeName) {
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

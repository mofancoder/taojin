package com.tj.util.enums;

public enum AlarmEnum {

    Audit(0, "审核异常"),        //审核异常
    Chain(1, "上链异常"),        //上链异常
    Arrive(2, "到账延迟");        //到账延迟

    private Integer code;
    private String typeName;

    private AlarmEnum(Integer code, String typeName) {
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

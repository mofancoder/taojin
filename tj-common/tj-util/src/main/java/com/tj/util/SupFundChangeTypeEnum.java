package com.tj.util;

/**
 * created by lh 2018-10-15
 */
public enum SupFundChangeTypeEnum {
    None(0, "全部"),
    Redeem(1, "赎回"),
    Vote(2, "投票"),
    Reward(3, "奖励"),
    Income(4, "收益"),
    Charge(5, "充值"),
    WithDraw(6, "提现"),
    InBlockChain(7, "链上转入"),
    OutBlockChain(8, "链上转出");


    private Integer code;
    private String typeName;

    private SupFundChangeTypeEnum(Integer code, String typeName) {
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

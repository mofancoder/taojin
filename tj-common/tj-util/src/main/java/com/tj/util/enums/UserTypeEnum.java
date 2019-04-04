package com.tj.util.enums;

/**
 * Created by ldh on 2018-03-14.
 */
public enum UserTypeEnum {
    //用户类型:1-普通用户 2-马甲用户 4-代理用户 8-超级用户  16-企业用户
    Normal(1), Puppeteer(2), Agent(4), SuperUser(8), Enterprise(16);
    private byte value;

    private UserTypeEnum(int a) {
        this.value = (byte) a;
    }

    public byte getValue() {
        return this.value;
    }
}

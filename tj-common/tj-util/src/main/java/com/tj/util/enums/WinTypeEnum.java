package com.tj.util.enums;

import com.tj.util.A.SacException;
import lombok.Getter;

@Getter
public enum WinTypeEnum {
    HOME_WIN(1, "主队胜"), VISIT_WIN(2, "客队胜"), BALANCE(0, "平局"), NO_VALUE(3, "暂无结果");
    private Integer code;
    private String desc;

    WinTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static WinTypeEnum codeOf(Integer code) {
        for (WinTypeEnum v : values()) {
            if (v.code.equals(code)) {
                return v;
            }
        }
        throw new SacException("不支持的投注结果类型");
    }

}

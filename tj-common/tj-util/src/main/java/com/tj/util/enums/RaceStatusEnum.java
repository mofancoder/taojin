package com.tj.util.enums;

import com.tj.util.A.SacException;
import lombok.Getter;

/**
 * @program: tj-core
 * @description: 比赛状态枚举
 * @author: liang.song
 * @create: 2018-12-13-14:41
 **/
@Getter
public enum RaceStatusEnum {
    cancel(0, "取消"), processing(1, "进行中"), end(2, "结束"), un_start(3, "未开始");
    private Integer code;
    private String desc;

    RaceStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static RaceStatusEnum codeOf(Integer code) {
        for (RaceStatusEnum v : values()) {
            if (v.code.equals(code)) {
                return v;
            }
        }
        throw new SacException("不支持的赛事状态");
    }
}

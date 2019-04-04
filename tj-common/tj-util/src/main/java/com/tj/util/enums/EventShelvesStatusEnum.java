package com.tj.util.enums;

import lombok.Getter;

/**
 * @ProjectName: TJ
 * @Author: MOFAN889
 * @Description: 赛事上下架状态
 * @Date: 2018/12/3 11:52
 */
@Getter
public enum EventShelvesStatusEnum {

    NotOnShelves(0,"未上架"),
    OnShelves(1,"已上架");
    private Integer code;
    private String describe;

    EventShelvesStatusEnum(Integer code,String describe ) {
        this.code = code;
        this.describe = describe;
    }

    public static EventShelvesStatusEnum codeOf(Integer code) {
        for (EventShelvesStatusEnum v : values()) {
            if (v.code.equals(code)) {
                return v;
            }
        }
        return null;
    }

}

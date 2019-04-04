package com.tj.util.enums;

import lombok.Getter;

/**
 * @ProjectName: TJ
 * @Author: MOFAN889
 * @Description: 赛事上下架状态
 * @Date: 2018/12/3 11:52
 */
@Getter
public enum EventRecommendStatusEnum {

    NotRecommend(0,"未推荐"),
    IsRecommend(1,"已推荐");
    private Integer code;
    private String describe;

    EventRecommendStatusEnum(Integer code, String describe ) {
        this.code = code;
        this.describe = describe;
    }

    public static EventRecommendStatusEnum codeOf(Integer code) {
        for (EventRecommendStatusEnum v : values()) {
            if (v.code.equals(code)) {
                return v;
            }
        }
        return null;
    }

}

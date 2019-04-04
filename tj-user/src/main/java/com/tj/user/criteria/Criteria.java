package com.tj.user.criteria;

import java.util.Optional;

/**
 * 过滤条件类
 */
public interface Criteria<I, R> {
    /**
     * 过滤器名称
     *
     * @return 名称
     */
    String name();


    default CriteriaType type() {
        return CriteriaType.common;
    }

    default Optional<R> meetCriteria(I param) {
        return Optional.empty();
    }

    enum CriteriaType {
        charge(1), withdraw(2), common(3), user(4);

        private Integer code;

        CriteriaType(Integer code) {
            this.code = code;
        }

        public static CriteriaType codeOf(Integer code) {
            for (CriteriaType v : values()) {
                if (v.code.equals(code)) {
                    return v;
                }
            }
            throw new RuntimeException("不支持的过滤类型");
        }
    }
}

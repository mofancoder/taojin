package com.tj.util.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 集合或数组的大小是否在指定范围内
 *
 * @author yelo
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Size {
    public Class<?>[] scope() default {};

    public int min();

    public int max();

    public String msg() default "该字段长度必须在%d~%d之间";
}

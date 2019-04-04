package com.tj.util.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 字段大小必须在此范围内
 *
 * @author yelo
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Range {

    public Class<?>[] scope() default {};

    public int min();

    public int max();

    public String msg() default "该字段大小必须在%d~%d之间";
}

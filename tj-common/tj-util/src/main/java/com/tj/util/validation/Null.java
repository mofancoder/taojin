package com.tj.util.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 指定字段必须为空
 *
 * @author acer
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Null {
    public Class<?>[] scope() default {};

    public String msg() default "该字段必须为null";
}

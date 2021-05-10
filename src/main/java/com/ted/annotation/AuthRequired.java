package com.ted.annotation;

import java.lang.annotation.*;

/**
 * 授权注解
 */
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthRequired {
    boolean loginSuccess() default true;
}

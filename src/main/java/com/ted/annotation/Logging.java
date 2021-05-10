package com.ted.annotation;

import java.lang.annotation.*;

/**
 * 日志注解
 */
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Logging {
    String module() default ""; // 操作模块
    String type() default ""; // 操作类型
    String description() default ""; // 操作描述
}

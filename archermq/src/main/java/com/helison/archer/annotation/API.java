package com.helison.archer.annotation;

/*
代表此接口是一个用户API接口，目的是为了向用户开放，仅为标记使用
 */
public @interface API {
    String value() default "";
}

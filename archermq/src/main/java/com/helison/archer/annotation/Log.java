package com.helison.archer.annotation;

import com.helison.archer.utils.log.LogHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
代表需要记录日志的方法或者类
其中使用的
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)

public @interface Log {
    Class<LogHandler> before();
    Class<LogHandler> after();
    Class<LogHandler> exception();
    Class<LogHandler> round();
    String[] params();
}

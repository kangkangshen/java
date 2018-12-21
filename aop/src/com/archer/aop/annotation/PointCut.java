package archer.aop.annotation;

import archer.container.support.debug.Description;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Description(description = "使用正则表达式完成对切点的识别，相关语法请参阅./config/README.md")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PointCut {
    String value();

}

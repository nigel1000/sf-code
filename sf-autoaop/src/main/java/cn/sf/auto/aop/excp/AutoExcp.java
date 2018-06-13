package cn.sf.auto.aop.excp;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//可以配合elasticsearch使用
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface AutoExcp {
    String module() default "auto log default module";
    String tag() default "auto log default tag";
}

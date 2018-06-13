package cn.sf.auto.aop.print;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//可以配合elasticsearch使用
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface AutoLog {
    String module() default "auto log default module";
    String tag() default "auto log default tag";

    //是否打印入参
    boolean logParam() default true;
    //是否打印响应
    boolean logResult() default true;

}

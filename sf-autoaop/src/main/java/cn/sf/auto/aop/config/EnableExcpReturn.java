package cn.sf.auto.aop.config;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(FetchEnableExcpReturnParam.class)
public @interface EnableExcpReturn {

    // 默认错误返回的类
    // 调用的方法(返回类型必须是默认错误返回的类)
    // 方法入参必须是string(传递错误信息用)
    // 否则返回null。
    Class[] returnTypes() default {};
    String[] methodNames() default {};

}
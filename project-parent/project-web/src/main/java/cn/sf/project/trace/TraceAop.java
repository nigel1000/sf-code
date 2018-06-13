package cn.sf.project.trace;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;

/**
 * Created by nijianfeng on 17/8/24.
 */
@Aspect
@Order(value = 0)
@Slf4j
public class TraceAop {

    @Pointcut("@within(cn.sf.auto.aop.print.AutoLog) && execution(public * *(..))")
    public void TraceIdClass() {
    }
    @Before(value = "TraceIdClass()")
    public void doTraceIdBefore(final JoinPoint point) {
        TraceIdUtil.initTraceId();
    }

}

package cn.sf.auto.aop.order;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Created by nijianfeng on 17/5/18.
 */
@Aspect
@Order(value = 0)
@Slf4j
@Component
public class OrderAop2 {

    @Pointcut("execution(public public * cn.sf.auto.aop.clazz..*.*(..))")
    public void order2() {}

    @Around("order2()")
    public Object around(final ProceedingJoinPoint point) throws Throwable {
        log.info(this.getClass().getName()+" begin");
        try{
            Object proceed = point.proceed();
            log.info(this.getClass().getName()+" end");
            return proceed;
        }catch (Exception ex){
            log.info(this.getClass().getName()+" exception");
        }finally {
            log.info(this.getClass().getName()+" finally");
            return null;
        }
    }

}




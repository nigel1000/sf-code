package cn.sf.auto.aop.print;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;

import java.lang.reflect.Method;

@Aspect
@Order(value = 0)
@Slf4j
public class AutoLogAop {

    // @within 用于匹配所以持有指定注解类型内的方法；代理织入
    @Pointcut("@within(cn.sf.auto.aop.print.AutoLog) && execution(public * *(..))")
    public void AutoLogAspectClass() {}

    // @annotation 用于匹配当前执行方法持有指定注解的方法；运行切入
    @Pointcut("@annotation(cn.sf.auto.aop.print.AutoLog)")
    public void AutoLogAspectMethod() {}

    // [测试模块][测试]Param List-->cn.sf.auto.log.clazz.LogClass#test1:["input"]
    @Before(value = "AutoLogAspectClass() || AutoLogAspectMethod()")
    public void doServiceBefore(final JoinPoint point) {
        Class<?> clazz = point.getTarget().getClass();
        Method iMethod = ((MethodSignature) point.getSignature()).getMethod();// interface或者class方法,实现类可用接口来调用
        Method method;// 真实类的方法
        try {
            method = clazz.getMethod(iMethod.getName(), iMethod.getParameterTypes());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        // 方法上有SkipAutoLog注解,不打log
        if (isSkipAutoLogWork(method)) {
            return;
        }

        // 优先使用方法上的注解
        AutoLog autoLog = getAutoLogAnno(method, clazz);
        if (null == autoLog) {
            // 方法或类上都没有AutoLog注解,不打log
            return;
        }

        if (autoLog.logParam()) {
            logBefore(method, clazz, autoLog, point.getArgs());
        }
    }

    // [测试模块][测试]Return List-->cn.sf.auto.log.clazz.LogClass#test1:"output"
    @AfterReturning(returning = "rtObj", value = "AutoLogAspectClass() || AutoLogAspectMethod()")
    public void doServiceAfter(final JoinPoint point, final Object rtObj) {
        Method method = ((MethodSignature) point.getSignature()).getMethod();
        Class clazz = point.getTarget().getClass();

        // 方法上有SkipAutoLog注解,不打log
        if (isSkipAutoLogWork(method)) {
            return;
        }

        // 优先使用方法上的注解
        AutoLog autoLog = getAutoLogAnno(method, clazz);
        if (null == autoLog) {
            // 方法或类上都没有AutoLog注解,不打log
            return;
        }

        if (autoLog.logResult()) {
            logAfter(method, clazz, autoLog, rtObj);
        }
    }


    private void logBefore(final Method method, final Class clazz, final AutoLog autoLog, final Object[] args) {

        StringBuilder sb = new StringBuilder();

        // 按注解参数拼装log
        appendAnnoParams(autoLog, sb);

        // 打上类名
        sb.append("Param List-->");
        sb.append(clazz.getName());
        sb.append("#");

        /* 打上方法名 */
        sb.append(method.getName());
        sb.append(":");

        /* 打上入参 */
        String jsonStr = "\t";
        for (int i = 0; i < args.length; i++) {
            try {
                jsonStr += i + ":" + JSONObject.toJSONString(args[i]);
            } catch (Exception ex) {
                jsonStr += i + ":" + "{json化失败了.}";
            }
            jsonStr += "\t";
        }
        sb.append(jsonStr);

        log.info(sb.toString());
    }

    private void logAfter(final Method method, final Class clazz, final AutoLog autoLog, final Object rtObj) {

        StringBuilder sb = new StringBuilder();

        // 按注解参数拼装log
        appendAnnoParams(autoLog, sb);

        // 打上类名
        sb.append("Return List-->");
        sb.append(clazz.getName());
        sb.append("#");

        /* 打上方法名 */
        sb.append(method.getName());
        sb.append(":");

        // 打上返回
        String jsonStr = "\t";
        try {
            jsonStr = JSONObject.toJSONString(rtObj);
        } catch (Exception ex) {
            jsonStr = "{json化失败了.}";
        }
        sb.append(jsonStr);

        log.info(sb.toString());
    }

    private boolean isSkipAutoLogWork(Method method) {
        AutoLogSkip autoLogSkip = method.getAnnotation(AutoLogSkip.class);
        if (null != autoLogSkip) {
            return true;
        } else {
            return false;
        }
    }

    private AutoLog getAutoLogAnno(Method method, Class clazz) {

        // 优先使用方法上的注解
        AutoLog autoLog = method.getAnnotation(AutoLog.class);
        if (null == autoLog) {
            // 类上的注解次之
            autoLog = (AutoLog) clazz.getAnnotation(AutoLog.class);
        }

        return autoLog;
    }

    private void appendAnnoParams(AutoLog autoLog, StringBuilder sb) {
        sb.append("[");
        sb.append(autoLog.module());
        sb.append("]");

        sb.append("[");
        sb.append(autoLog.tag());
        sb.append("]");
    }

}

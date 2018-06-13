package cn.sf.project.trace;

import cn.sf.project.utils.ConstantsUtil;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;


public class TraceIdUtil {
    private static ThreadLocal<Long> traceIdThreadLocal = new ThreadLocal<>();

    public static void initTraceId(HttpServletRequest request) {
        Long currentTraceId;
        String contextTraceId = request.getHeader(ConstantsUtil.TRACE_ID_KEY);
        if(StringUtils.isEmpty(contextTraceId)) {
            contextTraceId = request.getParameter(ConstantsUtil.TRACE_ID_KEY);
        }
        if(!StringUtils.isEmpty(contextTraceId)) {
            try {
                currentTraceId = Long.parseLong(contextTraceId);
            }catch (Exception ex){
                currentTraceId = createTraceId();
            }
        } else {
            currentTraceId = createTraceId();
        }

        traceIdThreadLocal.set(currentTraceId);
        MDC.put(ConstantsUtil.TRACE_ID_KEY, currentTraceId.toString());
    }

    public static void initTraceId() {
        Long currentTraceId;
        Long contextTraceId = getCurrentTraceId();
        if(!StringUtils.isEmpty(contextTraceId)) {
            currentTraceId = contextTraceId;
        } else {
            currentTraceId = createTraceId();
            traceIdThreadLocal.set(currentTraceId);
        }

        MDC.put(ConstantsUtil.TRACE_ID_KEY, currentTraceId.toString());
    }

    private static Long createTraceId() {
        long id = UUID.randomUUID().getMostSignificantBits();
        return Math.abs(id);
    }

    public static Long getCurrentTraceId() {
        return traceIdThreadLocal.get();
    }

}
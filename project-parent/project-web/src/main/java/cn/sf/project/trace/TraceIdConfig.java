package cn.sf.project.trace;

import cn.sf.bean.constants.LogString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class TraceIdConfig {

    @Bean
    public TraceIdFilter traceIdFilter() {
        log.info(LogString.initPre+"TraceIdFilter init");
        return new TraceIdFilter();
    }

    @Bean
    public TraceAop setupTraceAop() {
        log.info(LogString.initPre +"TraceAop init");
        return new TraceAop();
    }

//    @Bean
//    public FilterRegistrationBean traceFilterRegistration() {
//        FilterRegistrationBean registration = new FilterRegistrationBean(new TraceIdFilter());
//        registration.addUrlPatterns("/*");
//        return registration;
//    }

}
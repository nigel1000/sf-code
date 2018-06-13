package cn.sf.auto.aop.config;

import cn.sf.auto.aop.excp.AutoExcpAop;
import cn.sf.auto.aop.print.AutoLogAop;
import cn.sf.bean.constants.LogString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class AopAutoConfig {

    @Bean
    public AutoLogAop setupAutoLogAop() {
        log.info(LogString.initPre +"AutoLogAop init");
        return new AutoLogAop();
    }

    @Bean
    public AutoExcpAop setupAutoExcpAop() {
        log.info(LogString.initPre +"AutoExcpAop init");
        return new AutoExcpAop();
    }

}

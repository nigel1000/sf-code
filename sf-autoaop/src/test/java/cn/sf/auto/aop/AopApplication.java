package cn.sf.auto.aop;

import cn.sf.auto.aop.config.EnableExcpReturn;
import cn.sf.bean.beans.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;

@SpringBootApplication
@EnableExcpReturn(returnTypes = {Response.class}, methodNames = {"fail"})
@Slf4j
public class AopApplication implements EmbeddedServletContainerCustomizer {
    public static void main(String args[]) {
        SpringApplication.run(AopApplication.class, args);
    }

    //属性配置
    public void customize(ConfigurableEmbeddedServletContainer container) {
         container.setPort(8888);
    }

}

package cn.sf.alibaba.oss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;

@SpringBootApplication
public class OssApplication implements EmbeddedServletContainerCustomizer {

    public static void main(String args[]) {
        SpringApplication.run(OssApplication.class, args);
    }

    //属性配置
    public void customize(ConfigurableEmbeddedServletContainer container) {
         container.setPort(8880);
    }

}

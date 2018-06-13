package cn.sf.redis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;

@SpringBootApplication
public class RedisApplication implements EmbeddedServletContainerCustomizer {
    public static void main(String args[]) {
        SpringApplication.run(RedisApplication.class, args);
    }

    //属性配置
    public void customize(ConfigurableEmbeddedServletContainer container) {
         container.setPort(8889);
    }

}

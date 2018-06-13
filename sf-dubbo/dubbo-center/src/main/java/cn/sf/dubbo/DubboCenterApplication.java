package cn.sf.dubbo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
@ImportResource(value = {
        "classpath:dubbo/dubbo-provider.xml",
})
@Profile("default")
public class DubboCenterApplication {
    public static void main(String[] args) {
        SpringApplication.run(DubboCenterApplication.class, args);
    }
}
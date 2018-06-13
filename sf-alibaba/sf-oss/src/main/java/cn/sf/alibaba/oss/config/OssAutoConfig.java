package cn.sf.alibaba.oss.config;

import cn.sf.alibaba.oss.client.OssObjectUtil;
import cn.sf.alibaba.oss.client.OssUrlUtil;
import cn.sf.bean.constants.LogString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
//为了加载OssProperties的@Component
@ComponentScan(basePackages = { "cn.sf.alibaba.oss"})
@EnableConfigurationProperties
@Slf4j
public class OssAutoConfig{

    @Bean
    public OssObjectUtil ossObjectUtil() {
        log.info(LogString.initPre +"ossObjectUtil init");
        return new OssObjectUtil();
    }

    @Bean
    public OssUrlUtil ossUrlUtil() {
        log.info(LogString.initPre +"ossUrlUtil init");
        return new OssUrlUtil();
    }

}

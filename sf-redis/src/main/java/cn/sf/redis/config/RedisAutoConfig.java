package cn.sf.redis.config;

import cn.sf.bean.constants.LogString;
import cn.sf.redis.aops.RedisAop;
import cn.sf.redis.client.JedisClientUtil;
import cn.sf.redis.client.JedisManagerUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by nijianfeng on 17/7/3.
 */
@Configuration
//为了加载OssProperties的@Component
@ComponentScan(basePackages = { "cn.sf.redis"})
@EnableConfigurationProperties
@Slf4j
public class RedisAutoConfig {

    @Bean
    public RedisAop setupRedisAop() {
        log.info(LogString.initPre +"RedisAop init");
        return new RedisAop();
    }

    @Bean
    public JedisClientUtil jedisClientUtil() {
        log.info(LogString.initPre +"JedisClientUtil init");
        return new JedisClientUtil();
    }

    @Bean
    public JedisManagerUtil jedisManagerUtil() {
        log.info(LogString.initPre +"JedisManagerUtil init");
        return new JedisManagerUtil();
    }

}

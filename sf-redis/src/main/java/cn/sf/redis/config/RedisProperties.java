package cn.sf.redis.config;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by nijianfeng on 17/7/4.
 */
@Data
@Component
@ToString(callSuper = true)
@ConfigurationProperties(
        prefix = "multiRedis",
        ignoreInvalidFields = true
)
public class RedisProperties {
    //连接池的配置
    //最大连接数
    private int maxTotal = 8;
    //最大空闲连接数
    private int maxIdle = 8;
    //最小空闲连接数
    private int minIdle = 0;
    private boolean lifo = true;
    private boolean fairness = false;

    //连接的最后空闲时间，达到此值后空闲连接被移除
    private long minEvictableIdleTimeMillis = 60000L;
    private long softMinEvictableIdleTimeMillis = 1800000L;
    //做空闲连接检测时，每次的采样数
    private int numTestsPerEvictionRun = -1;
    private String evictionPolicyClassName = "org.apache.commons.pool2.impl.DefaultEvictionPolicy";
    private boolean testOnCreate = false;
    //借用连接时是否做连接有效性检测 ping
    private boolean testOnBorrow = false;
    //归还时是否做连接有效性检测 ping
    private boolean testOnReturn = false;
    //借用连接时是否做空闲检测
    private boolean testWhileIdle = true;
    //空闲连接的检测周期
    private long timeBetweenEvictionRunsMillis = 30000L;
    //连接池用尽后，调用者是否等待
    private boolean blockWhenExhausted = true;
    //连接池没有连接后客户端的最大等待时间  －1表示不超时  一直等
    private long maxWaitMillis = -1L;
    //开启jmx功能
    private boolean jmxEnabled = true;
    private String jmxNamePrefix = "pool";
    private String jmxNameBase;

    //主从模式
    private RedisPoolProperty singlePool;
    //哨兵模式
    private RedisSentinelPoolProperty sentinelPool;
    //集群模式
    private RedisClusterProperty cluster;


    @ToString
    @Data
    public static class RedisPoolProperty {

        private String hostName = "localhost";
        private int port = 6379;
        private int timeout = 30000;
        private String password;

    }

    @ToString
    @Data
    public static class RedisSentinelPoolProperty {

        //主节点名
        private String masterName;
        //哨兵节点集合
        private List<String> sentinels;
        //连接超时
        private int connectTimeout = 30000;
        //读写超时
        private int soTimeout = 30000;
        private String password;
        //当前数据库索引
        private int database = 0;
        //客户端名
        private String clientName;
    }

    @ToString
    @Data
    public static class RedisClusterProperty {

        //部分或全部cluster节点信息
        private List<String> nodes;
        //连接超时
        private int connectTimeout = 30000;
        //读写超时
        private int soTimeout = 30000;
        //重试次数
        private int maxAttempts = 3;
    }
}

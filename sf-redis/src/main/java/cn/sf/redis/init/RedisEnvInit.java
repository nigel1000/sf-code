package cn.sf.redis.init;

import cn.sf.bean.constants.LogString;
import cn.sf.bean.utils.BeanCopyUtil;
import cn.sf.redis.config.RedisProperties;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import redis.clients.jedis.*;
import redis.clients.util.Pool;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by nijianfeng on 17/6/26.
 */
@Component
@Slf4j
public class RedisEnvInit implements InitializingBean {

    // private static JedisPool jedisPool;
    // private static JedisSentinelPool jedisSentinelPool;
    private static JedisCluster cluster;

    private static Pool<Jedis> pool;


    @Resource
    protected RedisProperties redisProperties;

    @Override
    public void afterPropertiesSet() throws Exception {

        if (this.getClass() == RedisEnvInit.class) {
            log.info(LogString.initPre + "RedisEnvInit init jedisPool&jedisSentinelPool:" + redisProperties.toString());
            RedisProperties.RedisPoolProperty poolProperty = redisProperties.getSinglePool();
            if (poolProperty != null) {
                JedisPoolConfig poolConfig = BeanCopyUtil.genBean(redisProperties, JedisPoolConfig.class);
                // 根据配置实例化jedis池
                pool = new JedisPool(poolConfig, poolProperty.getHostName(), poolProperty.getPort(),
                        poolProperty.getTimeout(), poolProperty.getPassword());
            }

            RedisProperties.RedisSentinelPoolProperty sentinelPoolProperty = redisProperties.getSentinelPool();
            if (sentinelPoolProperty != null) {
                GenericObjectPoolConfig genericObjectPoolConfig =
                        BeanCopyUtil.genBean(redisProperties, GenericObjectPoolConfig.class);
                pool = new JedisSentinelPool(sentinelPoolProperty.getMasterName(),
                        Sets.newHashSet(sentinelPoolProperty.getSentinels()), genericObjectPoolConfig,
                        sentinelPoolProperty.getConnectTimeout(), sentinelPoolProperty.getSoTimeout(),
                        sentinelPoolProperty.getPassword(), sentinelPoolProperty.getDatabase(),
                        sentinelPoolProperty.getClientName());
            }
            RedisProperties.RedisClusterProperty clusterProperty = redisProperties.getCluster();
            if (clusterProperty != null) {
                Set<HostAndPort> clusterNodes = Sets.newHashSet();
                for (String node : clusterProperty.getNodes()) {
                    clusterNodes.add(HostAndPort.parseString(node));
                }
                GenericObjectPoolConfig genericObjectPoolConfig =
                        BeanCopyUtil.genBean(redisProperties, GenericObjectPoolConfig.class);
                cluster = new JedisCluster(clusterNodes, clusterProperty.getConnectTimeout(),
                        clusterProperty.getSoTimeout(), clusterProperty.getMaxAttempts(), genericObjectPoolConfig);
            }

            // 关闭 oss 客户端
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (pool != null) {
                    pool.close();
                    log.info(LogString.initPre + "关闭 " + pool.getClass().getName() + " ---> 成功!");
                }
                if(cluster!=null){
                    try {
                        cluster.close();
                        log.info(LogString.initPre + "关闭 " + cluster.getClass().getName() + " ---> 成功!");
                    } catch (IOException e) {
                        log.info(LogString.initPre + "关闭 " + cluster.getClass().getName() + " ---> 失败!",e);
                    }
                }
            }));
        }
    }

    protected Jedis getConnection() {
        return pool.getResource();
    }

    protected void releaseConnection(Jedis jedis) {
        if (jedis == null) {
            return;
        }
        jedis.close();
    }

    //获取集群中所有节点的Jedis  true获取所有主节点  false获取所有节点
    protected List<Jedis> getNodesJedis(boolean isMaster){
        if(cluster==null){
            throw new IllegalArgumentException("只有集群部署下能用此函数!!");
        }

        Map<String, JedisPool> jedisPoolMap = cluster.getClusterNodes();
        List<Jedis> jedises = Lists.newArrayList();
        jedisPoolMap.forEach((key,jedisPool)->{
            Jedis jedis = jedisPool.getResource();
            if(isMaster){
                if(isMaster(jedis)){
                    jedises.add(jedis);
                }
            }else{
                jedises.add(jedis);
            }
        });
        return jedises;
    }

    private boolean isMaster(Jedis jedis){
        String[] data = jedis.info("Replication").split("\r\n");
        for(String line : data){
            if("role:master".equals(line.trim())){
                return true;
            }
        }
        return false;
    }

}

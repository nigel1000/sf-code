package cn.sf.redis.client;

import cn.sf.bean.constants.LogString;
import cn.sf.redis.init.RedisEnvInit;
import cn.sf.utils.serializes.SerializeUtil;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by nijianfeng on 17/7/4.
 */
@Slf4j
public final class JedisManagerUtil extends RedisEnvInit{


    // hash key下的所有field
    public Set<String> hKeys(String key) {
        Jedis jedis = null;
        try {
            jedis = this.getConnection();
            Set<byte[]> bFields = jedis.hkeys(key.getBytes());
            return bFields.stream()
                    .map(String::new)
                    .collect(Collectors.toSet());
        }catch (Exception ex){
            log.error(LogString.redisPre+"hKeys failed key="+key+"\r\nredis exception:",ex);
        } finally {
            this.releaseConnection(jedis);
        }
        return Sets.newHashSet();
    }
    // hash key下的所有field和value
    public Map<String,Object> hGetAll(String key) {
        Jedis jedis = null;
        try {
            jedis = this.getConnection();
            Map<byte[], byte[]> maps = jedis.hgetAll(key.getBytes());
            Map<String,Object> ret = Maps.newHashMap();
            maps.forEach((field,object)->{
                ret.put(new String(field), SerializeUtil.javaDeserialize(object));
            });
            return ret;
        }catch (Exception ex){
            log.error(LogString.redisPre+"hGetAll failed key="+key+"\r\nredis exception:",ex);
        } finally {
            this.releaseConnection(jedis);
        }
        return Maps.newHashMap();
    }
    // 获取key pattern下所有的key
    public Set<String> keys(String key) {
        Jedis jedis = null;
        try {
            jedis = this.getConnection();
            //用scan优化  Set<byte[]> keys = jedis.keys(key.getBytes());
            //每次扫描10个key
            ScanParams params = new ScanParams().count(10).match(key.getBytes());
            Set<String> keys = Sets.newHashSet();
            String cursor = "0";
            while (true) {
                ScanResult<String> scanResult = jedis.scan(cursor, params);
                keys.addAll(scanResult.getResult());
                //游标变为0表示扫描结束  每次返回表示当前游标的值
                cursor = scanResult.getStringCursor();
                if("0".equals(cursor)){
                    break;
                }
            }
            return keys;
        }catch (Exception ex){
            log.error(LogString.redisPre+"keys failed key="+key+"\r\nredis exception:",ex);
        } finally {
            this.releaseConnection(jedis);
        }
        return Sets.newHashSet();
    }
     // 得到剩余缓存时间
    public long ttl(String key) {
        Jedis jedis = null;
        try {
            jedis = this.getConnection();
            return jedis.ttl(key.getBytes());
        }catch (Exception ex){
            log.error(LogString.redisPre+"ttl failed key="+key+"\r\nredis exception:",ex);
        } finally {
            this.releaseConnection(jedis);
        }
        return -100L;
    }


}

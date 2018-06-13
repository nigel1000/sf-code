package cn.sf.redis.client;

import cn.sf.bean.constants.LogString;
import cn.sf.bean.excps.KnowException;
import cn.sf.redis.init.RedisEnvInit;
import cn.sf.utils.serializes.SerializeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.io.Serializable;
import java.util.Set;


@Slf4j
public final class JedisClientUtil extends RedisEnvInit {

    //for all
    public Long expire(String key, int expireSecond) {
        Jedis jedis = null;
        Long count = -1L;
        try {
            jedis = this.getConnection();
            if(expireSecond>0) {
                count = jedis.expire(key.getBytes(), expireSecond);
            }
        } catch (Exception ex) {
            log.error(LogString.redisPre + "expire failed key=" + key + "\r\nredis exception:", ex);
        } finally {
            this.releaseConnection(jedis);
        }
        return count;
    }


    // key-object string
    public String setex(String key, Object obj, int expireSecond) {
        if (obj == null) {
            throw new IllegalArgumentException("redis key hash obj  不能为空!!");
        }
        if (!(obj instanceof Serializable)) {
            throw new KnowException("存储的对象" + obj.getClass().getName() + "没有进行序列化");
        }
        Jedis jedis = null;
        String ret = null;
        try {
            jedis = this.getConnection();
            if(expireSecond>0) {
                ret = jedis.setex(key.getBytes(), expireSecond, SerializeUtil.javaSerialize(obj));
            }else{
                ret = jedis.set(key.getBytes(),SerializeUtil.javaSerialize(obj));
            }
        } catch (Exception ex) {
            log.error(LogString.redisPre + "setex failed key=" + key + "\r\nredis exception:", ex);
        } finally {
            this.releaseConnection(jedis);
        }
        return ret;
    }

    public Object get(String key) {
        Jedis jedis = null;
        Object val = null;
        try {
            jedis = this.getConnection();
            byte[] buff = jedis.get(key.getBytes());
            if (buff != null) {
                val = SerializeUtil.javaDeserialize(buff);
            }
        } catch (Exception ex) {
            log.error(LogString.redisPre + "get failed key=" + key + "\r\nredis exception:", ex);
        } finally {
            this.releaseConnection(jedis);
        }
        return val;
    }

    public Long del(String key) {
        Jedis jedis = null;
        Long count = -1L;
        try {
            jedis = this.getConnection();
            if (jedis.get(key.getBytes()) != null) {
                count = jedis.del(key.getBytes());
                log.info(LogString.redisPre + "del jedis:key=" + key);
            }
        } catch (Exception ex) {
            log.error(LogString.redisPre + "del failed key=" + key + "\r\nredis exception:", ex);
        } finally {
            this.releaseConnection(jedis);
        }
        return count;
    }

    // key-hash(field,object) hash
    public Long hSet(String key, String field, Object obj, int expireSecond) {
        if (obj == null) {
            throw new IllegalArgumentException("redis key hash obj  不能为空!!");
        }
        if (!(obj instanceof Serializable)) {
            throw new KnowException("存储的对象" + obj.getClass().getName() + "没有进行序列化");
        }
        Jedis jedis = null;
        Long count = -1L;
        try {
            jedis = this.getConnection();
            count = jedis.hset(key.getBytes(), field.getBytes(), SerializeUtil.javaSerialize(obj));
            if(expireSecond>0) {
                jedis.expire(key.getBytes(), expireSecond);
            }
        } catch (Exception ex) {
            log.error(LogString.redisPre + "hSet failed key=" + key + ":field=" + field + "\r\nredis exception:", ex);
        } finally {
            this.releaseConnection(jedis);
        }
        return count;
    }

    public Object hGet(String key, String field) {
        Jedis jedis = null;
        Object val = null;
        try {
            jedis = this.getConnection();
            byte[] buff = jedis.hget(key.getBytes(), field.getBytes());
            if (buff != null) {
                val = SerializeUtil.javaDeserialize(buff);
            }
        } catch (Exception ex) {
            log.error(LogString.redisPre + "hGet failed key=" + key + ":field=" + field + "\r\nredis exception:", ex);
        } finally {
            this.releaseConnection(jedis);
        }

        return val;
    }

    public Long hDel(String key, String field) {
        Jedis jedis = null;
        Long count = -1L;
        try {
            jedis = this.getConnection();
            // 不传就是key的值都删光
            if (StringUtils.isBlank(field)) {
                Set<byte[]> fields = jedis.hkeys(key.getBytes());
                int tryTimes = 0;
				Pipeline pipeline = null;
				for (byte[] fd : fields) {
                	if(tryTimes==0){
						pipeline = jedis.pipelined();
						tryTimes++;
					}
                    pipeline.hdel(key.getBytes(), fd);
                    log.info(LogString.redisPre + "hDel jedis:key=" + key + " field=" + new String(fd));
                    if(tryTimes==20){
						pipeline.sync();
						tryTimes = 0;
					}
                }
                if(tryTimes!=0){
					pipeline.sync();
				}
				jedis.del(key.getBytes());
				count = (long) fields.size();
            } else {
                count = jedis.hdel(key.getBytes(), field.getBytes());
                log.info(LogString.redisPre + "hDel jedis:key=" + key + "  field=" + field);
            }
        } catch (Exception ex) {
            log.error(LogString.redisPre + "hDel failed key=" + key + ":field=" + field + "\r\nredis exception:", ex);
        } finally {
            this.releaseConnection(jedis);
        }
        return count;
    }

}

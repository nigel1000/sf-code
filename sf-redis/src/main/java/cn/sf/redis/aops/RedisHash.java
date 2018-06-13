package cn.sf.redis.aops;

import cn.sf.redis.enums.BizRedisEnum;

import java.lang.annotation.*;

//支持key hash的缓存处理
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface RedisHash {

    BizRedisEnum group() default BizRedisEnum.NULL;
    //直接指定hash的field
    String key() default "";
    //第几个参数作为hash的field   key+json(args[index])
    int index() default -1;

    //false  key由key()和index()指定 key=""&&index不合理的以包类方法参数为key 存入指定缓存
    //true  key由key()指定 key="_"清空组下所有缓存  删除指定缓存  删除全部缓存应该是个主观动作不能使用默认策略
    boolean isEvict() default false;
}

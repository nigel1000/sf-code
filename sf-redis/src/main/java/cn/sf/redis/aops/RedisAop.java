package cn.sf.redis.aops;

import cn.sf.bean.constants.LogString;
import cn.sf.redis.client.JedisClientUtil;
import cn.sf.redis.enums.BizRedisEnum;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;

import java.lang.reflect.Method;

@Aspect
@Order(value = 0)
@Slf4j
public class RedisAop {

    @Autowired
    private JedisClientUtil jedisClientUtil;

    //@within 用于匹配所以持有指定注解类型内的方法；代理织入
    @Pointcut("@within(cn.sf.redis.aops.RedisHash) && execution(public * *(..))")
    public void AutoJedisAspectClass() {
    }

    //@annotation 用于匹配当前执行方法持有指定注解的方法；运行切入
    @Pointcut("@annotation(cn.sf.redis.aops.RedisHash)")
    public void AutoJedisAspectMethod() {
    }

    @Around("(AutoJedisAspectClass()||AutoJedisAspectMethod())")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {

        Class clazz = pjp.getTarget().getClass();
        Method iMethod = ((MethodSignature) pjp.getSignature()).getMethod();//interface或者class方法,实现类可用接口来调用
        Method method;//真实类的方法
        try {
            method = clazz.getMethod(iMethod.getName(),iMethod.getParameterTypes());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        //优先使用方法上的注解
        RedisHash redisHash = method.getAnnotation(RedisHash.class);
        if(redisHash==null){
            //类上的注解次之
            redisHash = (RedisHash) clazz.getAnnotation(RedisHash.class);
        }
        //没啥用,留着只代表谨慎,没有redisHash注解不可能进入切面
        //方法或类上都没有redisHash注解,不进行缓存处理
        if (null == redisHash) {
            return pjp.proceed();
        }
        //缓存日志前缀
        StringBuilder unique =new StringBuilder();
        unique.append(pjp.getSignature().getDeclaringTypeName()+"#")
                .append(pjp.getSignature().getName()+"_");
        for(Object obj : pjp.getArgs()){
            unique.append(obj.getClass().getName()+":");
        }
        unique.append("------>");
        BizRedisEnum group = redisHash.group();
        if(group.equals(BizRedisEnum.NULL)){
            return pjp.proceed();
        }
        //处理每个注解的语义
        String groupName = redisHash.group().getGroupName()+"_"+
                pjp.getSignature().getDeclaringTypeName()+"_"+
                pjp.getSignature().getName()+"_";
        int expireTime = redisHash.group().getExpireTime();
        int index = redisHash.index();
        //删除缓存的处理
        boolean isEvict = redisHash.isEvict();
        String field = redisHash.key();
        if(isEvict){
            if(field.equals("_")){//删除组下全部缓存
                Long count = jedisClientUtil.hDel(groupName, null);
                if(count<=0){
                    log.info(LogString.redisPre +"已清空此组"+group.getGroupName()+"下缓存，已无缓存!");
                }
            }else if(!field.trim().equals("")) {
                Long count = jedisClientUtil.hDel(groupName, field);
                if(count<=0){
                    log.info(LogString.redisPre +"已没有此group:"+group.getGroupName()+"\tkey:"+field+"缓存!");
                }
            }
            return pjp.proceed();
        }
        //返回值的初始化
        Object var;
        //hash key的组装
        StringBuilder mapKey = new StringBuilder();
        if(index>=0&&index<pjp.getArgs().length){//有有效参数索引  key+对应参数json
            if(!StringUtils.isBlank(field)){
                mapKey.append(field+"_");
            }
            mapKey.append(JSONObject.toJSONString(pjp.getArgs()[index])+"_");
        } else if(!StringUtils.isBlank(field)){ //没有有效参数索引有key  key
            mapKey.append(field+"_");
        } else {
            //没有有效参数索引没有key  方法+参数
//            mapKey.append(pjp.getSignature().getDeclaringTypeName()+"_")
//                    .append(pjp.getSignature().getName()+"_");
            for(Object obj : pjp.getArgs()) {
                //json作为key的一部分
                mapKey.append(JSONObject.toJSONString(obj) + "_");
            }
        }

        //处理返回  查到第一个缓存返回 存入每个缓存
        var = jedisClientUtil.hGet(groupName,mapKey.toString());
        if(var!=null){
            log.info(unique+"命中缓存groupName="+ groupName +",\tkey="+mapKey+",\tvalue="+JSONObject.toJSONString(var));
        }else{
            var = pjp.proceed();
            Long count = jedisClientUtil.hSet(groupName, mapKey.toString(), var, expireTime);
            if(count>0){
                log.info(unique + "存入缓存groupName=" + groupName + ",\tkey=" + mapKey + ",\tvalue=" + JSONObject.toJSONString(var));
            }
//            if(count==0){
//                log.info(unique + "更新缓存groupName=" + groupName + ",\tkey=" + mapKey + ",\tvalue=" + JSONObject.toJSONString(var));
//            }
        }

        return var;
    }

}
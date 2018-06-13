package cn.sf.redis.test;

import cn.sf.bean.constants.LogString;
import cn.sf.redis.aops.RedisHash;
import cn.sf.redis.enums.BizRedisEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class TestService {

//    @RedisHash(group = BizRedisEnum.DEFAULT,key = "_",isEvict = true)
//    @RedisHash(group = BizRedisEnum.USER_MODULE,key = "测试",index = 0)
    @RedisHash(group = BizRedisEnum.USER_MODULE)
    public List<Integer> getList(Integer param){
        log.info(LogString.initPre +"进入真实方法");
        return Arrays.asList(1,2,3,4,5);
    }
}

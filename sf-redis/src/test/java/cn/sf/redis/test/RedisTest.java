package cn.sf.redis.test;

import cn.sf.redis.base.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by nijianfeng on 17/6/23.
 */
public class RedisTest extends BaseTest {

    @Autowired
    private TestService testService;

    @Test
    public void getList1(){
        testService.getList(10);
        testService.getList(10);
    }

}

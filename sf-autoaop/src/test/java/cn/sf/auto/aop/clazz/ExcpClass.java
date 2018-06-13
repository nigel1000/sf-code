package cn.sf.auto.aop.clazz;

import cn.sf.auto.aop.excp.AutoExcp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@AutoExcp(tag = "测试", module = "测试模块")
@Slf4j
@Component
public class ExcpClass implements IExcp{

    @AutoExcp(tag = "测试1", module = "测试模块1")
    public Object test1() {
        log.info("ExcpClass.test1");
        throw new RuntimeException();
    }

}

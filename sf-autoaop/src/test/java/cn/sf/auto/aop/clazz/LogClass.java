package cn.sf.auto.aop.clazz;

import cn.sf.auto.aop.print.AutoLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@AutoLog(tag = "测试", module = "测试模块")
@Slf4j
@Component
public class LogClass {

    public String test1(String input) {
        log.info("执行方法LogClass.test1");
        return "output";
    }

}

package cn.sf.auto.aop.test.excpaop;

import cn.sf.auto.aop.base.BaseTest;
import cn.sf.auto.aop.clazz.IExcp;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ExcpTest extends BaseTest {

    @Autowired(required = false)
    private IExcp iExcp;
    @Test
    public void test1(){
        System.out.println(iExcp.test1());
    }
}
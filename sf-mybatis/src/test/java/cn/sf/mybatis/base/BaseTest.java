package cn.sf.mybatis.base;

import cn.sf.mybatis.MybatisApplication;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MybatisApplication.class)
@ActiveProfiles("default")
public class BaseTest {

}

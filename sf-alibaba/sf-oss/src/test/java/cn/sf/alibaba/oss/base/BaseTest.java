package cn.sf.alibaba.oss.base;

import cn.sf.alibaba.oss.OssApplication;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = OssApplication.class)
@ActiveProfiles("default")
public class BaseTest {

}

package cn.sf.auto.aop.config;

import cn.sf.auto.aop.constants.ReturnConfig;
import cn.sf.bean.constants.LogString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Arrays;
import java.util.Map;

/**
 * Created by nijianfeng on 17/6/23.
 */
@Slf4j
public class FetchEnableExcpReturnParam implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

        Map<String, Object> map = importingClassMetadata.getAnnotationAttributes(EnableExcpReturn.class.getName(), false);
        AnnotationAttributes enableAop = AnnotationAttributes.fromMap(map);

        ReturnConfig.returnTypes = enableAop.getClassArray("returnTypes");
        ReturnConfig.methodNames = enableAop.getStringArray("methodNames");
        log.info(LogString.initPre+" EnableAop param fetch:" +
                "returnTypes->" + Arrays.toString(ReturnConfig.returnTypes) + ";" +
                "methodNames->" + Arrays.toString(ReturnConfig.methodNames)
        );

    }
}

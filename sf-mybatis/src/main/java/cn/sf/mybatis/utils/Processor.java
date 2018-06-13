package cn.sf.mybatis.utils;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.context.properties.ConfigurationPropertiesBindingPostProcessor;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;

import java.util.UUID;

public class Processor {
    private ConfigurationPropertiesBindingPostProcessor processor = new ConfigurationPropertiesBindingPostProcessor();
    private ConfigurableListableBeanFactory factory;

    public Processor(ConfigurableApplicationContext context) {
        processor.setApplicationContext(context);

        processor.setConversionService(new DefaultConversionService());

        Environment environment = context.getEnvironment();
        if (environment instanceof StandardEnvironment) {
            processor.setPropertySources(((StandardEnvironment) environment).getPropertySources());
        }

        factory = context.getBeanFactory();
    }

    public Processor(ConfigurableApplicationContext context, StandardEnvironment environment) {
        processor.setApplicationContext(context);
        processor.setConversionService(new DefaultConversionService());
        processor.setPropertySources(environment.getPropertySources());
        factory = context == null ? null : context.getBeanFactory();
    }

    public<T> T after (String name, T singleton) {
        if (null != factory && !factory.containsBean(name))
            factory.registerSingleton(name, singleton);
        return (T) processor.postProcessAfterInitialization(singleton, name);
    }

    public<T> T before (String name, T singleton) {
        if (null != factory && !factory.containsBean(name))
            factory.registerSingleton(name, singleton);
        return (T) processor.postProcessBeforeInitialization(singleton, name);
    }

    /**
     * process specified properties and ignore bean name.
     * @param singleton to be processed {@link org.springframework.boot.context.properties.ConfigurationProperties}
     * @param <T> generify type.
     * @return processed bean.
     */
    public<T> T before (T singleton) {
        return (T) processor.postProcessBeforeInitialization(singleton, UUID.randomUUID().toString());
    }
}
package cn.sf.project.config;

import cn.sf.bean.constants.LogString;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.HibernateValidator;
import org.springframework.boot.context.embedded.MultipartConfigFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.view.BeanNameViewResolver;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.MultipartConfigElement;
import javax.validation.Validator;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

@Configuration
@EnableWebMvc
@Slf4j
public class MvcConfig extends WebMvcConfigurerAdapter {
    // 集成freemarker
    @Bean
    public ViewResolver viewResolver() {
        log.info(LogString.initPre + "viewResolver init");
        ContentNegotiatingViewResolver viewResolver = new ContentNegotiatingViewResolver();

        BeanNameViewResolver viewResolver1 = new BeanNameViewResolver();
        FreeMarkerViewResolver viewResolver2 = new FreeMarkerViewResolver();
        viewResolver2.setCache(true);
        viewResolver2.setSuffix(".ftl");
        viewResolver2.setContentType("text/html;charset=UTF-8");
        viewResolver2.setExposeRequestAttributes(true);
        viewResolver2.setExposeSessionAttributes(true);
        viewResolver2.setExposeSpringMacroHelpers(true);
        viewResolver2.setAllowSessionOverride(true);

        viewResolver.setViewResolvers(Arrays.asList(viewResolver1, viewResolver2));
        viewResolver.setDefaultViews(Collections.singletonList(new MappingJackson2JsonView()));

        return viewResolver;
    }

    @Bean(name = "freemarkerConfig")
    public FreeMarkerConfigurer freeMarkerConfigurer() {
        log.info(LogString.initPre + "freeMarkerConfigurer");
        FreeMarkerConfigurer freeMarkerConfigurer = new FreeMarkerConfigurer();
        freeMarkerConfigurer.setTemplateLoaderPath("classpath:/public/templates/");
        Properties properties = new Properties();
        properties.setProperty("template_update_delay", "0");
        properties.setProperty("default_encoding", "utf-8");
        properties.setProperty("locale", "zh_cn");
        properties.setProperty("number_format", "0.##########");
        properties.setProperty("url_escaping_charset", "error/500");
        properties.setProperty("template_exception_handler", "html_debug");
        freeMarkerConfigurer.setFreemarkerSettings(properties);
        return freeMarkerConfigurer;
    }

    // 国际化配置
    // @Bean
    // public LocaleChangeInterceptor localeChangeInterceptor(){
    // log.info(LogString.initPre +"localeChangeInterceptor init");
    // return new LocaleChangeInterceptor();
    // }
    @Bean(name = "localeResolver")
    public AcceptHeaderLocaleResolver acceptHeaderLocaleResolver() {
        log.info(LogString.initPre + "acceptHeaderLocaleResolver init");
        return new AcceptHeaderLocaleResolver();
    }

    @Bean
    public MessageSource messageSource() {
        log.info(LogString.initPre + "messageSource init");
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setCacheSeconds(60); // reload messages every 10 seconds
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setUseCodeAsDefaultMessage(false);
        return messageSource;
    }

    // 数据校验配置
    @Bean
    public Validator localValidatorFactoryBean() {
        log.info(LogString.initPre + "localValidatorFactoryBean init");
        // 既实现了Spring的Validator接口(硬编码完成数据校验)也实现了JSR 303的Validator接口
        LocalValidatorFactoryBean validatorFactoryBean = new LocalValidatorFactoryBean();
        validatorFactoryBean.setProviderClass(HibernateValidator.class);
        validatorFactoryBean.setValidationMessageSource(messageSource());
        return validatorFactoryBean;
    }

    // 覆盖StringHttpMessageConverter的默认字符
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
        if (!messageConverters.isEmpty()) {
            int count = 0;
            for (HttpMessageConverter converter : messageConverters) {
                if (converter instanceof StringHttpMessageConverter) {
                    StringHttpMessageConverter stringConverter =
                            new StringHttpMessageConverter(Charset.forName("utf-8"));
                    stringConverter.setWriteAcceptCharset(false);
                    stringConverter.setSupportedMediaTypes(
                            Arrays.asList(MediaType.APPLICATION_JSON, MediaType.TEXT_HTML, MediaType.TEXT_PLAIN));
                    messageConverters.remove(count);
                    messageConverters.add(count, stringConverter);
                    break;
                }
                count++;
            }
            count = 0;
            for (HttpMessageConverter converter : messageConverters) {
                if (converter instanceof MappingJackson2HttpMessageConverter) {
                    MappingJackson2HttpMessageConverter jackson2HttpMessageConverter =
                            new MappingJackson2HttpMessageConverter();
                    ObjectMapper objectMapper = jackson2HttpMessageConverter.getObjectMapper();
                    // 不显示为null的字段 json序列化long转字符串
                    objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
                    SimpleModule simpleModule = new SimpleModule();
                    simpleModule.addSerializer(Integer.class, ToStringSerializer.instance);
                    simpleModule.addSerializer(Integer.TYPE, ToStringSerializer.instance);
                    simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
                    simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
                    objectMapper.registerModule(simpleModule);
                    jackson2HttpMessageConverter.setObjectMapper(objectMapper);
                    messageConverters.remove(count);
                    messageConverters.add(count, jackson2HttpMessageConverter);
                    break;
                }
                count++;
            }
        }
    }

//        <dependency>
//            <groupId>com.alibaba</groupId>
//            <artifactId>fastjson</artifactId>
//            <version>1.2.6</version>
//        </dependency>
//    @Override
//    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
//        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
//        FastJsonConfig fastJsonConfig = new FastJsonConfig();
//        SerializeConfig serializeConfig = SerializeConfig.globalInstance;
//        serializeConfig.put(BigInteger.class, ToStringSerializer.instance);
//        serializeConfig.put(Long.class, ToStringSerializer.instance);
//        serializeConfig.put(Long.TYPE, ToStringSerializer.instance);
//        fastJsonConfig.setSerializeConfig(serializeConfig);
//        fastConverter.setFastJsonConfig(fastJsonConfig);
//        converters.add(fastConverter);
//    }

    // 上传文件支持
    @Bean(name = "multipartResolver")
    public StandardServletMultipartResolver standardServletMultipartResolver() {
        log.info(LogString.initPre + "standardServletMultipartResolver");
        return new StandardServletMultipartResolver();
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(1024000000);
        factory.setMaxRequestSize(2048000000);
        return factory.createMultipartConfig();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info(LogString.initPre + "addInterceptors");
        registry.addInterceptor(new LocaleChangeInterceptor());
    }

    // WebMvcConfigurationSupport # handlerExceptionResolver 默认会加入按异常和状态码的异常机制 @ControllerAdvice
    // 复写这个方法会使默认三个异常机制不再配置
    // @Override
    // public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
    // log.info(LogString.initPre +"simpleMappingExceptionResolver");
    // SimpleMappingExceptionResolver simpleMappingExceptionResolver= new SimpleMappingExceptionResolver();
    // simpleMappingExceptionResolver.setDefaultErrorView("error/404");
    // simpleMappingExceptionResolver.setExceptionAttribute("error");
    // Properties properties = new Properties();
    // properties.setProperty("java.lang.RuntimeException", "error/500");
    // properties.setProperty("org.apache.shiro.authc.UnknownAccountException", "error/404");
    // properties.setProperty("org.apache.shiro.authc.LockedAccountException", "error/404");
    // simpleMappingExceptionResolver.setExceptionMappings(properties);
    //
    // exceptionResolvers.add(simpleMappingExceptionResolver);
    // }

    // 提前初始化dispatcherServlet的若干值
    // @Bean
    // public ServletRegistrationBean dispatcherRegistration(DispatcherServlet dispatcherServlet) {
    // ServletRegistrationBean registration = new ServletRegistrationBean(dispatcherServlet);
    // dispatcherServlet.setThrowExceptionIfNoHandlerFound(true);
    // return registration;
    // }

}

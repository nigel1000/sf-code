package cn.sf.shiro.config;

import cn.sf.bean.constants.LogString;
import cn.sf.shiro.filter.BaseSecurityFilter;
import cn.sf.shiro.realm.CustomAuthorizingRealm;
import cn.sf.shiro.session.CustomSessionDAO;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.config.Ini;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by nijianfeng on 17/7/10.
 */
@Configuration
@ComponentScan(basePackages = { "cn.sf.shiro"})
@EnableConfigurationProperties
@Slf4j
public class ShiroAutoConfig {

    @Resource
    private Ini.Section section;
    @Resource
    private ShiroProperty shiroProperty;
    @Resource
    private CustomSessionDAO customSessionDAO;
    @Resource
    private CustomAuthorizingRealm customAuthorizingRealm;

    @Bean
    public DefaultWebSessionManager webSessionManager() {
        DefaultWebSessionManager webSessionManager = new DefaultWebSessionManager();
        webSessionManager.setSessionDAO(customSessionDAO);
        log.info(LogString.initPre+"init webSessionManager ");
        return webSessionManager;
    }
    @Bean
    public SecurityManager webSecurityManager() {
        DefaultWebSecurityManager webSecurityManager = new DefaultWebSecurityManager();
        webSecurityManager.setSessionManager(webSessionManager());
        webSecurityManager.setRealm(customAuthorizingRealm);
        log.info(LogString.initPre+"init webSecurityManager ");
        return webSecurityManager;
    }

    // 类似PropertyPlaceholderConfigurer这种的Bean是需要在其他Bean初始化之前完成的，
    // 这会影响到Spring Bean生命周期的控制，所以如果你用到了这样的Bean，需要把他们声明成Static的，
    // 这样就会不需要@Configuration的实例而调用，从而提前完成Bean的构造。并且，这里还提到，
    // 如果你没有把实现 BeanFactoryPostProcessor接口的Bean声明为static的，他会给出警告。
    @Bean
    public static LifecycleBeanPostProcessor lifecycleBeanPostProcessor(){
        log.info(LogString.initPre+"init lifecycleBeanPostProcessor ");
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(){
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(webSecurityManager());
        advisor.setClassFilter(new PointCutClassFilter(shiroProperty.getShiroAnnotations()));
        log.info(LogString.initPre+"init authorizationAttributeSourceAdvisor ");
        return advisor;
    }
//    @Bean
//    @DependsOn("lifecycleBeanPostProcessor")
//    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator(){
//        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
//        advisorAutoProxyCreator.setProxyTargetClass(true);
//        log.info(LogString.initPre+"init defaultAdvisorAutoProxyCreator ");
//        return advisorAutoProxyCreator;
//    }

    //注册shiro的过滤器
    @Bean
    public FilterRegistrationBean filterRegistrationBean() throws Exception {
        try {
            FilterRegistrationBean registrationBean = new FilterRegistrationBean();
            registrationBean.setFilter((Filter) shiroSecurityFilter().getObject());
            registrationBean.setOrder(1);
            log.info(LogString.initPre+"init filterRegistrationBean ");
            return registrationBean;
        } catch (Exception e) {
            log.error("failed to register shiro security filter!");
            throw e;
        }
    }
    private ShiroFilterFactoryBean shiroSecurityFilter() {
        ShiroFilterFactoryBean shiroSecurityFilter = new ShiroFilterFactoryBean();
        shiroSecurityFilter.setLoginUrl(shiroProperty.getLoginUrl());
        shiroSecurityFilter.setSuccessUrl(shiroProperty.getSuccessUrl());
        shiroSecurityFilter.setUnauthorizedUrl(shiroProperty.getUnauthorizedUrl());
        shiroSecurityFilter.setSecurityManager(webSecurityManager());
        shiroSecurityFilter.setFilters(buildFilters());
        try {
            String urlFilters = shiroProperty.getUrlFilters();
            String[] filterArray = urlFilters.trim().split(",");
            if(filterArray.length>0) {
                for(String filter : filterArray){
                    String[] temp = filter.split("=");
                    if(temp.length==2) {
                        section.put(temp[0], temp[1]);
                    }
                }
            }
            shiroSecurityFilter.setFilterChainDefinitionMap(section);
        } catch (Throwable e) {
            log.error("failed to load application chain definition!");
        }
        return shiroSecurityFilter;
    }

    //创建自己的过滤器
    private Map<String, Filter> buildFilters() {
        Map<String, Filter> filterMap = new LinkedHashMap<>();
        Map<String, ShiroProperty.FilterProperty> filters = shiroProperty.getFilters();
        if(!CollectionUtils.isEmpty(filters)) {
            filters.forEach((key,filterProperty)->{
                try {
                    String className = filterProperty.getClassName();
                    if(!StringUtils.isEmpty(className)){
                        Class clazz = Class.forName(filterProperty.getClassName());
                        if(BaseSecurityFilter.class.isAssignableFrom(clazz)){
                            BaseSecurityFilter filter = (BaseSecurityFilter)clazz.newInstance();
                            filter.setAnonUrls(filterProperty.getAnonUrls());
                            filter.setLoginRequest(filterProperty.getLoginRequest());
                            filterMap.put(key,filter);
                        }
                    }

                } catch (ClassNotFoundException e) {
                    log.error(filterProperty.getClassName()+" is not found!",e);
                } catch (IllegalAccessException|InstantiationException e) {
                    log.error(filterProperty.getClassName()+" 没有默认构造函数!",e);
                }
            });
        }
        return filterMap;
    }

}

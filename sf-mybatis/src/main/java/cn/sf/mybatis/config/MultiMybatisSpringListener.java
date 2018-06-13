package cn.sf.mybatis.config;

import cn.sf.bean.constants.LogString;
import cn.sf.mybatis.utils.PatternResolver;
import cn.sf.mybatis.utils.Processor;
import com.alibaba.druid.pool.DruidDataSource;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.mapper.ClassPathMapperScanner;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

@Slf4j
public class MultiMybatisSpringListener implements SpringApplicationRunListener {

    private final static String star = "******";

    private final static Method methodDataSource;
    private final static Method methodSqlSessionFactory;
    private final static Method methodTransactionManager;
    private final static Method methodSqlSessionTemplate;

    private final static Method methodBuilderAddCr;
    private final static Method methodBuilderAddCv;
    private final static Method methodBuilderAddPr;
    private final static Method methodBuilderAddPv;

    private static MybatisProperties mybatises;

    private static ConfigurableApplicationContext context;
    private static ResourceLoader resourceLoader;
    private static ConfigurableListableBeanFactory factory;

    private static Set<String> mapperScanned = Sets.newHashSet();

    private final static Class cls = MultiMybatisSpringListener.class;
    private final static String className = cls.getSimpleName();
    static {
        try {
            methodDataSource = cls.getDeclaredMethod("getDataSource", MybatisProperty.class);
            methodTransactionManager = cls.getDeclaredMethod("getTransactionManager", DataSource.class);
            methodSqlSessionFactory = cls.getDeclaredMethod("getSqlSessionFactory", MybatisProperty.class, DataSource.class);
            methodSqlSessionTemplate = cls.getDeclaredMethod("getSqlSessionTemplate", MybatisProperty.class, SqlSessionFactory.class);

            Class<BeanDefinitionBuilder> clazz = BeanDefinitionBuilder.class;
            methodBuilderAddCr = clazz.getDeclaredMethod("addConstructorArgReference", String.class);
            methodBuilderAddCv = clazz.getDeclaredMethod("addConstructorArgValue", Object.class);
            methodBuilderAddPr = clazz.getDeclaredMethod("addPropertyReference", String.class, String.class);
            methodBuilderAddPv = clazz.getDeclaredMethod("addPropertyValue", String.class, Object.class);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
    enum CPAV_E {
        CR(methodBuilderAddCr),
        CV(methodBuilderAddCv),
        PR(methodBuilderAddPr),
        PV(methodBuilderAddPv);

        private Method method = null;
        CPAV_E(Method method) {
            this.method = method;
        }
        public Method value() {
            return this.method;
        }
    }
    static class Argument {
        public CPAV_E cpav;
        public Object[] arguments;
        public Argument(CPAV_E cpav, Object... arguments) {
            this.cpav = cpav;
            this.arguments = arguments;
        }
    }
    public MultiMybatisSpringListener() {}
    public MultiMybatisSpringListener(SpringApplication application, String... args) {
    }
    @Override
    public void started() {}

    @Override
    public void environmentPrepared(ConfigurableEnvironment environment) {}

    @Override
    public void contextPrepared(ConfigurableApplicationContext ctxt) {
        final Environment environment = ctxt.getEnvironment();

        // We should ignore bootstrapInProgress configuration.
        if (! (environment instanceof StandardEnvironment)) return;
        MutablePropertySources sources = ((StandardEnvironment) environment).getPropertySources();
        if (sources.contains("bootstrapInProgress")) return;
        // 初始化参数 保留上下文
        context = ctxt;
        factory = ctxt.getBeanFactory();
        resourceLoader = ctxt;
        // Use child context for properties parsing.
        Processor processor = new Processor(ctxt);
        mybatises = processor.before(MybatisProperties.class.getName(), new MybatisProperties());
        // Log content after password has been cleared.
        log.info(LogString.initPre +"MybatisProperties init: " + mybatises.toString());
        //进行数据源配置
        if(mybatises.getUrl()!=null||mybatises.mybatisMap!=null) {
            configure();
        }
    }

    @Override
    public void contextLoaded(ConfigurableApplicationContext context) {}

    @Override
    public void finished(ConfigurableApplicationContext context, Throwable exception) {}

    private void configure () {
        final String noSingleMybatisConfigurationAllowed = "Both 'mybatis.url' and 'mybatis.mybatises' are set, this should be a mistake";
        final String noProperMybatisConfigurationFound = "Nether 'mybatis.url' nor 'mybatis.mybatises' has been set, auto configuration will not work";
        if (null == mybatises.getUrl()) {
            if (null == mybatises.mybatisMap) {
                //单数据源，多数据源都为空，没有进行配置
                log.error(LogString.initPre +"{}: "+noProperMybatisConfigurationFound+".", className);
                return;
            }
            //多数据源
            prepare(mybatises.mybatisMap);
            log.info(LogString.initPre +"{}: configuring multiple mybatis.", className);
        } else {
            if (null != mybatises.mybatisMap) {
                log.error(LogString.initPre +"{}: " + noSingleMybatisConfigurationAllowed + ".", className);
                throw new IllegalArgumentException(noSingleMybatisConfigurationAllowed);
            }
            //单数据源
            prepare(mybatises);
            log.info(LogString.initPre +"{}: configuring single mybatis.", className);
        }
    }
    //配置多数据源
    private void prepare (Map<String,MybatisProperty> properties) {
        properties.forEach((app,single)->{
            log.info(LogString.initPre +"load multi datasource for "+ app);
            prepare(single);
        });
    }
    //配置数据源
    private void prepare (MybatisProperty single) {
        //检查配置文件是否存在
        if (single.isCheckConfigLocation()) {
            Resource resource = resourceLoader.getResource(single.getConfigLocation());
            Assert.state(resource.exists(),
                    "Cannot find config location: " + resource
                            + " (please add config file or check your Mybatis "
                            + "configuration)");
        }
        //DefaultListableBeanFactory注册bean
        if (factory instanceof DefaultListableBeanFactory) {
            DefaultListableBeanFactory registry = (DefaultListableBeanFactory) factory;
            //注册datasource
            if (! registry.containsBeanDefinition(single.getDataSourceName())) {
                BeanDefinition source = getDataSourceDefinition(single);
                registry.registerBeanDefinition(single.getDataSourceName(), source);
            }
            //注册TransactionManager
            if (! registry.containsBeanDefinition(single.getTransactionManagerName())) {
                BeanDefinition tx = getTransactionManagerDefinition(single.getDataSourceName());
                registry.registerBeanDefinition(single.getTransactionManagerName(), tx);
            }
            //注册SqlSessionFactory
            if (! registry.containsBeanDefinition(single.getSqlSessionFactoryName())) {
                 // If we register bean definition from SqlSessionFactoryBean, we build it twice.
                 // Which means transaction will not work.
                 // @see #getSqlSessionFactoryDefinition
                SqlSessionFactory sessionFactory = getSqlSessionFactory(single, (DataSource) registry.getBean(single.getDataSourceName()));
                registry.registerSingleton(single.getSqlSessionFactoryName(), sessionFactory);
            }
            //注册SqlSessionTemplate
            if (! factory.containsBeanDefinition(single.getSqlSessionTemplateName())) {
                BeanDefinition template = getSqlSessionTemplateDefinition(single, single.getSqlSessionFactoryName());
                registry.registerBeanDefinition(single.getSqlSessionTemplateName(), template);
            }
        } else {
            /**
             * If we register all these beans directly, some auto configuration class will crash.
             * @see org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration
             * {@link org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration} will
             * check the {@link org.springframework.transaction.PlatformTransactionManager} beans existed.
             * If there are multiple instances, {@link org.springframework.boot.autoconfigure.condition.OnBeanCondition}
             * will check the first {@link BeanDefinition} with type {@link org.springframework.transaction.PlatformTransactionManager}.
             * However, if we only registed singletons but no {@link BeanDefinition}, the check fail.
             */
            DataSource source = reference(single.getDataSourceRef(), methodDataSource, this, single);
            DataSourceTransactionManager manager = reference(single.getTransactionManagerRef(), methodTransactionManager, this, source);
            SqlSessionFactory sessionFactory = reference(single.getSqlSessionFactoryRef(), methodSqlSessionFactory, this, single, source);
            SqlSessionTemplate template = reference(single.getSqlSessionTemplateRef(), methodSqlSessionTemplate, this, single, sessionFactory);
            factory.registerSingleton(single.getDataSourceName(), source);
            factory.registerSingleton(single.getTransactionManagerName(), manager);
            factory.registerSingleton(single.getSqlSessionFactoryName(), sessionFactory);
            factory.registerSingleton(single.getSqlSessionTemplateName(), template);
        }
        //执行mybatis的ClassPathMapperScanner
        if (context instanceof AnnotationConfigApplicationContext) {
            List<String> mapperToBeScanned = new ArrayList<>(5);
            //去重mapper   dao层接口
            for (String scan : single.getMapperScan()) {
                if (mapperScanned.contains(scan)) {
                    continue;
                }
                mapperScanned.add(scan);
                mapperToBeScanned.add(scan);
            }
            if (mapperToBeScanned.size() > 0) {
                //初始化scanner
                ClassPathMapperScanner scanner = new ClassPathMapperScanner(((AnnotationConfigApplicationContext) context).getDefaultListableBeanFactory());
                scanner.setResourceLoader(resourceLoader);
                scanner.registerFilters();
                scanner.setSqlSessionFactoryBeanName(single.getSqlSessionFactoryName());
                scanner.setSqlSessionTemplateBeanName(single.getSqlSessionTemplateName());
                scanner.doScan(StringUtils.toStringArray(mapperToBeScanned));
            }
        }
        // clear password from memory
        single.setPassword(star);
    }

    private BeanDefinition getDataSourceDefinition (MybatisProperty properties) {
        Argument[] arguments = new Argument[] {
                new Argument(CPAV_E.PV, "url", properties.getUrl()),
                new Argument(CPAV_E.PV, "username", properties.getUsername()),
                new Argument(CPAV_E.PV, "password", properties.getPassword()),
                new Argument(CPAV_E.PV, "driverClassName", properties.getDriverClassName()),
                new Argument(CPAV_E.PV, "testOnBorrow", properties.getTestOnBorrow()),
                new Argument(CPAV_E.PV, "validationQuery", properties.getValidationQuery()),
                new Argument(CPAV_E.PV, "validationQueryTimeout", properties.getValidationQueryTimeout()),
                //DruidDataSource特有的属性
                new Argument(CPAV_E.PV, "initialSize", properties.getInitialSize()),
                new Argument(CPAV_E.PV, "maxActive", properties.getMaxActive()),
                new Argument(CPAV_E.PV, "minIdle", properties.getMinIdle()),
                new Argument(CPAV_E.PV, "maxWait", properties.getMaxWait()),
                new Argument(CPAV_E.PV, "poolPreparedStatements", properties.isPoolPreparedStatements()),
                new Argument(CPAV_E.PV, "maxPoolPreparedStatementPerConnectionSize", properties.getMaxPoolPreparedStatementPerConnectionSize())
        };
        Class clazz = DruidDataSource.class;
        if(properties.getDateSourceType()!=null&&!clazz.getName().equals(properties.getDateSourceType())){
            try {
                clazz = Class.forName(properties.getDateSourceType());
            } catch (ClassNotFoundException e) {
                log.warn(LogString.initPre +properties.getDateSourceType()+"找不到!");
            }
        }
        return getBeanDefinition(clazz, arguments);
    }
    private BeanDefinition getTransactionManagerDefinition (final String dataSource) {
        Argument[] arguments = new Argument[] {
                new Argument(CPAV_E.CR, dataSource)
        };
        return getBeanDefinition(DataSourceTransactionManager.class, arguments);
    }
    private BeanDefinition getSqlSessionTemplateDefinition (MybatisProperty properties, final String sqlSessionFactory) {
        Argument[] arguments = new Argument[] {
                new Argument(CPAV_E.CR, sqlSessionFactory),
                new Argument(CPAV_E.CV, properties.getExecutorType())
        };
        return getBeanDefinition(SqlSessionTemplate.class, arguments);
    }
    private BeanDefinition getBeanDefinition (Class clazz, Argument... arguments) {
        //生成一个BeanDefinition
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(clazz);
        if (null != arguments && arguments.length > 0) {
            for (Argument argument : arguments) {
                try {
                    invoke(argument.cpav.value(), builder, argument.arguments);
                }catch (Exception e){
                    StringBuilder sb = new StringBuilder();
                    for(Object temp : argument.arguments){
                        sb.append("  ").append(temp);
                    }
                    log.warn(LogString.initPre +clazz.getName()+"的"+argument.cpav.value().getName()+"方法不存在! 以下参数没被设置"+sb.toString());
                }
            }
        }
        return builder.getBeanDefinition();
    }
    //这个比较特殊
    private SqlSessionFactory getSqlSessionFactory(MybatisProperty mybatis, final DataSource source) {
        try {
            SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
            //设置属性
            factory.setDataSource(source);
            if (StringUtils.hasText(mybatis.getConfigLocation())) {
                factory.setConfigLocation(resourceLoader.getResource(mybatis.getConfigLocation()));
            } else {
                factory.setTypeAliasesSuperType(Serializable.class);
            }
            if (null != mybatis.getTypeAliases()) {
                factory.setTypeAliases(mybatis.getTypeAliases());
            }
            if (null != mybatis.getTypeAliasesPackage()) {
                factory.setTypeAliasesPackage(mybatis.getTypeAliasesPackage());
            }
            if (null != mybatis.getTypeHandlersPackage()) {
                factory.setTypeHandlersPackage(mybatis.getTypeHandlersPackage());
            }
            if (null != mybatis.getMapperLocations()) {
                String[] locations = mybatis.getMapperLocations();
                List<Resource> resources = PatternResolver.resolve(locations);
                factory.setMapperLocations(resources.toArray(new Resource[resources.size()]));
            }
            //主动将包下的所有类添加别名，查找指定包下的所有实现Serializable的类
            String typeAliasesPackage = mybatis.getTypeAliasesPackage();
            if (null != typeAliasesPackage){
                List<String> packageNames = Splitter.on(",").splitToList(typeAliasesPackage);
                ArrayList<Class> classes = Lists.newArrayList();
                for(String packageName : packageNames){
                    classes.addAll(resolveAlias(packageName, Serializable.class));
                }
                factory.setTypeAliases(Iterators.toArray(classes.iterator(), Class.class));
            }
            return factory.getObject();
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
    private static List<Class> resolveAlias(String packageName, Class<?> superType) {
        LinkedList<ClassLoader> classLoadersList = Lists.newLinkedList();
        classLoadersList.add(ClasspathHelper.contextClassLoader());
        classLoadersList.add(ClasspathHelper.staticClassLoader());
        Collection<URL> urls = ClasspathHelper.forClassLoader((ClassLoader[])classLoadersList.toArray(new ClassLoader[0]));
//        StringBuilder urlBuilder = new StringBuilder();
        for (Iterator iter = urls.iterator(); iter.hasNext();) {
            URL url = (URL)iter.next();
//            urlBuilder.append(url.getFile());
            //spring boot   file:/root/web-car-1.0-SNAPSHOT.jar!/lib/center-car-1.0-SNAPSHOT.jar!/
            //native file:/root/web-car-1.0-SNAPSHOT.jar!/lib/center-car-1.0-SNAPSHOT.jar
            if(!url.getFile().contains(".jar")){
                iter.remove();
            }
        }
//        log.info("mybatis resolve alias:"+urlBuilder);
        Reflections reflections = new Reflections((new ConfigurationBuilder())
            .setScanners(new SubTypesScanner(false), new ResourcesScanner())
            .setUrls(urls)
            .filterInputsBy((new FilterBuilder()).include(FilterBuilder.prefix(packageName))));
        Set classes = reflections.getSubTypesOf(superType);
        List<Class> classList = Lists.newArrayList();
        for(Object c : classes){
            Class clazz = (Class)c;
            if(!clazz.isAnonymousClass() && ! clazz.isInterface() && !clazz.isMemberClass()) {
                classList.add(clazz);
            }
        }
        return classList;
    }

    private<T> T reference (final String name, Method method, Object object, Object... parameters) {
        if (null != name && name.length() > 0) {
            //从spring的上下文中获取并返回  把bean的key从name变到single.get***Name()
            return (T) factory.getBean(name);
        }
        //利用反射生成一个bean
        return invoke(method, object, parameters);
    }
    private static<T> T invoke (Method method, Object object, Object... parameters) {
        try {
            return (T) (null != parameters ? method.invoke(object, parameters) : method.invoke(object));
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e.getMessage());
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public SqlSessionTemplate getSqlSessionTemplate(MybatisProperty mybatis, final SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory, mybatis.getExecutorType());
    }
    public DataSourceTransactionManager getTransactionManager(final DataSource source) {
        return new DataSourceTransactionManager(source);
    }
    public DataSource getDataSource(MybatisProperty mybatis) {
        if (null != mybatis.getDataSourceRef() && mybatis.getDataSourceRef().length() > 0) {
            return (DruidDataSource) factory.getBean(mybatis.getDataSourceRef());
        }
//        BasicDataSource dataSource = new BasicDataSource();
//        dataSource.setUrl(mybatis.getUrl());
//        dataSource.setUsername(mybatis.getUsername());
//        dataSource.setPassword(mybatis.getPassword());
//        dataSource.setDriverClassName(mybatis.getDriverClassName());
//        dataSource.setTestOnBorrow(mybatis.getTestOnBorrow().booleanValue());
//        dataSource.setValidationQuery(mybatis.getValidationQuery());
//        dataSource.setValidationQueryTimeout(mybatis.getValidationQueryTimeout());
        //调整为druid数据源
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(mybatis.getUrl());
        dataSource.setUsername(mybatis.getUsername());
        dataSource.setPassword(mybatis.getPassword());
        dataSource.setDriverClassName(mybatis.getDriverClassName());
        dataSource.setTestOnBorrow(mybatis.getTestOnBorrow());
        dataSource.setValidationQuery(mybatis.getValidationQuery());
        dataSource.setValidationQueryTimeout(mybatis.getValidationQueryTimeout());
        dataSource.setInitialSize(mybatis.getInitialSize());
        dataSource.setMaxActive(mybatis.getMaxActive());
        dataSource.setMinIdle(mybatis.getMinIdle());
        dataSource.setMaxWait(mybatis.getMaxWait());
        dataSource.setPoolPreparedStatements(mybatis.isPoolPreparedStatements());
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(mybatis.getMaxPoolPreparedStatementPerConnectionSize());
        return dataSource;
    }


//    private BeanDefinition getSqlSessionFactoryDefinition (MybatisProperty mybatis) {
//        DataSource source = getDataSource(mybatis);
//        DefaultSqlSessionFactory factory = (DefaultSqlSessionFactory) getSqlSessionFactory(mybatis, source);
//        Configuration configuration = factory.getConfiguration();
//
//        Argument[] arguments = new Argument[] {
//                new Argument(CPAV_E.CV, configuration)
//        };
//        return getBeanDefinition(DefaultSqlSessionFactory.class, arguments);
//    }



}

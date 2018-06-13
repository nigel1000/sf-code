package cn.sf.mybatis.config;

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.ToString;
import org.apache.ibatis.session.ExecutorType;

import java.util.List;

@ToString(exclude = {"password"})
@Data
public class MybatisProperty {

    // Config file path.
    private String configLocation;
    // Location of mybatis mapper files.
    private String[] mapperLocations;
    // Package to scan domain objects.
    private String typeAliasesPackage;
    // Classes of alias. (More preferred than {@link #typeAliasesPackage})
    private Class<?>[] typeAliases;
    // Package to scan handlers.
    private String typeHandlersPackage;
    // Check the config file exists.
    private boolean checkConfigLocation = false;
    //Execution mode.
    private ExecutorType executorType = ExecutorType.SIMPLE;
    // Configuration for Datasource.
    private String url = null;
    private String username;
    private String password;
    private String driverClassName;
    private Boolean testOnBorrow = true;
    private String validationQuery = "SELECT 1";
    private String dateSourceType = null;
    private int validationQueryTimeout = 1000;
    private int initialSize = 5;
    private int maxActive = 20;
    private int minIdle = 0;
    private int maxWait = 60000;
    private boolean poolPreparedStatements = Boolean.TRUE;
    private int maxPoolPreparedStatementPerConnectionSize = 100;
    // references for current mybatis.
    private String dataSourceRef = null;
    private String transactionManagerRef = null;
    private String sqlSessionFactoryRef = null;
    private String sqlSessionTemplateRef = null;
    // names for current mybatis.
    private String dataSourceName = "dataSource";
    private String transactionManagerName = "transactionManager";
    private String sqlSessionFactoryName = "sqlSessionFactory";
    private String sqlSessionTemplateName = "sqlSessionTemplate";
    // mapper scan base packages.
    private List<String> mapperScan = Lists.newArrayList();

}

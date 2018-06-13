package cn.sf.mybatis.plugins;

import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.defaults.DefaultSqlSession;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.sql.Statement;
import java.util.*;

/**
 * Sql执行时间记录拦截器
 */
// MyBatis插件机制非常有用，用得好可以解决很多问题，不只是这里的打印SQL语句以及记录SQL语句执行时间，分页、分表都可以通过插件来实现。
//   Executor（update、query、flushStatements、commint、rollback、getTransaction、close、isClosed）
//   ParameterHandler（getParameterObject、setParameters）
//   ResultSetHandler（handleResultSets、handleOutputParameters）
//   StatementHandler（prepare、parameterize、batch、update、query）
// 只有理解这四个接口及相关方法是干什么的，才能写出好的拦截器，开发出符合预期的功能。

// 有四个接口可以拦截，为什么使用StatementHandler去拦截？
// 根据名字来看ParameterHandler和ResultSetHandler，前者处理参数，后者处理结果是不可能使用的，
// 剩下的就是Executor和StatementHandler了。
// 拦截StatementHandler的原因是而不是用Executor的原因是：
//   Executor的update与query方法可能用到MyBatis的一二级缓存从而导致统计的并不是真正的SQL执行时间
//   StatementHandler的update与query方法无论如何都会统计到PreparedStatement的execute方法执行时间，尽管也有一定误差（误差主要来自将处理结果的时间也算上），但是相差不大
@Intercepts({
        @Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class}),
        @Signature(type = StatementHandler.class, method = "update", args = {Statement.class}),
        @Signature(type = StatementHandler.class, method = "batch", args = { Statement.class })
})
@Slf4j
public class SqlCostInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object target = invocation.getTarget();

        long startTime = System.currentTimeMillis();
        StatementHandler statementHandler = (StatementHandler)target;
        try {
            return invocation.proceed();
        } finally {
            long sqlCost = System.currentTimeMillis() - startTime;

            BoundSql boundSql = statementHandler.getBoundSql();
            //sql
            String sql = boundSql.getSql();
            //参数名
            Object parameterObject = boundSql.getParameterObject();
            //参数值
            List<ParameterMapping> parameterMappingList = boundSql.getParameterMappings();

            // 格式化Sql语句，去除换行符，替换参数
            sql = formatSql(sql, parameterObject, parameterMappingList);
            if(!StringUtils.isEmpty(sql)) {
                log.info("SQL：[" + sql + "]执行耗时[" + sqlCost + "ms]");
            }
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }
    // setProperties方法，可以将一些配置属性配置在<plugin></plugin>的子标签<property />中，
    // 所有的配置属性会在形参Properties中，setProperties方法可以拿到配置的属性进行需要的处理。
    @Override
    public void setProperties(Properties properties) {

    }

    private String formatSql(String sql, Object parameterObject, List<ParameterMapping> parameterMappingList) {
        // 输入sql字符串空判断
        if(StringUtils.isEmpty(sql)){
            return "";
        }

        // 美化sql
        sql = beautifySql(sql);

        // 不传参数的场景，直接把Sql美化一下返回出去
        if (parameterObject == null || parameterMappingList == null || parameterMappingList.size() == 0) {
            return sql;
        }

        // 定义一个没有替换过占位符的sql，用于出异常时返回
        String sqlWithoutReplacePlaceholder = sql;

        try {
            Class<?> parameterObjectClass = parameterObject.getClass();

            // 如果参数是StrictMap且Value类型为Collection，获取key="list"的属性，
            // 这里主要是为了处理<foreach>循环时传入List这种参数的占位符替换
            // 例如select * from xxx where id in <foreach collection="list">...</foreach>
            if (DefaultSqlSession.StrictMap.class.isAssignableFrom(parameterObjectClass)) {
                DefaultSqlSession.StrictMap<Collection<?>> strictMap = (DefaultSqlSession.StrictMap<Collection<?>>)parameterObject;
                if (isList(strictMap.get("list").getClass())) {
                    sql = handleListParameter(sql, strictMap.get("list"),parameterMappingList);
                }
            } else if (isMap(parameterObjectClass)) {
                // 如果参数是Map则直接强转，通过map.get(key)方法获取真正的属性值
                // 这里主要是为了处理<insert>、<delete>、<update>、<select>时传入parameterType为map的场景
                Map<?, ?> paramMap = (Map<?, ?>) parameterObject;
                sql = handleMapParameter(sql, paramMap, parameterMappingList);
            } else {
                // 通用场景，比如传的是一个自定义的对象或者八种基本数据类型之一或者String
                sql = handleCommonParameter(sql, parameterMappingList, parameterObject);
            }
        } catch (Exception e) {
            log.warn(Throwables.getStackTraceAsString(e));
            // 占位符替换过程中出现异常，则返回没有替换过占位符但是格式美化过的sql，这样至少保证sql语句比BoundSql中的sql更好看
            return sqlWithoutReplacePlaceholder;
        }

        return sql;
    }

    /**
     * 美化Sql
     */
    private String beautifySql(String sql) {
        sql = sql.replaceAll("\\s+\n\\s+", " ")
                .replaceAll("\\s+\t\\s+", " ")
                .replaceAll("\\s+\\(\\s+", " ( ")
                .replaceAll("\\s+\\)\\s+", " ) ")
                .replaceAll("\\s+\\?\\s+", " ? ")
                .replaceAll("\\s+,\\s+", ",")
                .replaceAll("\\s{2,}"," ");
        return sql;
    }

    /**
     * 处理参数为List的场景
     */
    private String handleListParameter(String sql, Collection<?> col, List<ParameterMapping> parameterMappingList) {
        if(StringUtils.isEmpty(sql)){
            return sql;
        }
        Integer addCount = 1;
        Integer everyCount = parameterMappingList.size()/col.size();
        if (col != null && col.size() != 0) {
            for (Object obj : col) {
                String value;
                Class<?> objClass = obj.getClass();
                // 只处理基本数据类型、基本数据类型的包装类、String这三种
                // 如果是复合类型也是可以的，不过复杂点且这种场景较少，写代码的时候要判断一下要拿到的是复合类型中的哪个属性
                if (isPrimitiveOrPrimitiveWrapper(objClass)) {
                    value = obj.toString();
                    sql = sql.replaceFirst("\\?", value);
                } else if (objClass.isAssignableFrom(String.class)) {
                    value = "\"" + obj.toString() + "\"";
                    sql = sql.replaceFirst("\\?", value);
                } else {
                    for(int i=addCount;i<=parameterMappingList.size();i++){
                        String propertyName = parameterMappingList.get(i-1).getProperty();
                        value = getPropertyValue(obj,propertyName.substring(propertyName.indexOf(".")+1),parameterMappingList.get(i-1));
                        sql = sql.replaceFirst("\\?", value);
                        if(i%everyCount==0){
                            addCount = addCount+everyCount;
                            break;
                        }
                    }
                }

            }
        }

        return sql;
    }

    /**
     * 处理参数为Map的场景
     */
    private String handleMapParameter(String sql, Map<?, ?> paramMap, List<ParameterMapping> parameterMappingList) {
        if(StringUtils.isEmpty(sql)){
            return sql;
        }
        for (ParameterMapping parameterMapping : parameterMappingList) {
            Object propertyName = parameterMapping.getProperty();
            Object propertyValue = paramMap.get(propertyName);
            if (propertyValue != null) {
                if (propertyValue.getClass().isAssignableFrom(String.class)) {
                    propertyValue = "\"" + propertyValue + "\"";
                }

                sql = sql.replaceFirst("\\?", propertyValue.toString());
            }
        }

        return sql;
    }

    /**
     * 处理通用的场景
     */
    private String handleCommonParameter(String sql,
                                         List<ParameterMapping> parameterMappingList,
                                         Object parameterObject) {
        if(StringUtils.isEmpty(sql)){
            return sql;
        }
        for (ParameterMapping parameterMapping : parameterMappingList) {
            String propertyValue = getPropertyValue(
                    parameterObject,
                    parameterMapping.getProperty(),
                    parameterMapping);

            sql = sql.replaceFirst("\\?", propertyValue);
        }

        return sql;
    }
    // propertyName支持.集联
    private String getPropertyValue(Object paramObject, String propertyName,ParameterMapping paramMapping ){

        // 基本数据类型或者基本数据类型的包装类，直接toString即可获取其真正的参数值，
        if (isPrimitiveOrPrimitiveWrapper(paramObject.getClass())) {
            return paramObject.toString();
        }

        //取paramterMapping中的property属性即可
        String propertyValue = "\\?";
        //临时变量
        Field tempField;
        Object tempValue;
        Object tempObject = paramObject;
        Class tempObjectClazz = tempObject.getClass();
        String[] propertyNames = propertyName.trim().split("\\.");
        //map第一个字符串表示的是key
        if(propertyNames.length>1) {
            // if(parameterObjectClass.isAssignableFrom(MapperMethod.ParamMap.class))
            if (MapperMethod.ParamMap.class.isAssignableFrom(tempObjectClazz)) {
                //获取map中第一个属性值对应的对象  project.name的 project 对象
                tempObject = ((HashMap) tempObject).get(propertyNames[0]);
                propertyNames = propertyName.trim().replaceFirst(propertyNames[0]+".","").split("\\.");
            }
        }
        //非map第一个字段就是其属性值
        for(String tempName : propertyNames){
            try {
                if(HashMap.class.isAssignableFrom(tempObjectClazz)) {
                    tempObject = ((HashMap) tempObject).get(tempName);
                    propertyValue = String.valueOf(tempObject);
                }else {
                    tempField = tempObjectClazz.getDeclaredField(tempName);
                    // 要获取Field中的属性值，这里必须将私有属性的accessible设置为true
                    tempField.setAccessible(true);
                    tempValue = tempField.get(tempObject);
                    propertyValue = String.valueOf(tempValue);
                    tempObject = tempValue;
                    if(tempObject!=null) {
                        tempObjectClazz = tempObject.getClass();
                    }
                }
            }catch (Exception ex){
                log.warn(tempObjectClazz.getName()+"类的"+tempName+"不存在");
            }
        }

        if (paramMapping.getJavaType().isAssignableFrom(String.class)) {
            propertyValue = "\"" + propertyValue + "\"";
        }

        return propertyValue;
    }

    /**
     * 是否基本数据类型或者基本数据类型的包装类
     */
    private boolean isPrimitiveOrPrimitiveWrapper(Class<?> parameterObjectClass) {
        return parameterObjectClass.isPrimitive() ||
                (parameterObjectClass.isAssignableFrom(Byte.class) || parameterObjectClass.isAssignableFrom(Short.class) ||
                        parameterObjectClass.isAssignableFrom(Integer.class) || parameterObjectClass.isAssignableFrom(Long.class) ||
                        parameterObjectClass.isAssignableFrom(Double.class) || parameterObjectClass.isAssignableFrom(Float.class) ||
                        parameterObjectClass.isAssignableFrom(Character.class) || parameterObjectClass.isAssignableFrom(Boolean.class));
    }

    /**
     * 是否List的实现类
     */
    private boolean isList(Class<?> clazz) {
        Class<?>[] interfaceClasses = clazz.getInterfaces();
        for (Class<?> interfaceClass : interfaceClasses) {
            if (List.class.isAssignableFrom(interfaceClass)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 是否Map的实现类
     */
    private boolean isMap(Class<?> parameterObjectClass) {
        Class<?>[] interfaceClasses = parameterObjectClass.getInterfaces();
        for (Class<?> interfaceClass : interfaceClasses) {
            if (Map.class.isAssignableFrom(interfaceClass)) {
                return true;
            }
        }

        return false;
    }

}
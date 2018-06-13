package cn.sf.auto.mybatis.util;

import cn.sf.auto.util.PropertiesLoad;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Paths;
import java.util.Map;

/**
 * Created by nijianfeng on 18/1/29.
 */
public class Constants {

    public final static String underLine = "under_line";
    public final static String camel = "camel";

    public final static String dbUrl = PropertiesLoad.getByKey("db_url", Boolean.TRUE);
    public final static String dbUser = PropertiesLoad.getByKey("db_user", Boolean.TRUE);
    public final static String dbPwd = PropertiesLoad.getByKey("db_pwd", Boolean.TRUE);
    public final static String dbSchema = PropertiesLoad.getByKey("db_schema", Boolean.TRUE);
    public final static String dbNameRule = PropertiesLoad.getByKey("db_name_rule", Boolean.TRUE);
    public final static String javaNameRule = PropertiesLoad.getByKey("java_name_rule", Boolean.TRUE);

    public final static String tableNames = PropertiesLoad.getByKey("table_names", Boolean.FALSE);

    public final static String path;
    static {
        String temp = PropertiesLoad.getByKey("path", Boolean.FALSE);
        if("default".equals(temp)|| StringUtils.isBlank(temp)){
            path = Paths.get("src/main/resources/dir/").toFile().getAbsolutePath();
        }else{
            path = temp;
        }
        System.out.println("文件生成位置:" + path);
    }

    public final static Map<String, String> typeMap = Maps.newHashMap();
    static {
        typeMap.put("datetime", "Date");
        typeMap.put("date", "Date");
        typeMap.put("varchar", "String");
        typeMap.put("char", "String");
        typeMap.put("tinyint", "Integer");
        typeMap.put("int", "Integer");
        typeMap.put("long", "Long");
        typeMap.put("bigint", "Long");
        typeMap.put("mediumint", "Long");
        typeMap.put("smallint", "Integer");
        typeMap.put("bit", "Boolean");
    }


}

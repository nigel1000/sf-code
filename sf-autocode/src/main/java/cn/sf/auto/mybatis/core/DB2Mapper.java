package cn.sf.auto.mybatis.core;

import cn.sf.auto.mybatis.model.MapperVo;
import cn.sf.auto.mybatis.model.Table;
import cn.sf.auto.mybatis.util.*;
import cn.sf.auto.mybatis.util.base.DBUtils;
import cn.sf.auto.util.PropertiesLoad;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by nijianfeng on 18/1/29.
 */
public class DB2Mapper {

    private DBUtils dbUtils = new MySqlUtils();

    private Boolean needGen = Boolean.valueOf(PropertiesLoad.getByKey("gen_mapper", Boolean.TRUE));

    private String daoPackage;
    private String daoType;
    private String domainPackage;
    private String tableNames;
    private String dateNowVal;
    private String dynamicCondition;
    private String mapperIds;
    private String mapperPath;

    public DB2Mapper() {
        if (needGen) {
            daoPackage = PropertiesLoad.getByKey("mapper_dao_package", Boolean.TRUE);
            daoType = PropertiesLoad.getByKey("mapper_dao_type", Boolean.TRUE);
            domainPackage = PropertiesLoad.getByKey("mapper_domain_package", Boolean.TRUE);
            tableNames = PropertiesLoad.getByKey("mapper_table_names", Boolean.FALSE);
            if (StringUtils.isBlank(tableNames)) {
                tableNames = Constants.tableNames;
            }
            dateNowVal = PropertiesLoad.getByKey("date_to_now", Boolean.FALSE);
            dynamicCondition = PropertiesLoad.getByKey("mapper_id_dynamic_condition_exclude", Boolean.FALSE);
            mapperIds = PropertiesLoad.getByKey("mapper_sql_ids", Boolean.TRUE);
            mapperPath = PropertiesLoad.getByKey("mapper_path", Boolean.FALSE);
            if (StringUtils.isBlank(mapperPath)) {
                mapperPath = Constants.path;
            }
        }
    }

    public synchronized void genMapper() {
        if (!needGen) {
            return;
        }
        List<String> dateNowValList = Splitter.on(",").omitEmptyStrings().trimResults().splitToList(dateNowVal);
        List<String> dynamicCondList = Splitter.on(",").omitEmptyStrings().trimResults().splitToList(dynamicCondition);
        List<String> mapperIdList = Splitter.on(",").omitEmptyStrings().trimResults().splitToList(mapperIds);
        List<String> nameList = Splitter.on(",").omitEmptyStrings().trimResults().splitToList(tableNames);
        if (nameList.contains("all")) {
            nameList = dbUtils.getTableNames();
        }
        for (String t : nameList) {
            Table table = dbUtils.getTable(t);
            Map<String, Object> tplMap = Maps.newHashMap();
            tplMap.put("daoPackage", daoPackage);
            tplMap.put("daoType", daoType);
            tplMap.put("domainPackage", domainPackage);
            tplMap.put("tableName", t);
            tplMap.put("dateNowValList", dateNowValList);
            tplMap.put("dynamicCondList", dynamicCondList);
            tplMap.put("mapperIdList", mapperIdList);
            String className =
                    NameUtils.firstUpper(NameUtils.ruleConvert(t, Constants.dbNameRule, Constants.javaNameRule));
            tplMap.put("className", className);
            List<MapperVo> mapperVos = Lists.newArrayList();
            table.getFields().forEach(f -> {
                MapperVo mapperVo = new MapperVo();
                mapperVo.setDbName(f.getField());
                mapperVo.setJavaName(NameUtils
                        .firstLower(NameUtils.ruleConvert(f.getField(), Constants.dbNameRule, Constants.javaNameRule)));
                mapperVo.setType(Constants.typeMap.get(f.getType()));
                mapperVos.add(mapperVo);
            });
            tplMap.put("mapperVos", mapperVos);
            FileUtils.genFile(mapperPath + "/mapper/" + className + "Mapper.xml",
                    TemplateUtils.genTemplate("classpath:tpl/", "mapper.tpl", tplMap));
        }
        DBUtils.closeConn();
    }

}

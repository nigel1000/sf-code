package cn.sf.auto.mybatis.core;

import cn.sf.auto.mybatis.model.ClassVo;
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
public class DB2Domain {

    private DBUtils dbUtils = new MySqlUtils();

    private Boolean needGen = Boolean.valueOf(PropertiesLoad.getByKey("gen_domain", Boolean.TRUE));

    private String domainPackage;
    private String tableNames;
    private String domainPath;

    public DB2Domain() {
        if (needGen) {
            domainPackage = PropertiesLoad.getByKey("domain_package", Boolean.TRUE);
            tableNames = PropertiesLoad.getByKey("domain_table_names", Boolean.FALSE);
            if (StringUtils.isBlank(tableNames)) {
                tableNames = Constants.tableNames;
            }
            domainPath = PropertiesLoad.getByKey("domain_path", Boolean.FALSE);
            if (StringUtils.isBlank(domainPath)) {
                domainPath = Constants.path;
            }
        }
    }

    public synchronized void genDomain() {
        if (!needGen) {
            return;
        }
        List<String> nameList = Splitter.on(",").omitEmptyStrings().trimResults().splitToList(tableNames);
        if (nameList.contains("all")) {
            nameList = dbUtils.getTableNames();
        }
        for (String t : nameList) {
            Table table = dbUtils.getTable(t);
            Map<String, Object> tplMap = Maps.newHashMap();
            tplMap.put("domainPackage", domainPackage);
            String className =
                    NameUtils.firstUpper(NameUtils.ruleConvert(t, Constants.dbNameRule, Constants.javaNameRule));
            tplMap.put("className", className);
            tplMap.put("tableComment", table.getComment());
            List<ClassVo> classVos = Lists.newArrayList();
            table.getFields().forEach(f -> {
                ClassVo classVo = new ClassVo();
                String type;
                // 末尾带有id的属性类型为Long
                if (f.getField().toLowerCase().endsWith("id") || f.getField().equals("id")) {
                    type = Constants.typeMap.get("long");
                } else {
                    type = Constants.typeMap.get(f.getType());
                }
                classVo.setType(type);
                classVo.setName(NameUtils
                        .firstLower(NameUtils.ruleConvert(f.getField(), Constants.dbNameRule, Constants.javaNameRule)));
                classVo.setMemo(f.getMemo());
                classVos.add(classVo);
            });
            tplMap.put("classVos", classVos);

            FileUtils.genFile(domainPath + "/domain/" + className + ".java",
                    TemplateUtils.genTemplate("classpath:tpl/", "domain.tpl", tplMap));
        }
        DBUtils.closeConn();
    }


}

package cn.sf.auto.mybatis.util;

import cn.sf.auto.exps.AutoCodeException;
import cn.sf.auto.mybatis.model.Field;
import cn.sf.auto.mybatis.model.Table;
import cn.sf.auto.mybatis.util.base.DBUtils;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by nijianfeng on 18/1/29.
 */
public class MySqlUtils extends DBUtils {

    private static final LoadingCache<String, Table> tableCache;
    static {
        tableCache = CacheBuilder.newBuilder().expireAfterWrite(60, TimeUnit.MINUTES)
                .build(new CacheLoader<String, Table>() {
                    @Override
                    public Table load(String tableName) throws Exception {
                        return getTableByName(tableName);
                    }
                });
    }
    private static final LoadingCache<String, List<String>> tableNameCache;
    static {
        tableNameCache = CacheBuilder.newBuilder().expireAfterWrite(60, TimeUnit.MINUTES)
                .build(new CacheLoader<String, List<String>>() {
                    @Override
                    public List<String> load(String schema) throws Exception {
                        return getNameBySchema(schema);
                    }
                });
    }


    // 获取表信息
    public Table getTable(String tableName) {
        return tableCache.getUnchecked(tableName);
    }

    // 获取库下所有表
    public List<String> getTableNames() {
        return tableNameCache.getUnchecked(Constants.dbSchema);
    }

    private static List<String> getNameBySchema(String schema) {
        List<String> names = Lists.newArrayList();
        try {
            // 获取表名列表
            PreparedStatement ps = getConnection().prepareStatement(
                    "SELECT table_name FROM Information_schema.tables WHERE table_schema = " + "'" + schema + "'");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                names.add(rs.getString(1));
            }
            close(rs, ps, null);
        } catch (Exception e) {
            closeConn();
            throw AutoCodeException.valueOf("获取 table name 失败", e);
        }
        return names;
    }

    private static final String SELECT_FIELD = " column_name as field,";
    private static final String SELECT_TYPE = " data_type as type,";
    private static final String SELECT_MEMO = " column_comment as memo,";
    private static final String SELECT_NUMERIC_LENGTH = " numeric_precision as numericLength,";
    private static final String SELECT_NUMERIC_SCALE = " numeric_scale as numericScale, ";
    private static final String SELECT_IS_NULLABLE = " is_nullable as isNullable,";
    private static final String SELECT_IS_AUTO_INCREMENT =
            " CASE WHEN extra = 'auto_increment' THEN 'true' ELSE 'false' END as isAutoIncrement,";
    private static final String SELECT_IS_DEFAULT = " column_default as isDefault,";
    private static final String SELECT_CHARACTER_LENGTH = " character_maximum_length  AS characterLength ";
    private static final String SELECT_SCHEMA = "SELECT " + SELECT_FIELD + SELECT_TYPE + SELECT_MEMO
            + SELECT_NUMERIC_LENGTH + SELECT_NUMERIC_SCALE + SELECT_IS_NULLABLE + SELECT_IS_AUTO_INCREMENT
            + SELECT_IS_DEFAULT + SELECT_CHARACTER_LENGTH + " FROM Information_schema.columns "
            + "WHERE  table_schema ='" + Constants.dbSchema + "' AND table_Name = ";

    private static Table getTableByName(String tableName) {
        System.out.println("start query " + tableName + " #################################");
        String table = "'" + tableName + "'";
        String sql = SELECT_SCHEMA + table;
        System.out.println("execute sql: " + sql);
        try {
            PreparedStatement ps = getConnection().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            List<Field> fields = Lists.newArrayList();
            while (rs.next()) {
                Field field = new Field();
                field.setField(rs.getString(1));
                field.setType(rs.getString(2));
                field.setMemo(rs.getString(3));
                field.setNumericLength(rs.getString(4));
                field.setNumericScale(rs.getString(5));
                field.setIsNullable(rs.getString(6));
                field.setIsAutoIncrement(Boolean.valueOf(rs.getString(7)));
                field.setIsDefault(rs.getString(8));
                field.setCharacterLength(rs.getString(9));
                fields.add(field);
                // 打印数据库某个表每列的返回数据
                System.out.println(field);
            }
            // 获取表描述
            ps = getConnection()
                    .prepareStatement("SELECT table_comment FROM Information_schema.tables WHERE table_Name =" + table);
            rs = ps.executeQuery();
            String tableComment = "无";
            while (rs.next()) {
                tableComment = rs.getString(1);
            }
            close(rs, ps, null);
            System.out.println("end " + tableName + " #################################");
            return Table.builder().fields(fields).name(tableName).comment(tableComment).build();
        } catch (Exception e) {
            closeConn();
            throw AutoCodeException.valueOf("获取schema数据失败", e);
        }
    }

}

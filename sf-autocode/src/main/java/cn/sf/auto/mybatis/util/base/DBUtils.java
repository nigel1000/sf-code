package cn.sf.auto.mybatis.util.base;

import cn.sf.auto.exps.AutoCodeException;
import cn.sf.auto.mybatis.model.Table;
import cn.sf.auto.mybatis.util.Constants;

import java.sql.*;
import java.util.List;

/**
 * Created by nijianfeng on 18/1/29.
 */
public abstract class DBUtils {

    public abstract Table getTable(String tableName);

    public abstract List<String> getTableNames();

    private static Connection conn;

    protected synchronized static Connection getConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            if (conn == null) {
                conn = DriverManager.getConnection(Constants.dbUrl, Constants.dbUser, Constants.dbPwd);
            }
            return conn;
        } catch (Exception e) {
            String message = "get db connection failed.";
            throw AutoCodeException.valueOf(message);
        }
    }

    public static void closeConn() {
        close(null, null, getConnection());
    }

    protected static void close(ResultSet rs, PreparedStatement ps, Connection conn) {
        // 关闭记录集
        if (rs != null) {
            try {
                rs.close();
                rs = null;
            } catch (SQLException e) {
                String message = "close rs failed.";
                throw AutoCodeException.valueOf(message);
            }
        }
        // 关闭声明
        if (ps != null) {
            try {
                ps.close();
                ps = null;
            } catch (SQLException e) {
                String message = "close ps failed.";
                throw AutoCodeException.valueOf(message);
            }
        }
        // 关闭链接对象
        if (conn != null) {
            try {
                conn.close();
                conn = null;
            } catch (SQLException e) {
                String message = "close conn failed.";
                throw AutoCodeException.valueOf(message);
            }
        }
    }
}

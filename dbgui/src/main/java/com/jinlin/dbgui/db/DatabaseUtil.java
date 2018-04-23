package com.jinlin.dbgui.db;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.NonNull;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据库处理类，数据库操作仅分为2种：读（查），写（增删改）。
 *
 * @date Created on 2018/4/17
 */
public class DatabaseUtil {
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/";
    private static DatabaseUtil instance = new DatabaseUtil();
    private Connection conn = null;//单连接
    private Gson gson = null;

    public synchronized static DatabaseUtil getInstance() {
        return instance;
    }

    private DatabaseUtil() {
    }

    public void init(String dbName, String userName, String passWord) {
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL + dbName, userName, passWord);
            gson = new Gson();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return conn;
    }

    public void release() {
        try {
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <T> List<T> query(@NonNull String sql, @NonNull Type type) {
        if (conn == null) throw new RuntimeException("DatabaseUtil : Please Init First.");
        //enable table name,but code in annotation
        //deal
        List<T> list = new ArrayList<>();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                int col = rs.getMetaData().getColumnCount();
                JsonObject json = new JsonObject();
                for (int i = 1; i <= col; i++) {
                    String key = rs.getMetaData().getColumnName(i);
                    String value = rs.getString(i);
                    json.addProperty(key, value);
                }
                list.add(gson.fromJson(json.toString(), type));
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            System.err.println("sql = " + sql);
            e.printStackTrace();
        }
        return list;
    }

    public boolean modify(@NonNull String sql) {
        if (conn == null) throw new RuntimeException("DatabaseUtil : Please Init First.");
        //enable table name,but code in annotation
        //deal
        int rs = 0;
        try {
            Statement stmt = conn.createStatement();
            rs = stmt.executeUpdate(sql);
            stmt.close();
        } catch (Exception e) {
            System.err.println("sql = " + sql);
            e.printStackTrace();
        }
        return rs > 0;
    }
}

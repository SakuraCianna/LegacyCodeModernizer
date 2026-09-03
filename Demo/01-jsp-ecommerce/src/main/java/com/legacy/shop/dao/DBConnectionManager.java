package com.legacy.shop.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Legacy Database Connection Manager using direct DriverManager and hardcoded credentials.
 * Classic legacy monolithic pattern before standard connection pools.
 */
public class DBConnectionManager {

    private static final String DRIVER_CLASS = "com.mysql.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/shop_db?useUnicode=true&characterEncoding=UTF-8&useSSL=false";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "root123";

    static {
        try {
            Class.forName(DRIVER_CLASS);
        } catch (ClassNotFoundException e) {
            System.err.println("[CRITICAL] MySQL JDBC Driver not found: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

package com.legacy.shop.dao;

import com.legacy.shop.model.Order;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Legacy Data Access Object using raw JDBC statements and SQL concatenation.
 */
public class OrderDAO {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/shop_db";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "root123";

    private Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    public boolean saveOrder(Order order) {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.createStatement();
            
            // Legacy Anti-Pattern: String concatenation vulnerable to SQL injection
            String sql = "INSERT INTO orders (order_number, customer_name, shipping_address, total_amount, status, created_at) VALUES ('"
                    + order.getOrderNumber() + "', '"
                    + order.getCustomerName() + "', '"
                    + order.getShippingAddress() + "', "
                    + order.getTotalAmount() + ", '"
                    + order.getStatus() + "', NOW())";

            int rows = stmt.executeUpdate(sql);
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public List<Order> findOrdersByCustomer(String customerName) {
        List<Order> list = new ArrayList<Order>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            stmt = conn.createStatement();
            String sql = "SELECT * FROM orders WHERE customer_name = '" + customerName + "'";
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Order order = new Order();
                order.setId(rs.getLong("id"));
                order.setOrderNumber(rs.getString("order_number"));
                order.setCustomerName(rs.getString("customer_name"));
                order.setShippingAddress(rs.getString("shipping_address"));
                order.setTotalAmount(rs.getDouble("total_amount"));
                order.setStatus(rs.getString("status"));
                order.setCreatedAt(rs.getTimestamp("created_at"));
                list.add(order);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return list;
    }
}

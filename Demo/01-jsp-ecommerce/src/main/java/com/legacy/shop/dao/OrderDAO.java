package com.legacy.shop.dao;

import com.legacy.shop.model.Order;
import com.legacy.shop.model.OrderItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    public boolean existsByIdempotencyKey(String idempotencyKey, Connection conn) throws SQLException {
        if (idempotencyKey == null || idempotencyKey.trim().isEmpty()) {
            return false;
        }
        String sql = "SELECT id FROM orders WHERE idempotency_key = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, idempotencyKey);
        ResultSet rs = ps.executeQuery();
        boolean exists = rs.next();
        rs.close();
        ps.close();
        return exists;
    }

    public Long insertOrder(Order order, Connection conn) throws SQLException {
        String sql = "INSERT INTO orders (order_number, idempotency_key, customer_name, customer_email, shipping_address, raw_subtotal, discount_amount, total_amount, applied_coupon, status, created_at, updated_at) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, order.getOrderNumber());
        ps.setString(2, order.getIdempotencyKey());
        ps.setString(3, order.getCustomerName());
        ps.setString(4, order.getCustomerEmail());
        ps.setString(5, order.getShippingAddress());
        ps.setDouble(6, order.getRawSubtotal());
        ps.setDouble(7, order.getDiscountAmount());
        ps.setDouble(8, order.getTotalAmount());
        ps.setString(9, order.getAppliedCoupon());
        ps.setString(10, order.getStatus());

        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();
        Long orderId = null;
        if (rs.next()) {
            orderId = rs.getLong(1);
            order.setId(orderId);
        }
        rs.close();
        ps.close();

        // Insert Order Items
        if (orderId != null && order.getItems() != null) {
            String itemSql = "INSERT INTO order_items (order_id, product_id, product_name, quantity, unit_price, subtotal) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement itemPs = conn.prepareStatement(itemSql);
            for (OrderItem item : order.getItems()) {
                itemPs.setLong(1, orderId);
                itemPs.setLong(2, item.getProductId());
                itemPs.setString(3, item.getProductName());
                itemPs.setInt(4, item.getQuantity());
                itemPs.setDouble(5, item.getUnitPrice());
                itemPs.setDouble(6, item.getSubtotal());
                itemPs.addBatch();
            }
            itemPs.executeBatch();
            itemPs.close();
        }

        return orderId;
    }

    public Order findByOrderNumber(String orderNumber) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionManager.getConnection();
            String sql = "SELECT * FROM orders WHERE order_number = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, orderNumber);
            rs = ps.executeQuery();
            if (rs.next()) {
                Order order = new Order();
                order.setId(rs.getLong("id"));
                order.setOrderNumber(rs.getString("order_number"));
                order.setIdempotencyKey(rs.getString("idempotency_key"));
                order.setCustomerName(rs.getString("customer_name"));
                order.setCustomerEmail(rs.getString("customer_email"));
                order.setShippingAddress(rs.getString("shipping_address"));
                order.setRawSubtotal(rs.getDouble("raw_subtotal"));
                order.setDiscountAmount(rs.getDouble("discount_amount"));
                order.setTotalAmount(rs.getDouble("total_amount"));
                order.setAppliedCoupon(rs.getString("applied_coupon"));
                order.setStatus(rs.getString("status"));
                order.setCreatedAt(rs.getTimestamp("created_at"));
                order.setUpdatedAt(rs.getTimestamp("updated_at"));
                return order;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                DBConnectionManager.close(conn);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    public List<Order> listRecentOrders(int limit) {
        List<Order> list = new ArrayList<Order>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionManager.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM orders ORDER BY created_at DESC LIMIT " + limit);
            while (rs.next()) {
                Order o = new Order();
                o.setId(rs.getLong("id"));
                o.setOrderNumber(rs.getString("order_number"));
                o.setCustomerName(rs.getString("customer_name"));
                o.setTotalAmount(rs.getDouble("total_amount"));
                o.setStatus(rs.getString("status"));
                o.setCreatedAt(rs.getTimestamp("created_at"));
                list.add(o);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                DBConnectionManager.close(conn);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return list;
    }
}

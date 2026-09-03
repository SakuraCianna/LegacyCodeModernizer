package com.legacy.shop.dao;

import com.legacy.shop.model.Coupon;
import java.sql.*;

public class CouponDAO {

    public Coupon findValidCoupon(String code, Connection conn) throws SQLException {
        String sql = "SELECT id, code, discount_amount, min_purchase, remaining_usage, expire_at FROM coupons WHERE code = ? AND expire_at > NOW() AND remaining_usage > 0";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, code.trim().toUpperCase());
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            Coupon c = new Coupon(
                rs.getLong("id"),
                rs.getString("code"),
                rs.getDouble("discount_amount"),
                rs.getDouble("min_purchase"),
                rs.getInt("remaining_usage"),
                rs.getTimestamp("expire_at")
            );
            rs.close();
            ps.close();
            return c;
        }
        rs.close();
        ps.close();
        return null;
    }

    public boolean decrementCouponUsage(Long id, Connection conn) throws SQLException {
        String sql = "UPDATE coupons SET remaining_usage = remaining_usage - 1 WHERE id = ? AND remaining_usage > 0";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setLong(1, id);
        int rows = ps.executeUpdate();
        ps.close();
        return rows > 0;
    }
}

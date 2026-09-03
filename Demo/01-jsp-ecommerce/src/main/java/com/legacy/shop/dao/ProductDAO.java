package com.legacy.shop.dao;

import com.legacy.shop.model.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    public Product findById(Long id, Connection conn) throws SQLException {
        String sql = "SELECT id, sku, name, category, price, stock, version FROM products WHERE id = " + id;
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        if (rs.next()) {
            Product p = new Product(
                rs.getLong("id"),
                rs.getString("sku"),
                rs.getString("name"),
                rs.getString("category"),
                rs.getDouble("price"),
                rs.getInt("stock"),
                rs.getInt("version")
            );
            rs.close();
            stmt.close();
            return p;
        }
        rs.close();
        stmt.close();
        return null;
    }

    public Product findByIdForUpdate(Long id, Connection conn) throws SQLException {
        // Pessimistic Row Lock for Concurrency Control
        String sql = "SELECT id, sku, name, category, price, stock, version FROM products WHERE id = ? FOR UPDATE";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setLong(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            Product p = new Product(
                rs.getLong("id"),
                rs.getString("sku"),
                rs.getString("name"),
                rs.getString("category"),
                rs.getDouble("price"),
                rs.getInt("stock"),
                rs.getInt("version")
            );
            rs.close();
            ps.close();
            return p;
        }
        rs.close();
        ps.close();
        return null;
    }

    public boolean deductStockAtomic(Long id, int quantity, Connection conn) throws SQLException {
        // Fool-proof non-negative guard in SQL
        String sql = "UPDATE products SET stock = stock - ?, version = version + 1 WHERE id = ? AND stock >= ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, quantity);
        ps.setLong(2, id);
        ps.setInt(3, quantity);
        int rows = ps.executeUpdate();
        ps.close();
        return rows > 0;
    }

    public List<Product> listAll() {
        List<Product> list = new ArrayList<Product>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionManager.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM products ORDER BY id ASC");
            while (rs.next()) {
                list.add(new Product(
                    rs.getLong("id"),
                    rs.getString("sku"),
                    rs.getString("name"),
                    rs.getString("category"),
                    rs.getDouble("price"),
                    rs.getInt("stock"),
                    rs.getInt("version")
                ));
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

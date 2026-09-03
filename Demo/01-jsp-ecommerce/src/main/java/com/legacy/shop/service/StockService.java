package com.legacy.shop.service;

import com.legacy.shop.dao.ProductDAO;
import com.legacy.shop.model.Product;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class StockService {

    private final ProductDAO productDAO = new ProductDAO();

    public void verifyAndLockStock(Map<Long, Integer> itemQuantities, Connection conn) throws SQLException {
        for (Map.Entry<Long, Integer> entry : itemQuantities.entrySet()) {
            Long productId = entry.getKey();
            Integer requestedQty = entry.getValue();

            if (requestedQty <= 0) {
                throw new IllegalArgumentException("Invalid purchase quantity: " + requestedQty);
            }

            // Pessimistic lock row to avoid race conditions and overselling
            Product p = productDAO.findByIdForUpdate(productId, conn);
            if (p == null) {
                throw new IllegalStateException("Product not found: " + productId);
            }
            if (p.getStock() < requestedQty) {
                throw new IllegalStateException("Insufficient inventory for product '" + p.getName() + "'. Available: " + p.getStock() + ", Requested: " + requestedQty);
            }
        }
    }

    public void deductStock(Map<Long, Integer> itemQuantities, Connection conn) throws SQLException {
        for (Map.Entry<Long, Integer> entry : itemQuantities.entrySet()) {
            Long productId = entry.getKey();
            Integer qty = entry.getValue();

            boolean success = productDAO.deductStockAtomic(productId, qty, conn);
            if (!success) {
                throw new IllegalStateException("Stock deduction failed due to concurrent modification on product id: " + productId);
            }
        }
    }
}

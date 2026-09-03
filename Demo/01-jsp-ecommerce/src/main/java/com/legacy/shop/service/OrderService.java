package com.legacy.shop.service;

import com.legacy.shop.dao.CouponDAO;
import com.legacy.shop.dao.DBConnectionManager;
import com.legacy.shop.dao.OrderDAO;
import com.legacy.shop.dao.ProductDAO;
import com.legacy.shop.model.Coupon;
import com.legacy.shop.model.Order;
import com.legacy.shop.model.OrderItem;
import com.legacy.shop.model.Product;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OrderService {

    private final OrderDAO orderDAO = new OrderDAO();
    private final ProductDAO productDAO = new ProductDAO();
    private final CouponDAO couponDAO = new CouponDAO();
    private final StockService stockService = new StockService();
    private final PriceCalculatorService priceCalculatorService = new PriceCalculatorService();

    public Order checkout(String idempotencyKey, String customerName, String customerEmail, String shippingAddress, Map<Long, Integer> items, String couponCode) throws Exception {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Cart cannot be empty");
        }

        Connection conn = null;
        try {
            conn = DBConnectionManager.getConnection();
            conn.setAutoCommit(false); // Manual ACID Transaction control

            // 1. Idempotency Check: Prevent duplicate charge on network retry
            if (orderDAO.existsByIdempotencyKey(idempotencyKey, conn)) {
                throw new IllegalStateException("Duplicate order submission detected for token: " + idempotencyKey);
            }

            // 2. Concurrency Lock: Lock products and verify inventory
            stockService.verifyAndLockStock(items, conn);

            // 3. Compute Subtotal and build items
            double rawSubtotal = 0.0;
            Order order = new Order();
            for (Map.Entry<Long, Integer> entry : items.entrySet()) {
                Product p = productDAO.findById(entry.getKey(), conn);
                int qty = entry.getValue();
                double itemSubtotal = p.getPrice() * qty;
                rawSubtotal += itemSubtotal;

                OrderItem orderItem = new OrderItem();
                orderItem.setProductId(p.getId());
                orderItem.setProductName(p.getName());
                orderItem.setQuantity(qty);
                orderItem.setUnitPrice(p.getPrice());
                orderItem.setSubtotal(itemSubtotal);
                order.getItems().add(orderItem);
            }

            // 4. Calculate Discounts, Taxes, and Final Bill
            PriceCalculatorService.PriceBreakdown breakdown = priceCalculatorService.calculatePrice(rawSubtotal, couponCode, conn);

            // 5. Deduct Stock
            stockService.deductStock(items, conn);

            // 6. Consume Coupon if applicable
            if (breakdown.couponCode != null) {
                Coupon c = couponDAO.findValidCoupon(breakdown.couponCode, conn);
                if (c != null) {
                    couponDAO.decrementCouponUsage(c.getId(), conn);
                }
            }

            // 7. Save Order Entity
            order.setOrderNumber("ORD-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase());
            order.setIdempotencyKey(idempotencyKey);
            order.setCustomerName(customerName);
            order.setCustomerEmail(customerEmail);
            order.setShippingAddress(shippingAddress);
            order.setRawSubtotal(breakdown.rawSubtotal);
            order.setDiscountAmount(breakdown.discount);
            order.setTotalAmount(breakdown.finalTotal);
            order.setAppliedCoupon(breakdown.couponCode);
            order.setStatus(Order.STATUS_PAID);

            orderDAO.insertOrder(order, conn);

            conn.commit(); // Commit Transaction
            return order;
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on failure
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}

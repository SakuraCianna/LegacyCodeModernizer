package com.legacy.shop.service;

import com.legacy.shop.dao.CouponDAO;
import com.legacy.shop.model.Coupon;
import java.sql.Connection;
import java.sql.SQLException;

public class PriceCalculatorService {

    private final CouponDAO couponDAO = new CouponDAO();

    public static class PriceBreakdown {
        public double rawSubtotal;
        public double discount;
        public double tax;
        public double finalTotal;
        public String couponCode;

        public PriceBreakdown(double rawSubtotal, double discount, double tax, double finalTotal, String couponCode) {
            this.rawSubtotal = rawSubtotal;
            this.discount = discount;
            this.tax = tax;
            this.finalTotal = finalTotal;
            this.couponCode = couponCode;
        }
    }

    public PriceBreakdown calculatePrice(double rawSubtotal, String couponCode, Connection conn) throws SQLException {
        if (rawSubtotal < 0) {
            throw new IllegalArgumentException("Subtotal cannot be negative: " + rawSubtotal);
        }

        double discount = 0.0;
        String appliedCode = null;

        if (couponCode != null && !couponCode.trim().isEmpty()) {
            Coupon coupon = couponDAO.findValidCoupon(couponCode, conn);
            if (coupon != null && rawSubtotal >= coupon.getMinPurchase()) {
                discount = coupon.getDiscountAmount();
                appliedCode = coupon.getCode();
            }
        }

        // Tiered VIP system rule: orders > $500 get additional 5% off
        if (rawSubtotal > 500.0) {
            discount += (rawSubtotal - discount) * 0.05;
        }

        double discountedSubtotal = Math.max(0.0, rawSubtotal - discount);
        double tax = Math.round(discountedSubtotal * 0.08 * 100.0) / 100.0; // 8% sales tax
        double finalTotal = Math.round((discountedSubtotal + tax) * 100.0) / 100.0;

        return new PriceBreakdown(rawSubtotal, discount, tax, finalTotal, appliedCode);
    }
}

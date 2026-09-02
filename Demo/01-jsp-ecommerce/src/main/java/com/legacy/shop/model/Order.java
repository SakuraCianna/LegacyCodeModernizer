package com.legacy.shop.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Legacy JavaBean representation of an E-Commerce Order.
 * Uses legacy java.util.Date and mutable setters.
 */
public class Order implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String orderNumber;
    private String customerName;
    private String shippingAddress;
    private Double totalAmount;
    private String status;
    private Date createdAt;

    public Order() {
    }

    public Order(Long id, String orderNumber, String customerName, String shippingAddress, Double totalAmount, String status, Date createdAt) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.customerName = customerName;
        this.shippingAddress = shippingAddress;
        this.totalAmount = totalAmount;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}

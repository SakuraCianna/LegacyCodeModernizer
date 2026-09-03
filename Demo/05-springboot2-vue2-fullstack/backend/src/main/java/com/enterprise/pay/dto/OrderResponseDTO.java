package com.enterprise.pay.dto;

import com.enterprise.pay.constant.OrderStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class OrderResponseDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String orderNo;
    private Long userId;
    private String title;
    private BigDecimal totalAmount;
    private BigDecimal refundedAmount;
    private OrderStatus status;
    private Date createdAt;
    private Date paidAt;

    public OrderResponseDTO() {}

    public OrderResponseDTO(Long id, String orderNo, Long userId, String title, BigDecimal totalAmount, BigDecimal refundedAmount, OrderStatus status, Date createdAt, Date paidAt) {
        this.id = id;
        this.orderNo = orderNo;
        this.userId = userId;
        this.title = title;
        this.totalAmount = totalAmount;
        this.refundedAmount = refundedAmount;
        this.status = status;
        this.createdAt = createdAt;
        this.paidAt = paidAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public BigDecimal getRefundedAmount() { return refundedAmount; }
    public void setRefundedAmount(BigDecimal refundedAmount) { this.refundedAmount = refundedAmount; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public Date getPaidAt() { return paidAt; }
    public void setPaidAt(Date paidAt) { this.paidAt = paidAt; }
}

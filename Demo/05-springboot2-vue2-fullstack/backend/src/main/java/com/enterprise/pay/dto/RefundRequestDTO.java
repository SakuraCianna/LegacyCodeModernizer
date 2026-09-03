package com.enterprise.pay.dto;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

public class RefundRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "Order number cannot be blank")
    private String orderNo;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Refund amount is required")
    @DecimalMin(value = "0.01", message = "Refund amount must be greater than 0")
    private BigDecimal refundAmount;

    @NotBlank(message = "Refund reason must be specified")
    private String reason;

    @NotBlank(message = "Audit user is required")
    private String auditBy;

    public RefundRequestDTO() {}

    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public BigDecimal getRefundAmount() { return refundAmount; }
    public void setRefundAmount(BigDecimal refundAmount) { this.refundAmount = refundAmount; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getAuditBy() { return auditBy; }
    public void setAuditBy(String auditBy) { this.auditBy = auditBy; }
}

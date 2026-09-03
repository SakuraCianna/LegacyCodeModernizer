package com.enterprise.pay.dto;

import com.enterprise.pay.constant.PaymentChannel;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

public class PaymentRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "Order number cannot be blank")
    private String orderNo;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Payment amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be at least $0.01")
    private BigDecimal amount;

    @NotNull(message = "Payment channel must be specified")
    private PaymentChannel channel;

    @NotBlank(message = "Idempotency token is required")
    private String idempotencyToken;

    public PaymentRequestDTO() {}

    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public PaymentChannel getChannel() { return channel; }
    public void setChannel(PaymentChannel channel) { this.channel = channel; }
    public String getIdempotencyToken() { return idempotencyToken; }
    public void setIdempotencyToken(String idempotencyToken) { this.idempotencyToken = idempotencyToken; }
}

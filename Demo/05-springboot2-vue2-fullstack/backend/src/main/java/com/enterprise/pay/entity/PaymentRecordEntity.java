package com.enterprise.pay.entity;

import com.enterprise.pay.constant.PaymentChannel;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "pay_records", indexes = {
    @Index(name = "idx_pay_serial", columnList = "serial_no", unique = true),
    @Index(name = "idx_pay_order_no", columnList = "order_no")
})
public class PaymentRecordEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "serial_no", nullable = false, length = 64)
    private String serialNo;

    @NotBlank
    @Column(name = "order_no", nullable = false, length = 64)
    private String orderNo;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotNull
    @DecimalMin("0.01")
    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false, length = 32)
    private PaymentChannel channel;

    @Column(name = "idempotency_token", length = 64)
    private String idempotencyToken;

    @Column(name = "signature", length = 128)
    private String signature;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSerialNo() { return serialNo; }
    public void setSerialNo(String serialNo) { this.serialNo = serialNo; }
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
    public String getSignature() { return signature; }
    public void setSignature(String signature) { this.signature = signature; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}

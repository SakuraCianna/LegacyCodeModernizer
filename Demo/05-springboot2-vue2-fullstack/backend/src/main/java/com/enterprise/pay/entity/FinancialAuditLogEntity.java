package com.enterprise.pay.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "financial_audit_logs", indexes = {
    @Index(name = "idx_audit_user", columnList = "user_id"),
    @Index(name = "idx_audit_biz", columnList = "biz_reference_no")
})
public class FinancialAuditLogEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotBlank
    @Column(name = "biz_type", nullable = false, length = 32)
    private String bizType; // e.g., ORDER_PAY, REFUND_CREDIT, DEPOSIT

    @NotBlank
    @Column(name = "biz_reference_no", nullable = false, length = 64)
    private String bizReferenceNo;

    @NotNull
    @Column(name = "before_balance", nullable = false, precision = 14, scale = 2)
    private BigDecimal beforeBalance;

    @NotNull
    @Column(name = "delta_amount", nullable = false, precision = 14, scale = 2)
    private BigDecimal deltaAmount;

    @NotNull
    @Column(name = "after_balance", nullable = false, precision = 14, scale = 2)
    private BigDecimal afterBalance;

    @Column(name = "remark", length = 255)
    private String remark;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getBizType() { return bizType; }
    public void setBizType(String bizType) { this.bizType = bizType; }
    public String getBizReferenceNo() { return bizReferenceNo; }
    public void setBizReferenceNo(String bizReferenceNo) { this.bizReferenceNo = bizReferenceNo; }
    public BigDecimal getBeforeBalance() { return beforeBalance; }
    public void setBeforeBalance(BigDecimal beforeBalance) { this.beforeBalance = beforeBalance; }
    public BigDecimal getDeltaAmount() { return deltaAmount; }
    public void setDeltaAmount(BigDecimal deltaAmount) { this.deltaAmount = deltaAmount; }
    public BigDecimal getAfterBalance() { return afterBalance; }
    public void setAfterBalance(BigDecimal afterBalance) { this.afterBalance = afterBalance; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}

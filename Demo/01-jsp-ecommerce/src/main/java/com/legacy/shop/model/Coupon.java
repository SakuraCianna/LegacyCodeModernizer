package com.legacy.shop.model;

import java.io.Serializable;
import java.util.Date;

public class Coupon implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String code;
    private Double discountAmount;
    private Double minPurchase;
    private Integer remainingUsage;
    private Date expireAt;

    public Coupon() {}

    public Coupon(Long id, String code, Double discountAmount, Double minPurchase, Integer remainingUsage, Date expireAt) {
        this.id = id;
        this.code = code;
        this.discountAmount = discountAmount;
        this.minPurchase = minPurchase;
        this.remainingUsage = remainingUsage;
        this.expireAt = expireAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public Double getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(Double discountAmount) { this.discountAmount = discountAmount; }
    public Double getMinPurchase() { return minPurchase; }
    public void setMinPurchase(Double minPurchase) { this.minPurchase = minPurchase; }
    public Integer getRemainingUsage() { return remainingUsage; }
    public void setRemainingUsage(Integer remainingUsage) { this.remainingUsage = remainingUsage; }
    public Date getExpireAt() { return expireAt; }
    public void setExpireAt(Date expireAt) { this.expireAt = expireAt; }
}

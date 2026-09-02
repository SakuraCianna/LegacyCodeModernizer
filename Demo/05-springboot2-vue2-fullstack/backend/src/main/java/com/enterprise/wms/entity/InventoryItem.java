package com.enterprise.wms.entity;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * Legacy JPA Entity using javax.persistence annotations and Java 8 types.
 */
@Entity
@Table(name = "inventory_items")
public class InventoryItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "SKU code cannot be blank")
    @Column(name = "sku_code", unique = true, nullable = false, length = 64)
    private String skuCode;

    @NotBlank(message = "Item name is required")
    @Column(name = "item_name", nullable = false)
    private String itemName;

    @Column(name = "category")
    private String category;

    @NotNull(message = "Quantity cannot be null")
    @Min(value = 0, message = "Quantity must be non-negative")
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @NotNull(message = "Unit price cannot be null")
    @Column(name = "unit_price", nullable = false)
    private Double unitPrice;

    @Column(name = "location_zone")
    private String locationZone;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt;

    public InventoryItem() {
    }

    public InventoryItem(Long id, String skuCode, String itemName, String category, Integer quantity, Double unitPrice, String locationZone, Date createdAt, Date updatedAt) {
        this.id = id;
        this.skuCode = skuCode;
        this.itemName = itemName;
        this.category = category;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.locationZone = locationZone;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getLocationZone() {
        return locationZone;
    }

    public void setLocationZone(String locationZone) {
        this.locationZone = locationZone;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}

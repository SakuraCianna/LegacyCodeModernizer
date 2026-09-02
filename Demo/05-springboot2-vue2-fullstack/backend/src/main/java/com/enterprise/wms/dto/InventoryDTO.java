package com.enterprise.wms.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Legacy Java 8 POJO Data Transfer Object with mutable fields and getters/setters.
 */
public class InventoryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @NotBlank(message = "SKU is required")
    private String skuCode;

    @NotBlank(message = "Item name is required")
    private String itemName;

    private String category;

    @NotNull(message = "Quantity is required")
    @Min(0)
    private Integer quantity;

    @NotNull(message = "Price is required")
    private Double unitPrice;

    private String locationZone;

    public InventoryDTO() {
    }

    public InventoryDTO(Long id, String skuCode, String itemName, String category, Integer quantity, Double unitPrice, String locationZone) {
        this.id = id;
        this.skuCode = skuCode;
        this.itemName = itemName;
        this.category = category;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.locationZone = locationZone;
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
}

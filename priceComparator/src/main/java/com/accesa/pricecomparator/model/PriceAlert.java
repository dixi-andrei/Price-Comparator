package com.accesa.pricecomparator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceAlert {
    private Long id;
    private String productId;
    private String productName;
    private String store;
    private Double targetPrice;
    private String userId;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime triggeredAt;


    public PriceAlert(String productId, String productName, String store, Double targetPrice, String userId) {
        this.productId = productId;
        this.productName = productName;
        this.store = store;
        this.targetPrice = targetPrice;
        this.userId = userId;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getStore() {
        return store;
    }

    public Double getTargetPrice() {
        return targetPrice;
    }

    public String getUserId() {
        return userId;
    }

    public boolean isActive() {
        return isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getTriggeredAt() {
        return triggeredAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public void setTargetPrice(Double targetPrice) {
        this.targetPrice = targetPrice;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setTriggeredAt(LocalDateTime triggeredAt) {
        this.triggeredAt = triggeredAt;
    }
}
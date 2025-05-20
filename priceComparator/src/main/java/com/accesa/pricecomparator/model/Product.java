package com.accesa.pricecomparator.model;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @CsvBindByName(column = "product_id")
    private String productId;

    @CsvBindByName(column = "product_name")
    private String productName;

    @CsvBindByName(column = "product_category")
    private String productCategory;

    @CsvBindByName(column = "brand")
    private String brand;

    @CsvBindByName(column = "package_quantity")
    private Double packageQuantity;

    @CsvBindByName(column = "package_unit")
    private String packageUnit;

    @CsvBindByName(column = "price")
    private Double price;

    @CsvBindByName(column = "currency")
    private String currency;

    // Not in CSV, will be set from filename
    private String store;
    private LocalDate date;

    // Calculated fields
    private Double discountedPrice;
    private Integer discountPercentage;

    // Helper method to calculate price per unit (e.g., price per kg)
    public Double getPricePerUnit() {
        if (packageQuantity == null || packageQuantity == 0) {
            return null;
        }
        return price / packageQuantity;
    }

    // Helper method to calculate discounted price
    public Double getDiscountedPriceValue() {
        if (discountPercentage == null || discountPercentage == 0) {
            return price;
        }
        return price * (1 - (discountPercentage / 100.0));
    }

    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public String getBrand() {
        return brand;
    }

    public Double getPackageQuantity() {
        return packageQuantity;
    }

    public String getPackageUnit() {
        return packageUnit;
    }

    public Double getPrice() {
        return price;
    }

    public String getCurrency() {
        return currency;
    }

    public String getStore() {
        return store;
    }

    public LocalDate getDate() {
        return date;
    }

    public Double getDiscountedPrice() {
        return discountedPrice;
    }

    public Integer getDiscountPercentage() {
        return discountPercentage;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setPackageQuantity(Double packageQuantity) {
        this.packageQuantity = packageQuantity;
    }

    public void setPackageUnit(String packageUnit) {
        this.packageUnit = packageUnit;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setDiscountedPrice(Double discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    public void setDiscountPercentage(Integer discountPercentage) {
        this.discountPercentage = discountPercentage;
    }
}
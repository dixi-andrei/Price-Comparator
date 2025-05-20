package com.accesa.pricecomparator.model;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Discount {
    @CsvBindByName(column = "product_id")
    private String productId;

    @CsvBindByName(column = "product_name")
    private String productName;

    @CsvBindByName(column = "brand")
    private String brand;

    @CsvBindByName(column = "package_quantity")
    private Double packageQuantity;

    @CsvBindByName(column = "package_unit")
    private String packageUnit;

    @CsvBindByName(column = "product_category")
    private String productCategory;

    @CsvBindByName(column = "from_date")
    @CsvDate(value = "yyyy-MM-dd")
    private LocalDate fromDate;

    @CsvBindByName(column = "to_date")
    @CsvDate(value = "yyyy-MM-dd")
    private LocalDate toDate;

    @CsvBindByName(column = "percentage_of_discount")
    private Integer percentageDiscount;

    // Not in CSV, will be set from filename
    private String store;
    private LocalDate discountDate;

    // Helper method to check if discount is active on a specific date
    public boolean isActiveOn(LocalDate date) {
        return date != null &&
                fromDate != null &&
                toDate != null &&
                (date.isEqual(fromDate) || date.isAfter(fromDate)) &&
                (date.isEqual(toDate) || date.isBefore(toDate));
    }

    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
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

    public String getProductCategory() {
        return productCategory;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public Integer getPercentageDiscount() {
        return percentageDiscount;
    }

    public String getStore() {
        return store;
    }

    public LocalDate getDiscountDate() {
        return discountDate;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setProductName(String productName) {
        this.productName = productName;
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

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }

    public void setPercentageDiscount(Integer percentageDiscount) {
        this.percentageDiscount = percentageDiscount;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public void setDiscountDate(LocalDate discountDate) {
        this.discountDate = discountDate;
    }
}
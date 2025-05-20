package com.accesa.pricecomparator.controller;

import com.accesa.pricecomparator.model.Discount;
import com.accesa.pricecomparator.model.Product;
import com.accesa.pricecomparator.service.DiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/discounts")
@RequiredArgsConstructor
public class DiscountController {

    private final DiscountService discountService;

    @GetMapping
    public List<Discount> getAllDiscounts() {
        return discountService.getAllDiscounts();
    }

    @GetMapping("/store/{store}")
    public List<Discount> getDiscountsByStore(@PathVariable String store) {
        return discountService.getDiscountsByStore(store);
    }

    @GetMapping("/active")
    public List<Discount> getActiveDiscounts(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        // If date is not provided, use current date
        LocalDate checkDate = date != null ? date : LocalDate.now();
        return discountService.getActiveDiscounts(checkDate);
    }

    @GetMapping("/new")
    public List<Discount> getNewDiscounts(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate since) {
        return discountService.getNewDiscounts(since);
    }

    @GetMapping("/category/{category}")
    public List<Discount> getDiscountsByCategory(@PathVariable String category) {
        return discountService.getDiscountsByCategory(category);
    }

    @GetMapping("/product/{productId}")
    public List<Discount> getDiscountsForProduct(@PathVariable String productId) {
        return discountService.getDiscountsForProduct(productId);
    }

    @GetMapping("/best")
    public List<Discount> getBestDiscounts(@RequestParam(defaultValue = "10") int limit) {
        return discountService.getBestDiscounts(limit);
    }

    @GetMapping("/best-products")
    public List<Product> getProductsWithBestDiscounts(@RequestParam(defaultValue = "10") int limit) {
        return discountService.getProductsWithBestDiscounts(limit);
    }

    @GetMapping("/history/{productId}")
    public Map<LocalDate, Integer> getDiscountHistory(
            @PathVariable String productId,
            @RequestParam(required = false) String store) {
        return discountService.getDiscountHistory(productId, store);
    }
}
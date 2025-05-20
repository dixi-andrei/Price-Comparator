package com.accesa.pricecomparator.controller;

import com.accesa.pricecomparator.model.Product;
import com.accesa.pricecomparator.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/best-discounts")
    public List<Product> getBestDiscounts(@RequestParam(defaultValue = "10") int limit) {
        return productService.getBestDiscounts(limit);
    }

    @GetMapping("/best-value")
    public List<Product> getBestValuePerUnit(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "10") int limit) {
        return productService.getBestValuePerUnit(category, limit);
    }

    @GetMapping("/compare")
    public Map<String, Product> compareProductPrices(
            @RequestParam String productName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return productService.compareProductPrices(productName, date);
    }

    @GetMapping("/price-history/{productId}")
    public Map<LocalDate, Double> getPriceHistory(
            @PathVariable String productId,
            @RequestParam(required = false) String store) {
        return productService.getPriceHistory(productId, store);
    }

    @PostMapping("/optimize-basket")
    public Map<String, Double> optimizeShoppingBasket(
            @RequestBody List<String> productIds,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return productService.optimizeShoppingBasket(productIds, date);
    }

    @GetMapping("/new-discounts")
    public List<Product> getNewDiscounts(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate since) {
        return productService.getNewDiscounts(since);
    }
}
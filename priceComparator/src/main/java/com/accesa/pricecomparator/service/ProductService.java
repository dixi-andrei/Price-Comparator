package com.accesa.pricecomparator.service;

import com.accesa.pricecomparator.model.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final CSVService csvService;

    /**
     * Gets products with the highest discount percentage across all stores
     * @param limit Number of products to return
     * @return List of products with highest discounts
     */
    public List<Product> getBestDiscounts(int limit) {
        return csvService.getAllProducts().stream()
                .filter(product -> product.getDiscountPercentage() != null && product.getDiscountPercentage() > 0)
                .sorted(Comparator.comparing(Product::getDiscountPercentage).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Gets products with the best value per unit (price per kg, price per liter)
     * @param category Product category to filter by
     * @param limit Number of products to return
     * @return List of products with best value per unit
     */
    public List<Product> getBestValuePerUnit(String category, int limit) {
        List<Product> products;

        if (category != null && !category.isBlank()) {
            products = csvService.getProductsByCategory(category);
        } else {
            products = csvService.getAllProducts();
        }

        return products.stream()
                .filter(product -> product.getPricePerUnit() != null)
                .sorted(Comparator.comparing(Product::getPricePerUnit))
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Compares prices for a specific product across different stores
     * @param productName Name of the product to compare
     * @param date Date to compare prices
     * @return Map of store to product with prices
     */
    public Map<String, Product> compareProductPrices(String productName, LocalDate date) {
        Map<String, Product> pricesByStore = new HashMap<>();

        if (date == null) {
            // Use the latest date for each store
            Map<String, LocalDate> latestDateByStore = new HashMap<>();

            csvService.getAllProducts().forEach(product -> {
                String store = product.getStore();
                LocalDate productDate = product.getDate();

                if (!latestDateByStore.containsKey(store) ||
                        productDate.isAfter(latestDateByStore.get(store))) {
                    latestDateByStore.put(store, productDate);
                }
            });

            // For each store, get the product with the given name on the latest date
            latestDateByStore.forEach((store, latestDate) -> {
                csvService.getProductsByStoreAndDate(store, latestDate).stream()
                        .filter(product -> product.getProductName().equalsIgnoreCase(productName))
                        .findFirst()
                        .ifPresent(product -> pricesByStore.put(store, product));
            });
        } else {
            // Use the specified date
            csvService.getAllProducts().stream()
                    .filter(product -> product.getProductName().equalsIgnoreCase(productName) &&
                            product.getDate().equals(date))
                    .forEach(product -> pricesByStore.put(product.getStore(), product));
        }

        return pricesByStore;
    }

    /**
     * Builds a price history for a product
     * @param productId Product ID
     * @param store Store name (optional)
     * @return Map of date to price
     */
    public Map<LocalDate, Double> getPriceHistory(String productId, String store) {
        List<Product> productsToAnalyze;

        if (store != null && !store.isBlank()) {
            productsToAnalyze = csvService.getProductsByStore(store);
        } else {
            productsToAnalyze = csvService.getAllProducts();
        }

        Map<LocalDate, Double> priceHistory = new TreeMap<>(); // TreeMap for chronological order

        productsToAnalyze.stream()
                .filter(product -> product.getProductId().equals(productId))
                .forEach(product -> {
                    // Use discounted price if available, otherwise regular price
                    Double price = product.getDiscountedPrice() != null ?
                            product.getDiscountedPrice() : product.getPrice();
                    priceHistory.put(product.getDate(), price);
                });

        return priceHistory;
    }

    /**
     * Builds a shopping basket with the lowest price
     * @param productIds List of product IDs to include in the basket
     * @param date Date for pricing, if null uses latest data
     * @return Map of store to total basket price
     */
    public Map<String, Double> optimizeShoppingBasket(List<String> productIds, LocalDate date) {
        Map<String, Map<String, Product>> productsByStore = new HashMap<>();
        Set<String> stores = new HashSet<>();

        // Collect all products across stores
        for (String productId : productIds) {
            Map<String, Product> storeProducts = new HashMap<>();

            csvService.getAllProducts().stream()
                    .filter(product -> product.getProductId().equals(productId))
                    .filter(product -> date == null || product.getDate().equals(date))
                    .forEach(product -> {
                        String store = product.getStore();
                        stores.add(store);

                        // Keep only the latest product if date is null
                        if (date == null) {
                            if (!storeProducts.containsKey(store) ||
                                    product.getDate().isAfter(storeProducts.get(store).getDate())) {
                                storeProducts.put(store, product);
                            }
                        } else {
                            storeProducts.put(store, product);
                        }
                    });

            productsByStore.put(productId, storeProducts);
        }

        // Calculate total basket price for each store
        Map<String, Double> basketPriceByStore = new HashMap<>();
        for (String store : stores) {
            double total = 0;
            boolean hasAllProducts = true;

            for (String productId : productIds) {
                Map<String, Product> storeProducts = productsByStore.get(productId);
                if (storeProducts.containsKey(store)) {
                    Product product = storeProducts.get(store);
                    Double price = product.getDiscountedPrice() != null ?
                            product.getDiscountedPrice() : product.getPrice();
                    total += price;
                } else {
                    hasAllProducts = false;
                    break;
                }
            }

            if (hasAllProducts) {
                basketPriceByStore.put(store, total);
            }
        }

        return basketPriceByStore;
    }

    /**
     * Finds new discounts added since a specific date
     * @param since Date to check for new discounts
     * @return List of products with new discounts
     */
    public List<Product> getNewDiscounts(LocalDate since) {
        return csvService.getAllProducts().stream()
                .filter(product -> product.getDiscountPercentage() != null && product.getDiscountPercentage() > 0)
                .filter(product -> product.getDate().isAfter(since) || product.getDate().isEqual(since))
                .collect(Collectors.toList());
    }
}
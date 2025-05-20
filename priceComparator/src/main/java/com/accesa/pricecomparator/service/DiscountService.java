package com.accesa.pricecomparator.service;

import com.accesa.pricecomparator.model.Discount;
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
public class DiscountService {

    private final CSVService csvService;

    /**
     * Gets all available discounts
     * @return List of all discounts
     */
    public List<Discount> getAllDiscounts() {
        return csvService.getAllDiscounts();
    }

    /**
     * Gets discounts for a specific store
     * @param store Store name
     * @return List of discounts for the store
     */
    public List<Discount> getDiscountsByStore(String store) {
        return csvService.getDiscountsByStore(store);
    }

    /**
     * Gets discounts that are active on a specific date
     * @param date Date to check
     * @return List of active discounts
     */
    public List<Discount> getActiveDiscounts(LocalDate date) {
        return csvService.getAllDiscounts().stream()
                .filter(discount -> discount.isActiveOn(date))
                .collect(Collectors.toList());
    }

    /**
     * Gets discounts that were newly added since a specific date
     * @param since Date to check from
     * @return List of new discounts
     */
    public List<Discount> getNewDiscounts(LocalDate since) {
        return csvService.getAllDiscounts().stream()
                .filter(discount -> discount.getDiscountDate().isEqual(since) ||
                        discount.getDiscountDate().isAfter(since))
                .collect(Collectors.toList());
    }

    /**
     * Gets discounts by product category
     * @param category Category to filter by
     * @return List of discounts for the category
     */
    public List<Discount> getDiscountsByCategory(String category) {
        return csvService.getAllDiscounts().stream()
                .filter(discount -> discount.getProductCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    /**
     * Gets discounts for a specific product
     * @param productId Product ID
     * @return List of discounts for the product
     */
    public List<Discount> getDiscountsForProduct(String productId) {
        return csvService.getAllDiscounts().stream()
                .filter(discount -> discount.getProductId().equals(productId))
                .collect(Collectors.toList());
    }

    /**
     * Gets the best discounts across all stores
     * @param limit Number of discounts to return
     * @return List of discounts with highest percentage
     */
    public List<Discount> getBestDiscounts(int limit) {
        return csvService.getAllDiscounts().stream()
                .sorted(Comparator.comparing(Discount::getPercentageDiscount).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Gets products that have highest discount percentage
     * @param limit Number of products to return
     * @return List of products with their discount information
     */
    public List<Product> getProductsWithBestDiscounts(int limit) {
        // Get all products
        List<Product> allProducts = csvService.getAllProducts();

        // Get best discounts
        List<Discount> bestDiscounts = getBestDiscounts(limit);

        // Create map of product ID to discount
        Map<String, Discount> discountMap = bestDiscounts.stream()
                .collect(Collectors.toMap(
                        Discount::getProductId,
                        discount -> discount,
                        (d1, d2) -> d1.getPercentageDiscount() > d2.getPercentageDiscount() ? d1 : d2
                ));

        // Filter and return products with best discounts
        return allProducts.stream()
                .filter(product -> discountMap.containsKey(product.getProductId()))
                .filter(product -> {
                    Discount discount = discountMap.get(product.getProductId());
                    return product.getStore().equals(discount.getStore()) &&
                            discount.isActiveOn(product.getDate());
                })
                .sorted(Comparator.comparing(product ->
                                discountMap.get(product.getProductId()).getPercentageDiscount(),
                        Comparator.reverseOrder()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Checks if a product has an active discount on a specific date
     * @param productId Product ID
     * @param store Store name
     * @param date Date to check
     * @return Optional of the discount if active, empty otherwise
     */
    public Optional<Discount> getActiveDiscountForProduct(String productId, String store, LocalDate date) {
        return csvService.getAllDiscounts().stream()
                .filter(discount -> discount.getProductId().equals(productId))
                .filter(discount -> discount.getStore().equals(store))
                .filter(discount -> discount.isActiveOn(date))
                .findFirst();
    }

    /**
     * Gets discount history for a specific product
     * @param productId Product ID
     * @param store Store name (optional)
     * @return Map of date to discount percentage
     */
    public Map<LocalDate, Integer> getDiscountHistory(String productId, String store) {
        List<Discount> discounts;

        if (store != null && !store.isEmpty()) {
            discounts = csvService.getDiscountsByStore(store).stream()
                    .filter(discount -> discount.getProductId().equals(productId))
                    .collect(Collectors.toList());
        } else {
            discounts = csvService.getAllDiscounts().stream()
                    .filter(discount -> discount.getProductId().equals(productId))
                    .collect(Collectors.toList());
        }

        // Create map with from date -> percentage
        Map<LocalDate, Integer> discountHistory = new TreeMap<>();
        for (Discount discount : discounts) {
            LocalDate from = discount.getFromDate();
            LocalDate to = discount.getToDate();
            Integer percentage = discount.getPercentageDiscount();

            // Add entry for each day in the discount period
            LocalDate currentDate = from;
            while (!currentDate.isAfter(to)) {
                discountHistory.put(currentDate, percentage);
                currentDate = currentDate.plusDays(1);
            }
        }

        return discountHistory;
    }
}
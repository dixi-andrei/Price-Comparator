package com.accesa.pricecomparator.service;

import com.accesa.pricecomparator.model.Discount;
import com.accesa.pricecomparator.model.Product;
import com.accesa.pricecomparator.util.CSVHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CSVService {

    private final CSVHelper csvHelper;

    @Value("${app.data.directory:./data}")
    private String dataDirectory;

    private final List<Product> allProducts = new ArrayList<>();
    private final List<Discount> allDiscounts = new ArrayList<>();
    private final Map<String, Map<LocalDate, List<Product>>> productsByStoreAndDate = new HashMap<>();
    private final Map<String, Map<LocalDate, List<Discount>>> discountsByStoreAndDate = new HashMap<>();

    @PostConstruct
    public void loadAllData() {
        loadProducts();
        loadDiscounts();
        applyDiscountsToProducts();
    }

    private void loadProducts() {
        List<String> productFiles = csvHelper.findProductFiles(dataDirectory);
        for (String file : productFiles) {
            List<Product> products = csvHelper.parseProductCSV(file);
            allProducts.addAll(products);

            // Organize by store and date
            for (Product product : products) {
                String store = product.getStore();
                LocalDate date = product.getDate();

                productsByStoreAndDate
                        .computeIfAbsent(store, k -> new HashMap<>())
                        .computeIfAbsent(date, k -> new ArrayList<>())
                        .add(product);
            }
        }
        log.info("Loaded {} products from {} files", allProducts.size(), productFiles.size());
    }

    private void loadDiscounts() {
        List<String> discountFiles = csvHelper.findDiscountFiles(dataDirectory);
        for (String file : discountFiles) {
            List<Discount> discounts = csvHelper.parseDiscountCSV(file);
            allDiscounts.addAll(discounts);

            // Organize by store and date
            for (Discount discount : discounts) {
                String store = discount.getStore();
                LocalDate date = discount.getDiscountDate();

                discountsByStoreAndDate
                        .computeIfAbsent(store, k -> new HashMap<>())
                        .computeIfAbsent(date, k -> new ArrayList<>())
                        .add(discount);
            }
        }
        log.info("Loaded {} discounts from {} files", allDiscounts.size(), discountFiles.size());
    }

    private void applyDiscountsToProducts() {
        for (Product product : allProducts) {
            String store = product.getStore();
            LocalDate date = product.getDate();
            String productId = product.getProductId();

            // Find applicable discounts for this product
            List<Discount> applicableDiscounts = discountsByStoreAndDate
                    .getOrDefault(store, new HashMap<>())
                    .getOrDefault(date, new ArrayList<>())
                    .stream()
                    .filter(discount -> discount.getProductId().equals(productId) && discount.isActiveOn(date))
                    .collect(Collectors.toList());

            // Apply highest discount if multiple exist
            if (!applicableDiscounts.isEmpty()) {
                Discount highestDiscount = applicableDiscounts.stream()
                        .max((d1, d2) -> d1.getPercentageDiscount().compareTo(d2.getPercentageDiscount()))
                        .orElse(null);

                if (highestDiscount != null) {
                    product.setDiscountPercentage(highestDiscount.getPercentageDiscount());
                    product.setDiscountedPrice(product.getDiscountedPriceValue());
                }
            }
        }
        log.info("Applied discounts to products");
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(allProducts);
    }

    public List<Discount> getAllDiscounts() {
        return new ArrayList<>(allDiscounts);
    }

    public List<Product> getProductsByStore(String store) {
        return allProducts.stream()
                .filter(product -> product.getStore().equalsIgnoreCase(store))
                .collect(Collectors.toList());
    }

    public List<Product> getProductsByStoreAndDate(String store, LocalDate date) {
        return productsByStoreAndDate
                .getOrDefault(store, new HashMap<>())
                .getOrDefault(date, new ArrayList<>());
    }

    public List<Product> getProductsByCategory(String category) {
        return allProducts.stream()
                .filter(product -> product.getProductCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    public List<Discount> getDiscountsByStore(String store) {
        return allDiscounts.stream()
                .filter(discount -> discount.getStore().equalsIgnoreCase(store))
                .collect(Collectors.toList());
    }
}
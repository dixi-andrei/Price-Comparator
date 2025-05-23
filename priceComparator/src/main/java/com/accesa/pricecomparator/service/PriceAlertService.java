package com.accesa.pricecomparator.service;

import com.accesa.pricecomparator.model.PriceAlert;
import com.accesa.pricecomparator.model.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PriceAlertService {

    private final CSVService csvService;

    // In-memory storage for alerts (in a real app, this would be in a database)
    private final Map<Long, PriceAlert> alerts = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    /**
     * Create a new price alert
     * @param productId Product ID to monitor
     * @param store Store to monitor (optional, can be null to check all stores)
     * @param targetPrice Target price to trigger alert
     * @param userId User ID the alert belongs to
     * @return Created alert
     */
    public PriceAlert createAlert(String productId, String store, Double targetPrice, String userId) {
        // Find product to get name
        String productName = csvService.getAllProducts().stream()
                .filter(p -> p.getProductId().equals(productId))
                .findFirst()
                .map(Product::getProductName)
                .orElse("Unknown Product");

        PriceAlert alert = new PriceAlert(productId, productName, store, targetPrice, userId);
        Long id = idCounter.getAndIncrement();
        alert.setId(id);

        alerts.put(id, alert);
        log.info("Created price alert: {}", alert);

        // Check if alert should be triggered immediately
        checkAlert(alert);

        return alert;
    }

    /**
     * Get all alerts
     * @param userId User ID (optional)
     * @param activeOnly Whether to return only active alerts
     * @return List of alerts
     */
    public List<PriceAlert> getAlerts(String userId, boolean activeOnly) {
        return alerts.values().stream()
                .filter(alert -> userId == null || alert.getUserId().equals(userId))
                .filter(alert -> !activeOnly || alert.isActive())
                .collect(Collectors.toList());
    }

    /**
     * Get a specific alert by ID
     * @param id Alert ID
     * @return Alert or null if not found
     */
    public PriceAlert getAlert(Long id) {
        return alerts.get(id);
    }

    /**
     * Delete an alert
     * @param id Alert ID
     * @return true if deleted, false if not found
     */
    public boolean deleteAlert(Long id) {
        return alerts.remove(id) != null;
    }

    /**
     * Check if any alerts should be triggered based on current prices
     * @return List of triggered alerts
     */
    public List<PriceAlert> checkAllAlerts() {
        List<PriceAlert> triggeredAlerts = new ArrayList<>();

        for (PriceAlert alert : alerts.values()) {
            if (alert.isActive() && checkAlert(alert)) {
                triggeredAlerts.add(alert);
            }
        }

        return triggeredAlerts;
    }

    /**
     * Check if a specific alert should be triggered
     * @param alert Alert to check
     * @return true if triggered, false otherwise
     */
    private boolean checkAlert(PriceAlert alert) {
        if (!alert.isActive()) {
            return false;
        }

        // Get current product prices
        List<Product> products = csvService.getAllProducts().stream()
                .filter(p -> p.getProductId().equals(alert.getProductId()))
                .filter(p -> alert.getStore() == null || p.getStore().equalsIgnoreCase(alert.getStore()))
                .collect(Collectors.toList());

        // Check if any product price is below target
        for (Product product : products) {
            Double currentPrice = product.getDiscountedPrice() != null ?
                    product.getDiscountedPrice() : product.getPrice();

            if (currentPrice <= alert.getTargetPrice()) {
                alert.setActive(false);
                alert.setTriggeredAt(LocalDateTime.now());
                log.info("Alert triggered: {} - Product {} is now {}",
                        alert.getId(), product.getProductName(), currentPrice);
                return true;
            }
        }

        return false;
    }
}
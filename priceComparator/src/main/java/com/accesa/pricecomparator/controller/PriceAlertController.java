package com.accesa.pricecomparator.controller;

import com.accesa.pricecomparator.model.PriceAlert;
import com.accesa.pricecomparator.service.PriceAlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class PriceAlertController {

    private final PriceAlertService alertService;

    @PostMapping
    public ResponseEntity<PriceAlert> createAlert(@RequestBody Map<String, Object> request) {
        String productId = (String) request.get("productId");
        String store = (String) request.get("store");
        Double targetPrice = Double.valueOf(request.get("targetPrice").toString());
        String userId = (String) request.getOrDefault("userId", "anonymous");

        PriceAlert alert = alertService.createAlert(productId, store, targetPrice, userId);
        return new ResponseEntity<>(alert, HttpStatus.CREATED);
    }

    @GetMapping
    public List<PriceAlert> getAlerts(
            @RequestParam(required = false) String userId,
            @RequestParam(defaultValue = "false") boolean activeOnly) {
        return alertService.getAlerts(userId, activeOnly);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PriceAlert> getAlert(@PathVariable Long id) {
        PriceAlert alert = alertService.getAlert(id);
        if (alert == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(alert, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlert(@PathVariable Long id) {
        boolean deleted = alertService.deleteAlert(id);
        if (!deleted) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/check")
    public List<PriceAlert> checkAlerts() {
        return alertService.checkAllAlerts();
    }
}
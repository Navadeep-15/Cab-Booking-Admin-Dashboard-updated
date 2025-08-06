package com.admindashboard.earnings;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/earnings")
public class EarningController {

    private final EarningService earningService;

    @Autowired
    public EarningController(EarningService earningService) {
        this.earningService = earningService;
    }

    @GetMapping
    public ResponseEntity<List<EarningDTO>> getAllEarnings() {
        return ResponseEntity.ok(earningService.getAllEarnings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EarningDTO> getEarningById(@PathVariable Long id) {
        return ResponseEntity.ok(earningService.getEarningById(id));
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<List<EarningDTO>> getEarningsByDriver(
            @PathVariable Long driverId) {
        return ResponseEntity.ok(earningService.getEarningsByDriver(driverId));
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<EarningDTO>> getEarningsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(earningService.getEarningsByDateRange(start, end));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<EarningDTO>> getEarningsByStatus(
            @PathVariable PaymentStatus status) {
        return ResponseEntity.ok(earningService.getEarningsByStatus(status));
    }
}
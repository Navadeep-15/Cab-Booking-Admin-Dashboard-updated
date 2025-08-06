package com.admindashboard.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin-logs")
public class AdminLogController {

    private final AdminLogService adminLogService;

    @Autowired
    public AdminLogController(AdminLogService adminLogService) {
        this.adminLogService = adminLogService;
    }

    // GET all logs
    @GetMapping
    public ResponseEntity<List<AdminLog>> getAllLogs() {
        return ResponseEntity.ok(adminLogService.getAllLogs());
    }

    // GET single log by ID
    @GetMapping("/{id}")
    public ResponseEntity<AdminLog> getLogById(@PathVariable Long id) {
        return ResponseEntity.ok(adminLogService.getLogById(id));
    }

    // POST new log
    @PostMapping
    public ResponseEntity<AdminLog> addLog(@RequestBody AdminLog log) {
        return ResponseEntity.ok(adminLogService.addLog(log));
    }
}
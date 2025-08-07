package com.admindashboard.reports;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<List<AdminLogDTO>> getAllLogs() {
        return ResponseEntity.ok(adminLogService.getAllLogs());
    }

    // GET single log by ID
    @GetMapping("/{id}")
    public ResponseEntity<AdminLogDTO> getLogById(@PathVariable Long id) {
        return ResponseEntity.ok(adminLogService.getLogById(id));
    }

    // POST new log with validation
    @PostMapping
    public ResponseEntity<AdminLogDTO> addLog(@Valid @RequestBody AdminLogDTO logDTO) {
        return new ResponseEntity<>(adminLogService.addLog(logDTO), HttpStatus.CREATED);
    }
}

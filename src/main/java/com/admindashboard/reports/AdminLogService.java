package com.admindashboard.reports;

import com.admindashboard.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AdminLogService {

    private final AdminLogRepository adminLogRepository;

    @Autowired
    public AdminLogService(AdminLogRepository adminLogRepository) {
        this.adminLogRepository = adminLogRepository;
    }

    public List<AdminLog> getAllLogs() {
        return adminLogRepository.findAllByOrderByTimestampDesc();
    }

    public AdminLog getLogById(Long id) {
        return adminLogRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("AdminLog", id));
    }

    public AdminLog addLog(AdminLog log) {
        return adminLogRepository.save(log);
    }
}
package com.admindashboard.reports;

import com.admindashboard.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminLogService {

    private final AdminLogRepository adminLogRepository;

    @Autowired
    public AdminLogService(AdminLogRepository adminLogRepository) {
        this.adminLogRepository = adminLogRepository;
    }
    
    // Method to get all logs and convert them to DTOs
    @Transactional(readOnly = true)
    public List<AdminLogDTO> getAllLogs() {
        return adminLogRepository.findAllByOrderByTimestampDesc().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Method to get a single log by ID and convert it to a DTO
    @Transactional(readOnly = true)
    public AdminLogDTO getLogById(Long id) {
        return adminLogRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new EntityNotFoundException("AdminLog", id));
    }
    
    // Method to add a log using a DTO
    @Transactional
    public AdminLogDTO addLog(AdminLogDTO logDTO) {
        AdminLog log = convertToEntity(logDTO);
        AdminLog savedLog = adminLogRepository.save(log);
        return convertToDto(savedLog);
    }

    // Converts an AdminLog entity to an AdminLogDTO
    private AdminLogDTO convertToDto(AdminLog log) {
        AdminLogDTO dto = new AdminLogDTO();
        dto.setLogId(log.getLogId());
        dto.setAction(log.getAction());
        dto.setAdminName(log.getAdminName());
        dto.setTimestamp(log.getTimestamp());
        return dto;
    }
    
    // Converts an AdminLogDTO to an AdminLog entity
    private AdminLog convertToEntity(AdminLogDTO dto) {
        AdminLog log = new AdminLog();
        log.setLogId(dto.getLogId());
        log.setAction(dto.getAction());
        log.setAdminName(dto.getAdminName());
        log.setTimestamp(dto.getTimestamp());
        return log;
    }
}

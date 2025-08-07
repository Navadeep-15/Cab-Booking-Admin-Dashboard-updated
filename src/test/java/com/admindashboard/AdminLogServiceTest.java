package com.admindashboard;

import com.admindashboard.exception.*;
import com.admindashboard.reports.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminLogServiceTest {

    @Mock
    private AdminLogRepository adminLogRepository;

    @InjectMocks
    private AdminLogService adminLogService;

    private AdminLog adminLog;
    private AdminLogDTO adminLogDTO;

    @BeforeEach
    void setUp() {
        adminLog = new AdminLog();
        adminLog.setLogId(1L);
        adminLog.setAction("User Created");
        adminLog.setAdminName("AdminUser");
        adminLog.setTimestamp(LocalDateTime.now());

        adminLogDTO = new AdminLogDTO();
        adminLogDTO.setAction("User Deleted");
        adminLogDTO.setAdminName("AdminUser");
        adminLogDTO.setTimestamp(LocalDateTime.now());
    }

    @Test
    void testGetAllLogsSuccess() {
        when(adminLogRepository.findAllByOrderByTimestampDesc()).thenReturn(Arrays.asList(adminLog));

        List<AdminLogDTO> logs = adminLogService.getAllLogs();

        assertFalse(logs.isEmpty());
        assertEquals(1, logs.size());
        assertEquals(adminLog.getAction(), logs.get(0).getAction());
        verify(adminLogRepository, times(1)).findAllByOrderByTimestampDesc();
    }

    @Test
    void testGetLogByIdSuccess() {
        when(adminLogRepository.findById(1L)).thenReturn(Optional.of(adminLog));

        AdminLogDTO foundLog = adminLogService.getLogById(1L);

        assertNotNull(foundLog);
        assertEquals(adminLog.getLogId(), foundLog.getLogId());
        verify(adminLogRepository, times(1)).findById(1L);
    }

    @Test
    void testGetLogByIdNotFound() {
        when(adminLogRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> adminLogService.getLogById(99L));
        verify(adminLogRepository, times(1)).findById(99L);
    }

    @Test
    void testAddLogSuccess() {
        when(adminLogRepository.save(any(AdminLog.class))).thenReturn(adminLog);

        AdminLogDTO createdLog = adminLogService.addLog(adminLogDTO);

        assertNotNull(createdLog);
        assertEquals(adminLogDTO.getAction(), createdLog.getAction());
        verify(adminLogRepository, times(1)).save(any(AdminLog.class));
    }
}

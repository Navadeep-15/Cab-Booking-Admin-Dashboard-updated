package com.admindashboard;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.admindashboard.exception.EntityNotFoundException;
import com.admindashboard.reports.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminLogController.class)
class AdminLogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminLogService adminLogService;

    @Autowired
    private ObjectMapper objectMapper;

    private AdminLogDTO adminLogDTO;
    private AdminLogDTO responseAdminLogDTO;

    @BeforeEach
    void setUp() {
        adminLogDTO = new AdminLogDTO();
        adminLogDTO.setAction("Login Attempt");
        adminLogDTO.setAdminName("TestAdmin");
        adminLogDTO.setTimestamp(LocalDateTime.now());

        responseAdminLogDTO = new AdminLogDTO();
        responseAdminLogDTO.setLogId(1L);
        responseAdminLogDTO.setAction("Login Attempt");
        responseAdminLogDTO.setAdminName("TestAdmin");
        responseAdminLogDTO.setTimestamp(LocalDateTime.now());
    }

    @Test
    void testGetAllLogsSuccess() throws Exception {
        List<AdminLogDTO> logs = Arrays.asList(responseAdminLogDTO);
        when(adminLogService.getAllLogs()).thenReturn(logs);

        mockMvc.perform(get("/api/admin-logs")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].action").value("Login Attempt"));
    }

    @Test
    void testGetLogByIdSuccess() throws Exception {
        when(adminLogService.getLogById(1L)).thenReturn(responseAdminLogDTO);

        mockMvc.perform(get("/api/admin-logs/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.logId").value(1L))
                .andExpect(jsonPath("$.adminName").value("TestAdmin"));
    }

    @Test
    void testGetLogByIdNotFound() throws Exception {
        when(adminLogService.getLogById(anyLong())).thenThrow(new EntityNotFoundException("AdminLog", 99L));

        mockMvc.perform(get("/api/admin-logs/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testAddLogSuccess() throws Exception {
        when(adminLogService.addLog(any(AdminLogDTO.class))).thenReturn(responseAdminLogDTO);

        mockMvc.perform(post("/api/admin-logs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminLogDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.action").value("Login Attempt"))
                .andExpect(jsonPath("$.adminName").value("TestAdmin"));
    }

    @Test
    void testAddLogValidationFailure() throws Exception {
        adminLogDTO.setAction(""); // Blank action to trigger validation error

        mockMvc.perform(post("/api/admin-logs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminLogDTO)))
                .andExpect(status().isBadRequest());
    }
}

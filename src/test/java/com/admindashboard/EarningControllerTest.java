package com.admindashboard;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.admindashboard.earnings.*;
import com.admindashboard.enums.PaymentMethod;
import com.admindashboard.enums.PaymentStatus;
import com.admindashboard.exception.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EarningController.class)
class EarningControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EarningService earningService;

    @Autowired
    private ObjectMapper objectMapper;

    private EarningDTO earningDTO;
    private EarningDTO responseEarningDTO;

    @BeforeEach
    void setUp() {
        earningDTO = new EarningDTO();
        earningDTO.setDriverId(1L);
        earningDTO.setDriverName("Test Driver");
        earningDTO.setAmount(BigDecimal.valueOf(100.00));
        earningDTO.setTransactionDate(LocalDateTime.now());
        earningDTO.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        earningDTO.setPaymentStatus(PaymentStatus.PAID);

        responseEarningDTO = new EarningDTO();
        responseEarningDTO.setId(1L);
        responseEarningDTO.setDriverId(1L);
        responseEarningDTO.setDriverName("Test Driver");
        responseEarningDTO.setAmount(BigDecimal.valueOf(100.00));
        responseEarningDTO.setTransactionDate(LocalDateTime.now());
        responseEarningDTO.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        responseEarningDTO.setPaymentStatus(PaymentStatus.PAID);
    }

    @Test
    void testCreateEarningSuccess() throws Exception {
        when(earningService.createEarning(any(EarningDTO.class))).thenReturn(responseEarningDTO);

        mockMvc.perform(post("/api/earnings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(earningDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.amount").value(100.00));
    }

    @Test
    void testCreateEarningValidationFailure() throws Exception {
        earningDTO.setAmount(BigDecimal.valueOf(0.00)); // Invalid amount

        mockMvc.perform(post("/api/earnings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(earningDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllEarningsSuccess() throws Exception {
        List<EarningDTO> earnings = Arrays.asList(responseEarningDTO);
        when(earningService.getAllEarnings()).thenReturn(earnings);

        mockMvc.perform(get("/api/earnings")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void testGetEarningByIdSuccess() throws Exception {
        when(earningService.getEarningById(1L)).thenReturn(responseEarningDTO);

        mockMvc.perform(get("/api/earnings/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testGetEarningByIdNotFound() throws Exception {
        when(earningService.getEarningById(anyLong())).thenThrow(new EntityNotFoundException("Earning", 99L));

        mockMvc.perform(get("/api/earnings/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetEarningsByDriverSuccess() throws Exception {
        List<EarningDTO> earnings = Arrays.asList(responseEarningDTO);
        when(earningService.getEarningsByDriver(1L)).thenReturn(earnings);

        mockMvc.perform(get("/api/earnings/driver/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].driverId").value(1L));
    }

    @Test
    void testGetEarningsByDriverNotFound() throws Exception {
        when(earningService.getEarningsByDriver(anyLong())).thenThrow(new EntityNotFoundException("Driver", 99L));

        mockMvc.perform(get("/api/earnings/driver/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetEarningsByDateRangeSuccess() throws Exception {
        List<EarningDTO> earnings = Arrays.asList(responseEarningDTO);
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        when(earningService.getEarningsByDateRange(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(earnings);

        mockMvc.perform(get("/api/earnings/date-range")
                        .param("start", start.toString())
                        .param("end", end.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void testGetEarningsByDateRangeInvalidDates() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().minusDays(1);
        when(earningService.getEarningsByDateRange(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenThrow(new IllegalArgumentException("Start date must be before end date"));

        mockMvc.perform(get("/api/earnings/date-range")
                        .param("start", start.toString())
                        .param("end", end.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetEarningsByStatusSuccess() throws Exception {
        List<EarningDTO> earnings = Arrays.asList(responseEarningDTO);
        when(earningService.getEarningsByStatus(PaymentStatus.PAID)).thenReturn(earnings);

        mockMvc.perform(get("/api/earnings/status/PAID")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].paymentStatus").value("PAID"));
    }
}

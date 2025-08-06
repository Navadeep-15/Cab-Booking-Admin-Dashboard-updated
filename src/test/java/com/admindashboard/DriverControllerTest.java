package com.admindashboard;

import com.admindashboard.driververification.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DriverController.class)
public class DriverControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DriverService driverService;

    @Autowired
    private ObjectMapper objectMapper;

    private DriverDTO driverDTO;
    private DriverResponseDTO driverResponseDTO;

    @BeforeEach
    void setUp() {
        driverDTO = new DriverDTO();
        driverDTO.setLicenseNumber("LIC123");
        driverDTO.setVehicleType("Car");
        driverDTO.setLicenseExpiryDate(Date.valueOf("2025-12-31"));
        driverDTO.setUserId(1L);

        driverResponseDTO = new DriverResponseDTO();
        driverResponseDTO.setDriverId(1L);
        driverResponseDTO.setUserId(1L);
        driverResponseDTO.setLicenseNumber("LIC123");
        driverResponseDTO.setVehicleType("Car");
        driverResponseDTO.setLicenseExpiryDate(Date.valueOf("2025-12-31"));
        driverResponseDTO.setVerificationStatus(DriverVerificationStatus.PENDING);
        driverResponseDTO.setFirstName("John");
        driverResponseDTO.setLastName("Doe");
    }

    @Test
    void testRegisterDriver() throws Exception {
        when(driverService.registerDriver(any(DriverDTO.class))).thenReturn(driverResponseDTO);

        mockMvc.perform(post("/api/drivers/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(driverDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.licenseNumber").value(driverDTO.getLicenseNumber()));
    }
    
    @Test
    void testGetAllDrivers() throws Exception {
        when(driverService.getAllDrivers()).thenReturn(List.of(driverResponseDTO));

        mockMvc.perform(get("/api/drivers")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].licenseNumber").value(driverResponseDTO.getLicenseNumber()));
    }

    @Test
    void testGetDriverById() throws Exception {
        when(driverService.getDriverById(1L)).thenReturn(driverResponseDTO);

        mockMvc.perform(get("/api/drivers/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.licenseNumber").value(driverResponseDTO.getLicenseNumber()));
    }

 // Example fix for the test method
    @Test
    void testUpdateDriver_Success() throws Exception {
        // Arrange
    	Long driverId = 1L;
        DriverDTO updateDTO = new DriverDTO();
        updateDTO.setVehicleType("Truck");
        updateDTO.setUserId(1L);
        updateDTO.setLicenseNumber("LIC123"); // <-- Mandatory field
        updateDTO.setLicenseExpiryDate(Date.valueOf("2026-12-31"));
        // Mock the service call
        DriverResponseDTO responseDTO = new DriverResponseDTO();
        responseDTO.setVehicleType("Truck");

        when(driverService.updateDriver(eq(driverId), any(DriverDTO.class))).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(put("/api/drivers/{id}", driverId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vehicleType").value("Truck"));

        verify(driverService, times(1)).updateDriver(eq(driverId), any(DriverDTO.class));
    }

    @Test
    void testDeleteDriver() throws Exception {
        doNothing().when(driverService).deleteDriver(1L);

        mockMvc.perform(delete("/api/drivers/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void testVerifyDriver() throws Exception {
        DriverResponseDTO verifiedDriver = new DriverResponseDTO();
        verifiedDriver.setVerificationStatus(DriverVerificationStatus.VERIFIED);
        when(driverService.updateDriverStatus(1L, DriverVerificationStatus.VERIFIED)).thenReturn(verifiedDriver);

        mockMvc.perform(put("/api/drivers/1/verify")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verificationStatus").value("VERIFIED"));
    }

    @Test
    void testRejectDriver() throws Exception {
        DriverResponseDTO rejectedDriver = new DriverResponseDTO();
        rejectedDriver.setVerificationStatus(DriverVerificationStatus.REJECTED);
        when(driverService.updateDriverStatus(1L, DriverVerificationStatus.REJECTED)).thenReturn(rejectedDriver);

        mockMvc.perform(put("/api/drivers/1/reject")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verificationStatus").value("REJECTED"));
    }
}
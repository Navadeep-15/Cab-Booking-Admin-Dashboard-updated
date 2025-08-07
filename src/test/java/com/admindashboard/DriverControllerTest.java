package com.admindashboard;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.admindashboard.driververification.*;
import com.admindashboard.enums.DriverStatus;
import com.admindashboard.enums.VehicleType;
import com.admindashboard.exception.DuplicateEntityException;
import com.admindashboard.exception.EntityNotFoundException;
import com.admindashboard.exception.UnauthorizedAccessException; // Import the new exception
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DriverController.class)
class DriverControllerTest {

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
        // Initialize DriverDTO with all schema fields
        driverDTO = new DriverDTO();
        driverDTO.setUserId(1L);
        driverDTO.setLicenseNumber("LIC12345");
        driverDTO.setVehicleType(VehicleType.SEDAN);
        driverDTO.setLicenseExpiryDate(new Date(System.currentTimeMillis() + 86400000));
        driverDTO.setCurrentLatitude(34.0522);
        driverDTO.setCurrentLongitude(-118.2437);
        driverDTO.setIsAvailable(true);
        driverDTO.setIsOnline(true);
        driverDTO.setLicenseImage("http://example.com/lic_drv123.jpg");
        driverDTO.setRating(4.5);
        driverDTO.setTotalEarnings(1500.75);
        driverDTO.setTotalRides(50);
        driverDTO.setVehicleColor("Blue");
        driverDTO.setVehicleImage("http://example.com/car_drv123.jpg");
        driverDTO.setVehicleModel("Honda Civic");
        driverDTO.setVehicleNumber("CA123XYZ");


        // Initialize DriverResponseDTO with all schema fields
        driverResponseDTO = new DriverResponseDTO();
        driverResponseDTO.setDriverId(1L);
        driverResponseDTO.setUserId(1L);
        driverResponseDTO.setLicenseNumber("LIC12345");
        driverResponseDTO.setVehicleType(VehicleType.SEDAN);
        driverResponseDTO.setLicenseExpiryDate(new Date(System.currentTimeMillis() + 86400000));
        driverResponseDTO.setVerificationStatus(DriverStatus.PENDING);
        driverResponseDTO.setFirstName("John");
        driverResponseDTO.setLastName("Doe");
        driverResponseDTO.setEmail("john.doe@test.com");
        driverResponseDTO.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
        driverResponseDTO.setCurrentLatitude(34.0522);
        driverResponseDTO.setCurrentLongitude(-118.2437);
        driverResponseDTO.setIsAvailable(true);
        driverResponseDTO.setIsOnline(true);
        driverResponseDTO.setLicenseImage("http://example.com/license.jpg");
        driverResponseDTO.setRating(4.5);
        driverResponseDTO.setTotalEarnings(1500.75);
        driverResponseDTO.setTotalRides(50);
        driverResponseDTO.setUpdatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
        driverResponseDTO.setVehicleColor("Black");
        driverResponseDTO.setVehicleImage("http://example.com/vehicle.jpg");
        driverResponseDTO.setVehicleModel("Camry");
        driverResponseDTO.setVehicleNumber("XYZ789");
    }

    @Test
    void testRegisterDriverSuccess() throws Exception {
        when(driverService.registerDriver(any(DriverDTO.class))).thenReturn(driverResponseDTO);

        mockMvc.perform(post("/api/drivers/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(driverDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.licenseNumber").value("LIC12345"))
                .andExpect(jsonPath("$.verificationStatus").value("PENDING"))
                .andExpect(jsonPath("$.vehicleType").value("SEDAN")) // Assert new field
                .andExpect(jsonPath("$.rating").value(4.5)) // Assert new field
                .andExpect(jsonPath("$.vehicleNumber").value("XYZ789")); // Assert new field
    }

    @Test
    void testRegisterDriverWithDuplicateLicenseNumber() throws Exception {
        doThrow(new DuplicateEntityException("Driver", "license number", driverDTO.getLicenseNumber()))
                .when(driverService).registerDriver(any(DriverDTO.class));

        mockMvc.perform(post("/api/drivers/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(driverDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    void testGetAllDriversSuccess() throws Exception {
        List<DriverResponseDTO> driverList = List.of(driverResponseDTO);
        when(driverService.getAllDrivers()).thenReturn(driverList);

        mockMvc.perform(get("/api/drivers")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].licenseNumber").value("LIC12345"))
                .andExpect(jsonPath("$[0].vehicleType").value("SEDAN"))
                .andExpect(jsonPath("$[0].rating").value(4.5));
    }

    @Test
    void testGetDriverByIdSuccess() throws Exception {
        when(driverService.getDriverById(1L)).thenReturn(driverResponseDTO);

        mockMvc.perform(get("/api/drivers/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.driverId").value(1L))
                .andExpect(jsonPath("$.licenseNumber").value("LIC12345"))
                .andExpect(jsonPath("$.vehicleNumber").value("XYZ789"));
    }

    @Test
    void testGetDriverByIdNotFound() throws Exception {
        when(driverService.getDriverById(99L)).thenThrow(new EntityNotFoundException("Driver", 99L));

        mockMvc.perform(get("/api/drivers/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateDriverSuccess() throws Exception {
        DriverDTO updateDTO = new DriverDTO();
        updateDTO.setVehicleType(VehicleType.SUV);
        updateDTO.setIsOnline(false);
        updateDTO.setRating(4.8);
        updateDTO.setVehicleColor("White");
        updateDTO.setLicenseNumber("LIC12345"); // Keep same license number to avoid duplicate check
        updateDTO.setLicenseExpiryDate(new Date(System.currentTimeMillis() + 86400000));
        updateDTO.setUserId(1L); // Matches existing user ID
        updateDTO.setVehicleNumber("XYZ789"); // Mandatory field


        DriverResponseDTO updatedResponse = new DriverResponseDTO();
        updatedResponse.setDriverId(1L);
        updatedResponse.setLicenseNumber("LIC12345");
        updatedResponse.setVehicleType(VehicleType.SUV);
        updatedResponse.setIsOnline(false);
        updatedResponse.setRating(4.8);
        updatedResponse.setVehicleColor("White");
        updatedResponse.setVehicleNumber("XYZ789");


        when(driverService.updateDriver(eq(1L), any(DriverDTO.class))).thenReturn(updatedResponse);

        mockMvc.perform(put("/api/drivers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vehicleType").value("SUV"))
                .andExpect(jsonPath("$.isOnline").value(false))
                .andExpect(jsonPath("$.rating").value(4.8))
                .andExpect(jsonPath("$.vehicleColor").value("White"));
    }

    @Test
    void testUpdateDriverNotFound() throws Exception {
        DriverDTO updateDTO = new DriverDTO();
        updateDTO.setVehicleType(VehicleType.SUV);
        updateDTO.setLicenseNumber("LIC12345");
        updateDTO.setLicenseExpiryDate(new Date(System.currentTimeMillis() + 86400000));
        updateDTO.setUserId(1L);
        updateDTO.setVehicleNumber("XYZ789");

        doThrow(new EntityNotFoundException("Driver", 99L)).when(driverService).updateDriver(eq(99L), any(DriverDTO.class));

        mockMvc.perform(put("/api/drivers/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateDriverWithMismatchedUserId() throws Exception {
        DriverDTO updateDTO = new DriverDTO();
        updateDTO.setUserId(99L); // Mismatched user ID
        updateDTO.setLicenseNumber("LIC12345");
        updateDTO.setVehicleType(VehicleType.SEDAN);
        updateDTO.setLicenseExpiryDate(new Date(System.currentTimeMillis() + 86400000));
        updateDTO.setVehicleNumber("XYZ789");

        doThrow(new UnauthorizedAccessException("Cannot change the user associated with a driver profile.", "DRIVER_AUTH_001"))
                .when(driverService).updateDriver(eq(1L), any(DriverDTO.class));

        mockMvc.perform(put("/api/drivers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isForbidden()); // Expect 403 Forbidden
    }

    @Test
    void testDeleteDriverSuccess() throws Exception {
        doNothing().when(driverService).deleteDriver(1L);

        mockMvc.perform(delete("/api/drivers/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteDriverNotFound() throws Exception {
        doThrow(new EntityNotFoundException("Driver", 99L)).when(driverService).deleteDriver(99L);

        mockMvc.perform(delete("/api/drivers/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testVerifyDriver() throws Exception {
        DriverResponseDTO verifiedDriver = new DriverResponseDTO();
        verifiedDriver.setVerificationStatus(DriverStatus.VERIFIED); // Changed to VERIFIED
        when(driverService.updateDriverStatus(1L, DriverStatus.VERIFIED)).thenReturn(verifiedDriver); // Changed to VERIFIED

        mockMvc.perform(put("/api/drivers/1/verify")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verificationStatus").value("VERIFIED")); // Changed to VERIFIED
    }

    @Test
    void testRejectDriver() throws Exception {
        DriverResponseDTO rejectedDriver = new DriverResponseDTO();
        rejectedDriver.setVerificationStatus(DriverStatus.REJECTED);
        when(driverService.updateDriverStatus(1L, DriverStatus.REJECTED)).thenReturn(rejectedDriver);

        mockMvc.perform(put("/api/drivers/1/reject")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verificationStatus").value("REJECTED"));
    }
}

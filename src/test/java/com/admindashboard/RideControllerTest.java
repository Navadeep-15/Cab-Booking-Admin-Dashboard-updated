package com.admindashboard;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.admindashboard.enums.PaymentMethod;
import com.admindashboard.enums.PaymentStatus;
import com.admindashboard.enums.RideStatus;
import com.admindashboard.enums.VehicleType;
import com.admindashboard.exception.EntityNotFoundException;
import com.admindashboard.ridelogs.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RideController.class)
class RideControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RideService rideService;

    @Autowired
    private ObjectMapper objectMapper;

    private RideDTO rideDTO; // This will be used for both request body and mock service response

    @BeforeEach
    void setUp() {
        rideDTO = new RideDTO();
        rideDTO.setId(1L); // Set ID for expected response DTO
        rideDTO.setPassengerId(1L);
        rideDTO.setDriverId(2L);
        rideDTO.setVehicleType(VehicleType.SEDAN);
        rideDTO.setPickupAddress("123 Main St, City");
        rideDTO.setPickupLatitude(34.0);
        rideDTO.setPickupLongitude(-118.0);
        rideDTO.setDropoffAddress("456 Oak Ave, City");
        rideDTO.setDropoffLatitude(34.1);
        rideDTO.setDropoffLongitude(-118.1);
        rideDTO.setStatus(RideStatus.REQUESTED);
        rideDTO.setActualFare(25.50);
        rideDTO.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        rideDTO.setPaymentStatus(PaymentStatus.COMPLETED);
        rideDTO.setCustomerRating(4);
        rideDTO.setCustomerFeedback("Good experience");
        rideDTO.setDriverRating(5);
        rideDTO.setDriverFeedback("Very polite");
    }

    @Test
    void testCreateRideSuccess() throws Exception {
        when(rideService.createRide(any(RideDTO.class))).thenReturn(rideDTO); // Service returns DTO

        mockMvc.perform(post("/api/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rideDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L)) // Assert DTO properties
                .andExpect(jsonPath("$.pickupAddress").value("123 Main St, City"))
                .andExpect(jsonPath("$.dropoffAddress").value("456 Oak Ave, City"));
    }

    @Test
    void testCreateRideValidationFailure() throws Exception {
        rideDTO.setDropoffAddress(null); // Invalid DTO

        mockMvc.perform(post("/api/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rideDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllRidesSuccess() throws Exception {
        List<RideDTO> rides = Arrays.asList(rideDTO); // Service returns List<DTO>
        when(rideService.getAllRides()).thenReturn(rides);

        mockMvc.perform(get("/api/rides")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void testGetRideByIdSuccess() throws Exception {
        when(rideService.getRideById(1L)).thenReturn(rideDTO); // Service returns DTO

        mockMvc.perform(get("/api/rides/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testGetRideByIdNotFound() throws Exception {
        when(rideService.getRideById(anyLong())).thenThrow(new EntityNotFoundException("Ride", 99L));

        mockMvc.perform(get("/api/rides/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetRidesByDriverIdSuccess() throws Exception {
        List<RideDTO> rides = Arrays.asList(rideDTO); // Service returns List<DTO>
        when(rideService.getRidesByDriverId(2L)).thenReturn(rides);

        mockMvc.perform(get("/api/rides/driver/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].driverId").value(2L));
    }

    @Test
    void testGetRidesByDriverIdNotFound() throws Exception {
        when(rideService.getRidesByDriverId(anyLong())).thenThrow(new EntityNotFoundException("Rides for Driver ID", 99L));

        mockMvc.perform(get("/api/rides/driver/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetRidesByPassengerIdSuccess() throws Exception {
        List<RideDTO> rides = Arrays.asList(rideDTO); // Service returns List<DTO>
        when(rideService.getRidesByPassengerId(1L)).thenReturn(rides);

        mockMvc.perform(get("/api/rides/passenger/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].passengerId").value(1L));
    }

    @Test
    void testGetRidesByPassengerIdNotFound() throws Exception {
        when(rideService.getRidesByPassengerId(anyLong())).thenReturn(List.of());

        mockMvc.perform(get("/api/rides/passenger/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetRideHistoryForPassengerSuccess() throws Exception {
        List<RideDTO> rides = Arrays.asList(rideDTO); // Service returns List<DTO>
        when(rideService.getRideHistoryForPassenger(1L)).thenReturn(rides);

        mockMvc.perform(get("/api/rides/history/passenger/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void testGetRideHistoryForDriverSuccess() throws Exception {
        List<RideDTO> rides = Arrays.asList(rideDTO); // Service returns List<DTO>
        when(rideService.getRideHistoryForDriver(2L)).thenReturn(rides);

        mockMvc.perform(get("/api/rides/history/driver/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void testGetCancelledRidesSuccess() throws Exception {
        List<RideDTO> rides = Arrays.asList(rideDTO); // Service returns List<DTO>
        when(rideService.getCancelledRides()).thenReturn(rides);

        mockMvc.perform(get("/api/rides/cancelled")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void testGetRidesWithComplaintsSuccess() throws Exception {
        List<RideDTO> rides = Arrays.asList(rideDTO); // Service returns List<DTO>
        when(rideService.getRidesWithComplaints()).thenReturn(rides);

        mockMvc.perform(get("/api/rides/complaints")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void testGetRidesWithFeedbackSuccess() throws Exception {
        List<RideDTO> rides = Arrays.asList(rideDTO); // Service returns List<DTO>
        when(rideService.getRidesWithFeedback()).thenReturn(rides);

        mockMvc.perform(get("/api/rides/feedback")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void testUpdateRideSuccess() throws Exception {
        RideDTO updateDTO = new RideDTO();
        updateDTO.setPickupAddress("Updated Pickup");
        updateDTO.setPassengerId(1L);
        updateDTO.setDriverId(2L);
        updateDTO.setVehicleType(VehicleType.SUV);
        updateDTO.setPickupLatitude(34.0);
        updateDTO.setPickupLongitude(-118.0);
        updateDTO.setDropoffAddress("New Dropoff Address");
        updateDTO.setDropoffLatitude(34.1);
        updateDTO.setDropoffLongitude(-118.1);
        updateDTO.setStatus(RideStatus.COMPLETED);
        updateDTO.setActualFare(30.00);
        updateDTO.setPaymentMethod(PaymentMethod.WALLET);
        updateDTO.setPaymentStatus(PaymentStatus.COMPLETED);

        RideDTO updatedRideDTO = new RideDTO();
        updatedRideDTO.setId(1L);
        updatedRideDTO.setPickupAddress("Updated Pickup");
        updatedRideDTO.setDropoffAddress("New Dropoff Address");

        when(rideService.updateRide(eq(1L), any(RideDTO.class))).thenReturn(updatedRideDTO); // Service returns DTO

        mockMvc.perform(put("/api/rides/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pickupAddress").value("Updated Pickup"))
                .andExpect(jsonPath("$.dropoffAddress").value("New Dropoff Address"));
    }

    @Test
    void testUpdateRideNotFound() throws Exception {
        RideDTO updateDTO = new RideDTO();
        updateDTO.setPickupAddress("Updated Pickup");
        updateDTO.setPassengerId(1L);
        updateDTO.setDriverId(2L);
        updateDTO.setVehicleType(VehicleType.SEDAN);
        updateDTO.setPickupLatitude(34.0);
        updateDTO.setPickupLongitude(-118.0);
        updateDTO.setDropoffAddress("456 Oak Ave, City");
        updateDTO.setDropoffLatitude(34.1);
        updateDTO.setDropoffLongitude(-118.1);
        updateDTO.setStatus(RideStatus.REQUESTED);
        updateDTO.setActualFare(25.50);
        updateDTO.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        updateDTO.setPaymentStatus(PaymentStatus.COMPLETED);


        when(rideService.updateRide(eq(99L), any(RideDTO.class))).thenThrow(new EntityNotFoundException("Ride", 99L));

        mockMvc.perform(put("/api/rides/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rideDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteRideSuccess() throws Exception {
        doNothing().when(rideService).deleteRide(1L);

        mockMvc.perform(delete("/api/rides/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteRideNotFound() throws Exception {
        doThrow(new EntityNotFoundException("Ride", 99L)).when(rideService).deleteRide(99L);

        mockMvc.perform(delete("/api/rides/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}

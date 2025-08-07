package com.admindashboard;

import com.admindashboard.driververification.Driver;
import com.admindashboard.driververification.DriverRepository;
import com.admindashboard.enums.PaymentMethod;
import com.admindashboard.enums.PaymentStatus;
import com.admindashboard.enums.RideStatus;
import com.admindashboard.enums.UserType;
import com.admindashboard.enums.VehicleType;
import com.admindashboard.exception.EntityNotFoundException;
import com.admindashboard.ridelogs.*;
import com.admindashboard.usermanagement.User;
import com.admindashboard.usermanagement.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RideServiceTest {

    @Mock
    private RideRepository rideRepository;

    @Mock
    private UserService userService;

    @Mock
    private DriverRepository driverRepository;

    @InjectMocks
    private RideService rideService;

    private User passengerUser;
    private User driverUser;
    private Driver driverEntity;
    private Ride ride;
    private RideDTO rideDTO; // This will now also represent the expected response DTO

    @BeforeEach
    void setUp() {
        passengerUser = new User();
        passengerUser.setId(1L);
        passengerUser.setFirstName("John");
        passengerUser.setLastName("Doe");
        passengerUser.setEmail("john.doe@example.com");
        passengerUser.setUserType(UserType.PASSENGER);

        driverUser = new User();
        driverUser.setId(2L);
        driverUser.setFirstName("Jane");
        driverUser.setLastName("Smith");
        driverUser.setEmail("jane.smith@example.com");
        driverUser.setUserType(UserType.DRIVER);

        driverEntity = new Driver();
        driverEntity.setId(10L);
        driverEntity.setUser(driverUser);

        ride = new Ride();
        ride.setId(100L);
        ride.setPassenger(passengerUser);
        ride.setDriver(driverEntity);
        ride.setPickupAddress("Location A");
        ride.setPickupLatitude(10.0);
        ride.setPickupLongitude(20.0);
        ride.setDropoffAddress("Location B");
        ride.setDestinationLatitude(30.0);
        ride.setDestinationLongitude(40.0);
        ride.setStatus(RideStatus.REQUESTED);
        ride.setActualFare(25.50);
        ride.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        ride.setPaymentStatus(PaymentStatus.COMPLETED);
        ride.setCustomerFeedback("Good ride");
        ride.setCustomerRating(5);
        ride.setDriverFeedback("Polite driver");
        ride.setDriverRating(4);
        ride.setVehicleType(VehicleType.SEDAN);
        ride.setRequestedAt(new Timestamp(System.currentTimeMillis()));
        ride.setCreatedAt(new Timestamp(System.currentTimeMillis()));


        rideDTO = new RideDTO();
        rideDTO.setId(100L); // Set ID for expected response DTO
        rideDTO.setPassengerId(1L);
        rideDTO.setDriverId(10L);
        rideDTO.setPickupAddress("Location A");
        rideDTO.setPickupLatitude(10.0);
        rideDTO.setPickupLongitude(20.0);
        rideDTO.setDropoffAddress("Location B");
        rideDTO.setDropoffLatitude(30.0);
        rideDTO.setDropoffLongitude(40.0);
        rideDTO.setStatus(RideStatus.REQUESTED);
        rideDTO.setActualFare(25.50);
        rideDTO.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        rideDTO.setPaymentStatus(PaymentStatus.COMPLETED);
        rideDTO.setCustomerFeedback("Good ride");
        rideDTO.setCustomerRating(5);
        rideDTO.setDriverFeedback("Polite driver");
        rideDTO.setDriverRating(4);
        rideDTO.setVehicleType(VehicleType.SEDAN);
        rideDTO.setRequestedAt(ride.getRequestedAt());
        rideDTO.setCreatedAt(ride.getCreatedAt());
    }

    @Test
    void testCreateRideSuccess() {
        when(userService.getUserById(1L)).thenReturn(passengerUser);
        when(driverRepository.findById(10L)).thenReturn(Optional.of(driverEntity));
        when(rideRepository.save(any(Ride.class))).thenReturn(ride);

        RideDTO createdRideDTO = rideService.createRide(rideDTO); // Expect DTO

        assertNotNull(createdRideDTO);
        assertEquals(rideDTO.getId(), createdRideDTO.getId());
        assertEquals(rideDTO.getPickupAddress(), createdRideDTO.getPickupAddress());
        assertEquals(rideDTO.getDropoffAddress(), createdRideDTO.getDropoffAddress());
        assertEquals(rideDTO.getDropoffLatitude(), createdRideDTO.getDropoffLatitude());
        assertEquals(rideDTO.getDropoffLongitude(), createdRideDTO.getDropoffLongitude());
        assertEquals(rideDTO.getDriverId(), createdRideDTO.getDriverId()); // Assert driver ID on DTO
        verify(rideRepository, times(1)).save(any(Ride.class));
    }

    @Test
    void testCreateRidePassengerNotFound() {
        when(userService.getUserById(anyLong())).thenThrow(new EntityNotFoundException("User", 1L));

        assertThrows(EntityNotFoundException.class, () -> rideService.createRide(rideDTO));
        verify(rideRepository, never()).save(any(Ride.class));
    }

    @Test
    void testCreateRideDriverNotFound() {
        when(userService.getUserById(1L)).thenReturn(passengerUser);
        when(driverRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> rideService.createRide(rideDTO));
        verify(rideRepository, never()).save(any(Ride.class));
    }

    @Test
    void testGetAllRides() {
        when(rideRepository.findAll()).thenReturn(Arrays.asList(ride));

        List<RideDTO> rides = rideService.getAllRides(); // Expect List<DTO>

        assertFalse(rides.isEmpty());
        assertEquals(1, rides.size());
        assertEquals(rideDTO.getId(), rides.get(0).getId());
        verify(rideRepository, times(1)).findAll();
    }

    @Test
    void testGetRideByIdSuccess() {
        when(rideRepository.findById(100L)).thenReturn(Optional.of(ride));

        RideDTO foundRideDTO = rideService.getRideById(100L); // Expect DTO

        assertNotNull(foundRideDTO);
        assertEquals(rideDTO.getId(), foundRideDTO.getId());
        verify(rideRepository, times(1)).findById(100L);
    }

    @Test
    void testGetRideByIdNotFound() {
        when(rideRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> rideService.getRideById(999L));
        verify(rideRepository, times(1)).findById(999L);
    }

    @Test
    void testGetRidesByDriverIdSuccess() {
        when(rideRepository.findByDriverId(10L)).thenReturn(Arrays.asList(ride));

        List<RideDTO> rides = rideService.getRidesByDriverId(10L); // Expect List<DTO>

        assertFalse(rides.isEmpty());
        assertEquals(1, rides.size());
        assertEquals(rideDTO.getDriverId(), rides.get(0).getDriverId());
        verify(rideRepository, times(1)).findByDriverId(10L);
    }

    @Test
    void testGetRidesByDriverIdNotFound() {
        when(rideRepository.findByDriverId(anyLong())).thenReturn(List.of());

        assertThrows(EntityNotFoundException.class, () -> rideService.getRidesByDriverId(999L));
        verify(rideRepository, times(1)).findByDriverId(999L);
    }

    @Test
    void testGetRidesByPassengerIdSuccess() {
        when(rideRepository.findByPassengerId(1L)).thenReturn(Arrays.asList(ride));

        List<RideDTO> rides = rideService.getRidesByPassengerId(1L); // Expect List<DTO>

        assertFalse(rides.isEmpty());
        assertEquals(1, rides.size());
        assertEquals(rideDTO.getPassengerId(), rides.get(0).getPassengerId());
        verify(rideRepository, times(1)).findByPassengerId(1L);
    }

    @Test
    void testGetRidesByPassengerIdNotFound() {
        when(rideRepository.findByPassengerId(anyLong())).thenReturn(List.of());

        assertThrows(EntityNotFoundException.class, () -> rideService.getRidesByPassengerId(999L));
        verify(rideRepository, times(1)).findByPassengerId(999L);
    }

    @Test
    void testGetRideHistoryForPassenger() {
        when(rideRepository.findByPassengerIdAndStatus(1L, RideStatus.COMPLETED)).thenReturn(Arrays.asList(ride));

        List<RideDTO> rides = rideService.getRideHistoryForPassenger(1L); // Expect List<DTO>

        assertFalse(rides.isEmpty());
        assertEquals(1, rides.size());
        assertEquals(RideStatus.COMPLETED, rides.get(0).getStatus());
        verify(rideRepository, times(1)).findByPassengerIdAndStatus(1L, RideStatus.COMPLETED);
    }

    @Test
    void testGetRideHistoryForDriver() {
        when(rideRepository.findByDriverIdAndStatus(10L, RideStatus.COMPLETED)).thenReturn(Arrays.asList(ride));

        List<RideDTO> rides = rideService.getRideHistoryForDriver(10L); // Expect List<DTO>

        assertFalse(rides.isEmpty());
        assertEquals(1, rides.size());
        assertEquals(RideStatus.COMPLETED, rides.get(0).getStatus());
        verify(rideRepository, times(1)).findByDriverIdAndStatus(10L, RideStatus.COMPLETED);
    }

    @Test
    void testGetCancelledRides() {
        when(rideRepository.findByStatus(RideStatus.CANCELLED)).thenReturn(Arrays.asList(ride));

        List<RideDTO> rides = rideService.getCancelledRides(); // Expect List<DTO>

        assertFalse(rides.isEmpty());
        assertEquals(1, rides.size());
        assertEquals(RideStatus.CANCELLED, rides.get(0).getStatus());
        verify(rideRepository, times(1)).findByStatus(RideStatus.CANCELLED);
    }

    @Test
    void testGetRidesWithComplaints() {
        when(rideRepository.findByCustomerRatingLessThanOrCustomerFeedbackContainingIgnoreCase(3, "complaint"))
                .thenReturn(Arrays.asList(ride));

        List<RideDTO> rides = rideService.getRidesWithComplaints(); // Expect List<DTO>

        assertFalse(rides.isEmpty());
        assertEquals(1, rides.size());
        verify(rideRepository, times(1)).findByCustomerRatingLessThanOrCustomerFeedbackContainingIgnoreCase(3, "complaint");
    }

    @Test
    void testGetRidesWithFeedback() {
        when(rideRepository.findRidesWithAnyFeedback()).thenReturn(Arrays.asList(ride));

        List<RideDTO> rides = rideService.getRidesWithFeedback(); // Expect List<DTO>

        assertFalse(rides.isEmpty());
        assertEquals(1, rides.size());
        verify(rideRepository, times(1)).findRidesWithAnyFeedback();
    }

    @Test
    void testUpdateRideSuccess() {
        RideDTO updateDTO = new RideDTO();
        updateDTO.setPickupAddress("New Pickup");
        updateDTO.setStatus(RideStatus.COMPLETED);
        updateDTO.setActualFare(30.00);
        updateDTO.setCustomerFeedback("Great service!");
        updateDTO.setCustomerRating(5);
        updateDTO.setDriverFeedback("Excellent passenger!");
        updateDTO.setDriverRating(5);
        updateDTO.setVehicleType(VehicleType.SUV);
        updateDTO.setPassengerId(1L);
        updateDTO.setDriverId(10L);
        updateDTO.setPickupLatitude(11.0);
        updateDTO.setPickupLongitude(21.0);
        updateDTO.setDropoffAddress("New Dropoff");
        updateDTO.setDropoffLatitude(31.0);
        updateDTO.setDropoffLongitude(41.0);
        updateDTO.setPaymentMethod(PaymentMethod.WALLET);
        updateDTO.setPaymentStatus(PaymentStatus.COMPLETED);


        when(rideRepository.findById(100L)).thenReturn(Optional.of(ride));
        when(userService.getUserById(1L)).thenReturn(passengerUser);
        when(driverRepository.findById(10L)).thenReturn(Optional.of(driverEntity));
        when(rideRepository.save(any(Ride.class))).thenReturn(ride);

        RideDTO updatedRideDTO = rideService.updateRide(100L, updateDTO); // Expect DTO

        assertNotNull(updatedRideDTO);
        assertEquals("New Pickup", updatedRideDTO.getPickupAddress());
        assertEquals("New Dropoff", updatedRideDTO.getDropoffAddress());
        assertEquals(RideStatus.COMPLETED, updatedRideDTO.getStatus());
        assertEquals(30.00, updatedRideDTO.getActualFare());
        assertEquals("Great service!", updatedRideDTO.getCustomerFeedback());
        assertEquals(5, updatedRideDTO.getCustomerRating());
        assertEquals("Excellent passenger!", updatedRideDTO.getDriverFeedback());
        assertEquals(5, updatedRideDTO.getDriverRating());
        assertEquals(VehicleType.SUV, updatedRideDTO.getVehicleType());
        verify(rideRepository, times(1)).save(any(Ride.class));
    }

    @Test
    void testUpdateRideNotFound() {
        RideDTO updateDTO = new RideDTO();
        updateDTO.setPickupAddress("New Pickup");
        updateDTO.setPassengerId(1L);
        updateDTO.setDriverId(10L);
        updateDTO.setPickupLatitude(11.0);
        updateDTO.setPickupLongitude(21.0);
        updateDTO.setDropoffAddress("New Dropoff");
        updateDTO.setDropoffLatitude(31.0);
        updateDTO.setDropoffLongitude(41.0);
        updateDTO.setStatus(RideStatus.REQUESTED);
        updateDTO.setActualFare(25.50);
        updateDTO.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        updateDTO.setPaymentStatus(PaymentStatus.COMPLETED);


        when(rideRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> rideService.updateRide(999L, updateDTO));
        verify(rideRepository, never()).save(any(Ride.class));
    }

    @Test
    void testDeleteRideSuccess() {
        when(rideRepository.existsById(100L)).thenReturn(true);
        doNothing().when(rideRepository).deleteById(100L);

        assertDoesNotThrow(() -> rideService.deleteRide(100L));
        verify(rideRepository, times(1)).deleteById(100L);
    }

    @Test
    void testDeleteRideNotFound() {
        when(rideRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> rideService.deleteRide(999L));
        verify(rideRepository, never()).deleteById(anyLong());
    }
}

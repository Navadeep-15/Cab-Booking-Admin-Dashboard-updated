package com.admindashboard;

import com.admindashboard.driververification.*;
import com.admindashboard.enums.DriverStatus;
import com.admindashboard.enums.UserType;
import com.admindashboard.enums.VehicleType;
import com.admindashboard.exception.DuplicateEntityException;
import com.admindashboard.exception.EntityNotFoundException;
import com.admindashboard.exception.UnauthorizedAccessException; // Import the new exception
import com.admindashboard.usermanagement.User;
import com.admindashboard.usermanagement.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DriverServiceTest {

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private DriverService driverService;

    private User user;
    private Driver driver;
    private DriverDTO driverDTO;

    @BeforeEach
    void setUp() {
        // Setup a mock User entity
        user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@test.com");
        user.setUserType(UserType.PASSENGER); // Initially a passenger

        // Setup a mock Driver entity with all new fields
        driver = new Driver();
        driver.setId(1L);
        driver.setUser(user); // Link the driver to the user
        driver.setLicenseNumber("LIC12345");
        driver.setVehicleType(VehicleType.SEDAN);
        driver.setLicenseExpiryDate(new Date(System.currentTimeMillis() + 86400000)); // Tomorrow
        driver.setVerificationStatus(DriverStatus.PENDING);
        driver.setCurrentLatitude(34.0522);
        driver.setCurrentLongitude(-118.2437);
        driver.setIsAvailable(true);
        driver.setIsOnline(true);
        driver.setLicenseImage("http://example.com/license.jpg");
        driver.setRating(4.5);
        driver.setTotalEarnings(1500.75);
        driver.setTotalRides(50);
        driver.setVehicleColor("Black");
        driver.setVehicleImage("http://example.com/vehicle.jpg");
        driver.setVehicleModel("Camry");
        driver.setVehicleNumber("XYZ789");

        // Setup a mock DriverDTO with all new fields
        driverDTO = new DriverDTO();
        driverDTO.setUserId(1L); // This matches the user.id
        driverDTO.setLicenseNumber("LIC12345");
        driverDTO.setVehicleType(VehicleType.SEDAN);
        driverDTO.setLicenseExpiryDate(new Date(System.currentTimeMillis() + 86400000));
        driverDTO.setCurrentLatitude(34.0522);
        driverDTO.setCurrentLongitude(-118.2437);
        driverDTO.setIsAvailable(true);
        driverDTO.setIsOnline(true);
        driverDTO.setLicenseImage("http://example.com/license.jpg");
        driverDTO.setRating(4.5);
        driverDTO.setTotalEarnings(1500.75);
        driverDTO.setTotalRides(50);
        driverDTO.setVehicleColor("Black");
        driverDTO.setVehicleImage("http://example.com/vehicle.jpg");
        driverDTO.setVehicleModel("Camry");
        driverDTO.setVehicleNumber("XYZ789");
    }

    @Test
    void testRegisterDriverSuccess() {
        // Mock the behavior of dependencies
        when(userService.getUserById(anyLong())).thenReturn(user);
        when(driverRepository.existsByLicenseNumber(anyString())).thenReturn(false);
        when(driverRepository.findByUserId(anyLong())).thenReturn(Optional.empty());
        when(driverRepository.save(any(Driver.class))).thenReturn(driver);

        // Call the service method
        DriverResponseDTO response = driverService.registerDriver(driverDTO);

        // Assertions
        assertNotNull(response);
        assertEquals("LIC12345", response.getLicenseNumber());
        assertEquals(DriverStatus.PENDING, response.getVerificationStatus());
        assertEquals(VehicleType.SEDAN, response.getVehicleType()); // Assert new field
        assertEquals(4.5, response.getRating()); // Assert new field
        assertEquals("XYZ789", response.getVehicleNumber()); // Assert new field
        verify(userService, times(1)).getUserById(1L);
        verify(driverRepository, times(1)).save(any(Driver.class));
    }

    @Test
    void testRegisterDriverWithDuplicateLicenseNumber() {
        // Mock the repository to simulate an existing license number
        when(driverRepository.existsByLicenseNumber(anyString())).thenReturn(true);

        // Assert that a DuplicateEntityException is thrown
        assertThrows(DuplicateEntityException.class, () -> driverService.registerDriver(driverDTO));
        verify(driverRepository, never()).save(any(Driver.class));
    }

    @Test
    void testRegisterDriverWithExistingUserDriverProfile() {
        // Mock the repository to simulate an existing driver profile for the user
        when(userService.getUserById(anyLong())).thenReturn(user);
        when(driverRepository.existsByLicenseNumber(anyString())).thenReturn(false);
        when(driverRepository.findByUserId(anyLong())).thenReturn(Optional.of(driver));

        // Assert that a DuplicateEntityException is thrown
        assertThrows(DuplicateEntityException.class, () -> driverService.registerDriver(driverDTO));
        verify(driverRepository, never()).save(any(Driver.class));
    }

    @Test
    void testGetAllDriversSuccess() {
        // Mock the repository to return a list of drivers
        when(driverRepository.findAll()).thenReturn(List.of(driver));

        // Call the service method
        List<DriverResponseDTO> drivers = driverService.getAllDrivers();

        // Assertions
        assertFalse(drivers.isEmpty());
        assertEquals(1, drivers.size());
        assertEquals("John", drivers.get(0).getFirstName());
        assertEquals(VehicleType.SEDAN, drivers.get(0).getVehicleType());
        assertEquals(4.5, drivers.get(0).getRating());
        verify(driverRepository, times(1)).findAll();
    }

    @Test
    void testGetDriverByIdSuccess() {
        // Mock the repository to return a driver
        when(driverRepository.findById(1L)).thenReturn(Optional.of(driver));

        // Call the service method
        DriverResponseDTO foundDriver = driverService.getDriverById(1L);

        // Assertions
        assertNotNull(foundDriver);
        assertEquals(driver.getId(), foundDriver.getDriverId());
        assertEquals(driver.getUser().getFirstName(), foundDriver.getFirstName());
        assertEquals(VehicleType.SEDAN, foundDriver.getVehicleType());
        assertEquals("XYZ789", foundDriver.getVehicleNumber());
        verify(driverRepository, times(1)).findById(1L);
    }

    @Test
    void testGetDriverByIdNotFound() {
        // Mock the repository to return an empty Optional
        when(driverRepository.findById(1L)).thenReturn(Optional.empty());

        // Assert that an EntityNotFoundException is thrown
        assertThrows(EntityNotFoundException.class, () -> driverService.getDriverById(1L));
    }

    @Test
    void testUpdateDriverSuccess() {
        // Create an update DTO with some new fields
        DriverDTO updateDTO = new DriverDTO();
        updateDTO.setVehicleType(VehicleType.SUV);
        updateDTO.setIsOnline(false);
        updateDTO.setRating(4.8);
        updateDTO.setVehicleColor("White");
        updateDTO.setLicenseNumber("LIC12345"); // Keep same license number to avoid duplicate check
        updateDTO.setLicenseExpiryDate(new Date(System.currentTimeMillis() + 86400000));
        updateDTO.setUserId(1L); // Matches existing user ID
        updateDTO.setVehicleNumber("XYZ789"); // Mandatory field


        when(driverRepository.findById(1L)).thenReturn(Optional.of(driver));
        when(driverRepository.existsByLicenseNumber(anyString())).thenReturn(false); // No duplicate license
        when(driverRepository.save(any(Driver.class))).thenReturn(driver); // Return the original driver object, which will be modified by service

        // Call the service method to update the driver
        DriverResponseDTO updatedDriver = driverService.updateDriver(1L, updateDTO);

        // Assertions
        assertNotNull(updatedDriver);
        assertEquals(VehicleType.SUV, updatedDriver.getVehicleType());
        assertEquals(false, updatedDriver.getIsOnline());
        assertEquals(4.8, updatedDriver.getRating());
        assertEquals("White", updatedDriver.getVehicleColor());
        verify(driverRepository, times(1)).save(any(Driver.class));
    }

    @Test
    void testUpdateDriverWithMismatchedUserId() {
        DriverDTO updateDTO = new DriverDTO();
        updateDTO.setUserId(99L); // Mismatched user ID
        updateDTO.setLicenseNumber("LIC12345"); // Other mandatory fields
        updateDTO.setVehicleType(VehicleType.SEDAN);
        updateDTO.setLicenseExpiryDate(new Date(System.currentTimeMillis() + 86400000));
        updateDTO.setVehicleNumber("XYZ789");

        when(driverRepository.findById(1L)).thenReturn(Optional.of(driver));

        // Assert that an UnauthorizedAccessException is thrown
        assertThrows(UnauthorizedAccessException.class, () -> driverService.updateDriver(1L, updateDTO));
        verify(driverRepository, never()).save(any(Driver.class));
    }

    @Test
    void testUpdateDriverWithDuplicateLicenseNumber() {
        DriverDTO updateDTO = new DriverDTO();
        updateDTO.setLicenseNumber("NEWLIC789"); // New license number that conflicts
        updateDTO.setVehicleType(VehicleType.SEDAN); // Add other mandatory fields if needed by validation
        updateDTO.setUserId(1L); // Matching user ID
        updateDTO.setLicenseExpiryDate(new Date(System.currentTimeMillis() + 86400000));
        updateDTO.setVehicleNumber("XYZ789");


        Driver existingDriverWithNewLicense = new Driver();
        existingDriverWithNewLicense.setId(2L); // Different ID
        existingDriverWithNewLicense.setLicenseNumber("NEWLIC789");

        when(driverRepository.findById(1L)).thenReturn(Optional.of(driver));
        when(driverRepository.existsByLicenseNumber("NEWLIC789")).thenReturn(true);

        assertThrows(DuplicateEntityException.class, () -> driverService.updateDriver(1L, updateDTO));
        verify(driverRepository, never()).save(any(Driver.class));
    }

    @Test
    void testDeleteDriverSuccess() {
        when(driverRepository.existsById(1L)).thenReturn(true);
        doNothing().when(driverRepository).deleteById(1L);

        assertDoesNotThrow(() -> driverService.deleteDriver(1L));
        verify(driverRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteDriverNotFound() {
        when(driverRepository.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> driverService.deleteDriver(1L));
        verify(driverRepository, never()).deleteById(anyLong());
    }

    @Test
    void testUpdateDriverStatusSuccess() {
        when(driverRepository.findById(1L)).thenReturn(Optional.of(driver));
        when(driverRepository.save(any(Driver.class))).thenReturn(driver);

        // Call the service method to verify the driver
        DriverResponseDTO response = driverService.updateDriverStatus(1L, DriverStatus.VERIFIED); // Changed to VERIFIED

        assertNotNull(response);
        assertEquals(DriverStatus.VERIFIED, response.getVerificationStatus());
        verify(driverRepository, times(1)).save(any(Driver.class));
    }

    @Test
    void testUpdateDriverStatusNotFound() {
        when(driverRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> driverService.updateDriverStatus(1L, DriverStatus.VERIFIED)); // Changed to VERIFIED
        verify(driverRepository, never()).save(any(Driver.class));
    }
}

package com.admindashboard;

import com.admindashboard.driververification.*;
import com.admindashboard.usermanagement.*;
import com.admindashboard.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DriverServiceTest {

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
        user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");

        driver = new Driver();
        driver.setId(1L);
        driver.setUser(user);
        driver.setLicenseNumber("LIC123");
        driver.setVehicleType("Car");
        driver.setLicenseExpiryDate(Date.valueOf("2025-12-31"));
        driver.setVerificationStatus(DriverVerificationStatus.PENDING);

        driverDTO = new DriverDTO();
        driverDTO.setLicenseNumber("LIC123");
        driverDTO.setVehicleType("Car");
        driverDTO.setLicenseExpiryDate(Date.valueOf("2025-12-31"));
        driverDTO.setUserId(1L);
    }

    @Test
    void testRegisterDriverSuccess() {
        when(driverRepository.existsByLicenseNumber(driverDTO.getLicenseNumber())).thenReturn(false);
        when(userService.getUserById(driverDTO.getUserId())).thenReturn(user);
        when(driverRepository.findByUserId(user.getId())).thenReturn(Optional.empty());
        when(driverRepository.save(any(Driver.class))).thenReturn(driver);

        DriverResponseDTO response = driverService.registerDriver(driverDTO);

        assertNotNull(response);
        assertEquals(driver.getLicenseNumber(), response.getLicenseNumber());
        assertEquals(DriverVerificationStatus.PENDING, response.getVerificationStatus());
        verify(driverRepository, times(1)).save(any(Driver.class));
    }
    
    @Test
    void testGetAllDrivers() {
        when(driverRepository.findAll()).thenReturn(List.of(driver));
        
        List<DriverResponseDTO> drivers = driverService.getAllDrivers();
        
        assertFalse(drivers.isEmpty());
        assertEquals(1, drivers.size());
        assertEquals(driver.getLicenseNumber(), drivers.get(0).getLicenseNumber());
        verify(driverRepository, times(1)).findAll();
    }

    @Test
    void testRegisterDriverWithExistingLicense() {
        when(driverRepository.existsByLicenseNumber(driverDTO.getLicenseNumber())).thenReturn(true);

        assertThrows(DuplicateEntityException.class, () -> driverService.registerDriver(driverDTO));
        verify(driverRepository, never()).save(any(Driver.class));
    }

    @Test
    void testGetDriverByIdSuccess() {
        when(driverRepository.findById(1L)).thenReturn(Optional.of(driver));

        DriverResponseDTO foundDriver = driverService.getDriverById(1L);

        assertNotNull(foundDriver);
        assertEquals(driver.getLicenseNumber(), foundDriver.getLicenseNumber());
    }

    @Test
    void testGetDriverByIdNotFound() {
        when(driverRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> driverService.getDriverById(1L));
    }

    @Test
    void testUpdateDriverStatusSuccess() {
        when(driverRepository.findById(1L)).thenReturn(Optional.of(driver));
        when(driverRepository.save(any(Driver.class))).thenReturn(driver);

        DriverResponseDTO response = driverService.updateDriverStatus(1L, DriverVerificationStatus.VERIFIED);

        assertNotNull(response);
        assertEquals(DriverVerificationStatus.VERIFIED, response.getVerificationStatus());
        verify(driverRepository, times(1)).save(any(Driver.class));
    }

    @Test
    void testUpdateDriverSuccess() {
        DriverDTO updateDTO = new DriverDTO();
        updateDTO.setVehicleType("Truck");
        
        when(driverRepository.findById(1L)).thenReturn(Optional.of(driver));
        when(driverRepository.save(any(Driver.class))).thenReturn(driver);
        
        DriverResponseDTO updatedDriver = driverService.updateDriver(1L, updateDTO);

        assertNotNull(updatedDriver);
        assertEquals("Truck", updatedDriver.getVehicleType());
        verify(driverRepository, times(1)).save(any(Driver.class));
    }

    @Test
    void testDeleteDriverSuccess() {
        when(driverRepository.existsById(1L)).thenReturn(true);
        doNothing().when(driverRepository).deleteById(1L);

        assertDoesNotThrow(() -> driverService.deleteDriver(1L));
        verify(driverRepository, times(1)).deleteById(1L);
    }
}
package com.admindashboard.driververification;

import com.admindashboard.exception.*;
import com.admindashboard.usermanagement.*;
import com.admindashboard.enums.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DriverService {

    private final DriverRepository driverRepository;
    private final UserService userService;

    public DriverService(DriverRepository driverRepository, UserService userService) {
        this.driverRepository = driverRepository;
        this.userService = userService;
    }

    @Transactional
    public DriverResponseDTO registerDriver(DriverDTO driverDTO) {
        if (driverRepository.existsByLicenseNumber(driverDTO.getLicenseNumber())) {
            throw new DuplicateEntityException("Driver", "license number", driverDTO.getLicenseNumber());
        }

        User user = userService.getUserById(driverDTO.getUserId());
        driverRepository.findByUserId(user.getId()).ifPresent(d -> {
            throw new DuplicateEntityException("User already has an associated driver profile", "DRIVER_002");
        });

        Driver driver = new Driver();
        driver.setUser(user);
        driver.setLicenseNumber(driverDTO.getLicenseNumber());
        driver.setVehicleType(driverDTO.getVehicleType());
        driver.setLicenseExpiryDate(driverDTO.getLicenseExpiryDate());
        driver.setVerificationStatus(DriverStatus.PENDING); // Default status on registration

        // Set new fields from DTO
        driver.setCurrentLatitude(driverDTO.getCurrentLatitude());
        driver.setCurrentLongitude(driverDTO.getCurrentLongitude());
        driver.setIsAvailable(driverDTO.getIsAvailable());
        driver.setIsOnline(driverDTO.getIsOnline());
        driver.setLicenseImage(driverDTO.getLicenseImage());
        driver.setRating(driverDTO.getRating());
        driver.setTotalEarnings(driverDTO.getTotalEarnings());
        driver.setTotalRides(driverDTO.getTotalRides());
        driver.setVehicleColor(driverDTO.getVehicleColor());
        driver.setVehicleImage(driverDTO.getVehicleImage());
        driver.setVehicleModel(driverDTO.getVehicleModel());
        driver.setVehicleNumber(driverDTO.getVehicleNumber());

        Driver savedDriver = driverRepository.save(driver);

        return convertToDto(savedDriver);
    }

    public List<DriverResponseDTO> getAllDrivers() {
        return driverRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public DriverResponseDTO getDriverById(Long driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new EntityNotFoundException("Driver", driverId));
        return convertToDto(driver);
    }

    @Transactional
    public DriverResponseDTO updateDriver(Long driverId, DriverDTO driverDTO) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new EntityNotFoundException("Driver", driverId));

        // ⭐ IMPORTANT: Verify userId consistency
        if (driverDTO.getUserId() != null && !driverDTO.getUserId().equals(driver.getUser().getId())) {
            throw new UnauthorizedAccessException("Cannot change the user associated with a driver profile.", "DRIVER_AUTH_001");
        }

        if (driverDTO.getLicenseNumber() != null &&
            !driverDTO.getLicenseNumber().equals(driver.getLicenseNumber())) {
            if (driverRepository.existsByLicenseNumber(driverDTO.getLicenseNumber())) {
                throw new DuplicateEntityException("Driver", "license number", driverDTO.getLicenseNumber());
            }
            driver.setLicenseNumber(driverDTO.getLicenseNumber());
        }

        if (driverDTO.getVehicleType() != null) driver.setVehicleType(driverDTO.getVehicleType());
        if (driverDTO.getLicenseExpiryDate() != null) driver.setLicenseExpiryDate(driverDTO.getLicenseExpiryDate());
        // verificationStatus should typically be updated via a specific admin action, not a general update
        // if (driverDTO.getVerificationStatus() != null) driver.setVerificationStatus(driverDTO.getVerificationStatus());

        // Update new fields from DTO if present
        if (driverDTO.getCurrentLatitude() != null) driver.setCurrentLatitude(driverDTO.getCurrentLatitude());
        if (driverDTO.getCurrentLongitude() != null) driver.setCurrentLongitude(driverDTO.getCurrentLongitude());
        if (driverDTO.getIsAvailable() != null) driver.setIsAvailable(driverDTO.getIsAvailable());
        if (driverDTO.getIsOnline() != null) driver.setIsOnline(driverDTO.getIsOnline());
        if (driverDTO.getLicenseImage() != null) driver.setLicenseImage(driverDTO.getLicenseImage());
        if (driverDTO.getRating() != null) driver.setRating(driverDTO.getRating());
        if (driverDTO.getTotalEarnings() != null) driver.setTotalEarnings(driverDTO.getTotalEarnings());
        if (driverDTO.getTotalRides() != null) driver.setTotalRides(driverDTO.getTotalRides());
        if (driverDTO.getVehicleColor() != null) driver.setVehicleColor(driverDTO.getVehicleColor());
        if (driverDTO.getVehicleImage() != null) driver.setVehicleImage(driverDTO.getVehicleImage());
        if (driverDTO.getVehicleModel() != null) driver.setVehicleModel(driverDTO.getVehicleModel());
        if (driverDTO.getVehicleNumber() != null) driver.setVehicleNumber(driverDTO.getVehicleNumber());


        return convertToDto(driverRepository.save(driver));
    }

    @Transactional
    public void deleteDriver(Long driverId) {
        if (!driverRepository.existsById(driverId)) {
            throw new EntityNotFoundException("Driver", driverId);
        }
        driverRepository.deleteById(driverId);
    }

    @Transactional
    public DriverResponseDTO updateDriverStatus(Long driverId, DriverStatus status) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new EntityNotFoundException("Driver", driverId));
        driver.setVerificationStatus(status);
        return convertToDto(driverRepository.save(driver));
    }

    private DriverResponseDTO convertToDto(Driver driver) {
        DriverResponseDTO dto = new DriverResponseDTO();
        dto.setDriverId(driver.getId());
        dto.setUserId(driver.getUser().getId());
        dto.setLicenseNumber(driver.getLicenseNumber());
        dto.setVehicleType(driver.getVehicleType());
        dto.setLicenseExpiryDate(driver.getLicenseExpiryDate());
        dto.setVerificationStatus(driver.getVerificationStatus());
        dto.setFirstName(driver.getUser().getFirstName());
        dto.setLastName(driver.getUser().getLastName());
        dto.setEmail(driver.getUser().getEmail());

        // Set new fields
        dto.setCreatedAt(driver.getCreatedAt());
        dto.setCurrentLatitude(driver.getCurrentLatitude());
        dto.setCurrentLongitude(driver.getCurrentLongitude());
        dto.setIsAvailable(driver.getIsAvailable());
        dto.setIsOnline(driver.getIsOnline());
        dto.setLicenseImage(driver.getLicenseImage());
        dto.setRating(driver.getRating());
        dto.setTotalEarnings(driver.getTotalEarnings());
        dto.setTotalRides(driver.getTotalRides());
        dto.setUpdatedAt(driver.getUpdatedAt());
        dto.setVehicleColor(driver.getVehicleColor());
        dto.setVehicleImage(driver.getVehicleImage());
        dto.setVehicleModel(driver.getVehicleModel());
        dto.setVehicleNumber(driver.getVehicleNumber());

        return dto;
    }
}

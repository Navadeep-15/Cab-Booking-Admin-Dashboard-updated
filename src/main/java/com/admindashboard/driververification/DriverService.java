package com.admindashboard.driververification;

import com.admindashboard.driververification.*;
import com.admindashboard.exception.*;
import com.admindashboard.usermanagement.User;
import com.admindashboard.usermanagement.UserService;

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
            throw new DuplicateEntityException("License number already exists", "DRIVER_001");
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
        driver.setVerificationStatus(DriverVerificationStatus.PENDING);
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
                .orElseThrow(() -> new EntityNotFoundException("Driver not found with id: " + driverId, "DRIVER_003"));
        return convertToDto(driver);
    }

    @Transactional
    public DriverResponseDTO updateDriver(Long driverId, DriverDTO driverDTO) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new EntityNotFoundException("Driver", driverId));

        if (driverDTO.getLicenseNumber() != null && 
            !driverDTO.getLicenseNumber().equals(driver.getLicenseNumber())) {
            if (driverRepository.existsByLicenseNumber(driverDTO.getLicenseNumber())) {
                throw new DuplicateEntityException("Driver", "license number", driverDTO.getLicenseNumber());
            }
            driver.setLicenseNumber(driverDTO.getLicenseNumber());
        }

        if (driverDTO.getVehicleType() != null) driver.setVehicleType(driverDTO.getVehicleType());
        if (driverDTO.getLicenseExpiryDate() != null) driver.setLicenseExpiryDate(driverDTO.getLicenseExpiryDate());
        if (driverDTO.getVerificationStatus() != null) driver.setVerificationStatus(driverDTO.getVerificationStatus());

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
    public DriverResponseDTO updateDriverStatus(Long driverId, DriverVerificationStatus status) {
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
        return dto;
    }
}
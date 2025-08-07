package com.admindashboard.earnings;

import com.admindashboard.driververification.Driver;
import com.admindashboard.enums.*;
import com.admindashboard.driververification.DriverRepository;
import com.admindashboard.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class EarningService {

    private final EarningRepository earningRepository;
    private final DriverRepository driverRepository;

    @Autowired
    public EarningService(EarningRepository earningRepository,
                         DriverRepository driverRepository) {
        this.earningRepository = earningRepository;
        this.driverRepository = driverRepository;
    }

    /**
     * Creates a new earning from an EarningDTO, validates the associated driver, and saves it.
     *
     * @param earningDTO The DTO containing the new earning data.
     * @return The DTO of the newly created earning.
     */
    @Transactional
    public EarningDTO createEarning(EarningDTO earningDTO) {
        // Find the driver entity to establish the relationship
        Driver driver = driverRepository.findById(earningDTO.getDriverId())
                .orElseThrow(() -> new EntityNotFoundException("Driver", earningDTO.getDriverId()));

        // Convert DTO to an Earning entity
        Earning earning = new Earning();
        earning.setDriver(driver);
        earning.setAmount(earningDTO.getAmount());
        earning.setTransactionDate(earningDTO.getTransactionDate());
        earning.setPaymentMethod(earningDTO.getPaymentMethod());
        earning.setPaymentStatus(earningDTO.getPaymentStatus());

        // Save the entity and return the DTO
        Earning savedEarning = earningRepository.save(earning);
        return convertToDto(savedEarning);
    }

    public List<EarningDTO> getAllEarnings() {
        return earningRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public EarningDTO getEarningById(Long id) {
        return earningRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new EntityNotFoundException("Earning", id));
    }

    public List<EarningDTO> getEarningsByDriver(Long driverId) {
        if (!driverRepository.existsById(driverId)) {
            throw new EntityNotFoundException("Driver", driverId);
        }
        return earningRepository.findByDriverId(driverId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<EarningDTO> getEarningsByDateRange(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
        return earningRepository.findByDateRange(start, end)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<EarningDTO> getEarningsByStatus(PaymentStatus status) {
        return earningRepository.findByPaymentStatus(status)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private EarningDTO convertToDto(Earning earning) {
        EarningDTO dto = new EarningDTO();
        dto.setId(earning.getId());
        dto.setAmount(earning.getAmount());
        dto.setTransactionDate(earning.getTransactionDate());
        dto.setPaymentMethod(earning.getPaymentMethod());
        dto.setPaymentStatus(earning.getPaymentStatus());

        Driver driver = earning.getDriver();
        dto.setDriverId(driver.getId());
        dto.setDriverName(driver.getUser().getFirstName() + " " + driver.getUser().getLastName());
        return dto;
    }
}

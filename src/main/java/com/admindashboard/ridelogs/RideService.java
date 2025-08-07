package com.admindashboard.ridelogs;

import com.admindashboard.enums.RideStatus;
import com.admindashboard.exception.EntityNotFoundException;
import com.admindashboard.usermanagement.User;
import com.admindashboard.usermanagement.UserService;
import com.admindashboard.driververification.Driver;
import com.admindashboard.driververification.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing Ride entities.
 * The implementation logic has been consolidated into this single class,
 * replacing the separate RideService interface and RideServiceImpl.
 */
@Service
public class RideService {

    private final RideRepository rideRepository;
    private final UserService userService;
    private final DriverRepository driverRepository;

    @Autowired
    public RideService(RideRepository rideRepository, UserService userService, DriverRepository driverRepository) {
        this.rideRepository = rideRepository;
        this.userService = userService;
        this.driverRepository = driverRepository;
    }

    @Transactional
    public RideDTO createRide(RideDTO rideDTO) { // Changed return type to RideDTO
        // Fetch passenger and driver entities
        User passenger = userService.getUserById(rideDTO.getPassengerId());
        Driver driver = driverRepository.findById(rideDTO.getDriverId())
                .orElseThrow(() -> new EntityNotFoundException("Driver", rideDTO.getDriverId()));

        Ride ride = new Ride();
        ride.setPassenger(passenger);
        ride.setDriver(driver);

        ride.setPickupAddress(rideDTO.getPickupAddress());
        ride.setPickupLatitude(rideDTO.getPickupLatitude());
        ride.setPickupLongitude(rideDTO.getPickupLongitude());
        ride.setDropoffAddress(rideDTO.getDropoffAddress());
        ride.setDestinationLatitude(rideDTO.getDropoffLatitude());
        ride.setDestinationLongitude(rideDTO.getDropoffLongitude());
        ride.setStatus(rideDTO.getStatus());
        ride.setActualFare(rideDTO.getActualFare());
        ride.setPaymentMethod(rideDTO.getPaymentMethod());
        ride.setPaymentStatus(rideDTO.getPaymentStatus());
        ride.setCancellationReason(rideDTO.getCancellationReason());
        ride.setCustomerRating(rideDTO.getCustomerRating());
        ride.setCustomerFeedback(rideDTO.getCustomerFeedback());
        ride.setDriverRating(rideDTO.getDriverRating());
        ride.setDriverFeedback(rideDTO.getDriverFeedback());
        ride.setVehicleType(rideDTO.getVehicleType());

        // Set timestamps from DTO if available, otherwise let @PrePersist handle it
        if (rideDTO.getRequestedAt() != null) ride.setRequestedAt(rideDTO.getRequestedAt());
        if (rideDTO.getAcceptedAt() != null) ride.setAcceptedAt(rideDTO.getAcceptedAt());
        if (rideDTO.getStartedAt() != null) ride.setStartedAt(rideDTO.getStartedAt());
        if (rideDTO.getCompletedAt() != null) ride.setCompletedAt(rideDTO.getCompletedAt());
        if (rideDTO.getCancelledAt() != null) ride.setCancelledAt(rideDTO.getCancelledAt());
        if (rideDTO.getCreatedAt() != null) ride.setCreatedAt(rideDTO.getCreatedAt());

        Ride savedRide = rideRepository.save(ride);
        return convertToDto(savedRide); // Convert to DTO before returning
    }

    public List<RideDTO> getAllRides() { // Changed return type to List<RideDTO>
        return rideRepository.findAll().stream()
                .map(this::convertToDto) // Convert each entity to DTO
                .collect(Collectors.toList());
    }

    public RideDTO getRideById(Long rideId) { // Changed return type to RideDTO
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new EntityNotFoundException("Ride", rideId));
        return convertToDto(ride); // Convert to DTO before returning
    }

    public List<RideDTO> getRidesByDriverId(Long driverId) { // Changed return type to List<RideDTO>
        List<Ride> rides = rideRepository.findByDriverId(driverId);
        if (rides.isEmpty()) {
            throw new EntityNotFoundException("Rides for Driver ID", driverId);
        }
        return rides.stream()
                .map(this::convertToDto) // Convert each entity to DTO
                .collect(Collectors.toList());
    }

    public List<RideDTO> getRidesByPassengerId(Long passengerId) { // Changed return type to List<RideDTO>
        List<Ride> rides = rideRepository.findByPassengerId(passengerId);
        if (rides.isEmpty()) {
            throw new EntityNotFoundException("Rides for Passenger ID", passengerId);
        }
        return rides.stream()
                .map(this::convertToDto) // Convert each entity to DTO
                .collect(Collectors.toList());
    }

    public List<RideDTO> getRideHistoryForPassenger(Long passengerId) { // Changed return type to List<RideDTO>
        return rideRepository.findByPassengerIdAndStatus(passengerId, RideStatus.COMPLETED).stream()
                .map(this::convertToDto) // Convert each entity to DTO
                .collect(Collectors.toList());
    }

    public List<RideDTO> getRideHistoryForDriver(Long driverId) { // Changed return type to List<RideDTO>
        return rideRepository.findByDriverIdAndStatus(driverId, RideStatus.COMPLETED).stream()
                .map(this::convertToDto) // Convert each entity to DTO
                .collect(Collectors.toList());
    }

    public List<RideDTO> getCancelledRides() { // Changed return type to List<RideDTO>
        return rideRepository.findByStatus(RideStatus.CANCELLED).stream()
                .map(this::convertToDto) // Convert each entity to DTO
                .collect(Collectors.toList());
    }

    public List<RideDTO> getRidesWithComplaints() { // Changed return type to List<RideDTO>
        return rideRepository.findByCustomerRatingLessThanOrCustomerFeedbackContainingIgnoreCase(3, "complaint").stream()
                .map(this::convertToDto) // Convert each entity to DTO
                .collect(Collectors.toList());
    }

    public List<RideDTO> getRidesWithFeedback() { // Changed return type to List<RideDTO>
        return rideRepository.findRidesWithAnyFeedback().stream()
                .map(this::convertToDto) // Convert each entity to Dto
                .collect(Collectors.toList());
    }

    @Transactional
    public RideDTO updateRide(Long rideId, RideDTO rideDTO) { // Changed return type to RideDTO
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new EntityNotFoundException("Ride", rideId));

        // Update fields if provided in DTO
        if (rideDTO.getPassengerId() != null && !rideDTO.getPassengerId().equals(ride.getPassenger().getId())) {
            ride.setPassenger(userService.getUserById(rideDTO.getPassengerId()));
        }
        if (rideDTO.getDriverId() != null && !rideDTO.getDriverId().equals(ride.getDriver().getId())) {
            ride.setDriver(driverRepository.findById(rideDTO.getDriverId())
                    .orElseThrow(() -> new EntityNotFoundException("Driver", rideDTO.getDriverId())));
        }
        if (rideDTO.getPickupAddress() != null) ride.setPickupAddress(rideDTO.getPickupAddress());
        if (rideDTO.getPickupLatitude() != null) ride.setPickupLatitude(rideDTO.getPickupLatitude());
        if (rideDTO.getPickupLongitude() != null) ride.setPickupLongitude(rideDTO.getPickupLongitude());
        if (rideDTO.getDropoffAddress() != null) ride.setDropoffAddress(rideDTO.getDropoffAddress());
        if (rideDTO.getDropoffLatitude() != null) ride.setDestinationLatitude(rideDTO.getDropoffLatitude());
        if (rideDTO.getDropoffLongitude() != null) ride.setDestinationLongitude(rideDTO.getDropoffLongitude());
        if (rideDTO.getStatus() != null) ride.setStatus(rideDTO.getStatus());
        if (rideDTO.getActualFare() != null) ride.setActualFare(rideDTO.getActualFare());
        if (rideDTO.getPaymentMethod() != null) ride.setPaymentMethod(rideDTO.getPaymentMethod());
        if (rideDTO.getPaymentStatus() != null) ride.setPaymentStatus(rideDTO.getPaymentStatus());
        if (rideDTO.getCancellationReason() != null) ride.setCancellationReason(rideDTO.getCancellationReason());
        if (rideDTO.getCustomerRating() != null) ride.setCustomerRating(rideDTO.getCustomerRating());
        if (rideDTO.getCustomerFeedback() != null) ride.setCustomerFeedback(rideDTO.getCustomerFeedback());
        if (rideDTO.getDriverRating() != null) ride.setDriverRating(rideDTO.getDriverRating());
        if (rideDTO.getDriverFeedback() != null) ride.setDriverFeedback(rideDTO.getDriverFeedback());
        if (rideDTO.getVehicleType() != null) ride.setVehicleType(rideDTO.getVehicleType());

        // Update timestamps if provided in DTO
        if (rideDTO.getRequestedAt() != null) ride.setRequestedAt(rideDTO.getRequestedAt());
        if (rideDTO.getAcceptedAt() != null) ride.setAcceptedAt(rideDTO.getAcceptedAt());
        if (rideDTO.getStartedAt() != null) ride.setStartedAt(rideDTO.getStartedAt());
        if (rideDTO.getCompletedAt() != null) ride.setCompletedAt(rideDTO.getCompletedAt());
        if (rideDTO.getCancelledAt() != null) ride.setCancelledAt(rideDTO.getCancelledAt());
        if (rideDTO.getCreatedAt() != null) ride.setCreatedAt(rideDTO.getCreatedAt());

        Ride updatedRide = rideRepository.save(ride);
        return convertToDto(updatedRide); // Convert to DTO before returning
    }

    @Transactional
    public void deleteRide(Long rideId) {
        if (!rideRepository.existsById(rideId)) {
            throw new EntityNotFoundException("Ride", rideId);
        }
        rideRepository.deleteById(rideId);
    }

    /**
     * Converts a Ride entity to a RideDTO.
     * This method fetches associated User and Driver details to populate the DTO fully.
     * @param ride The Ride entity to convert.
     * @return The corresponding RideDTO.
     */
    private RideDTO convertToDto(Ride ride) {
        RideDTO dto = new RideDTO();
        dto.setId(ride.getId());
        dto.setPassengerId(ride.getPassenger().getId());
        dto.setDriverId(ride.getDriver().getId());
        dto.setVehicleType(ride.getVehicleType());
        dto.setPickupAddress(ride.getPickupAddress());
        dto.setPickupLatitude(ride.getPickupLatitude());
        dto.setPickupLongitude(ride.getPickupLongitude());
        dto.setDropoffAddress(ride.getDropoffAddress());
        dto.setDropoffLatitude(ride.getDestinationLatitude()); // Map destination to dropoff for DTO
        dto.setDropoffLongitude(ride.getDestinationLongitude()); // Map destination to dropoff for DTO
        dto.setStatus(ride.getStatus());
        dto.setActualFare(ride.getActualFare());
        dto.setPaymentMethod(ride.getPaymentMethod());
        dto.setPaymentStatus(ride.getPaymentStatus());
        dto.setCancellationReason(ride.getCancellationReason());
        dto.setCustomerRating(ride.getCustomerRating());
        dto.setCustomerFeedback(ride.getCustomerFeedback());
        dto.setDriverRating(ride.getDriverRating());
        dto.setDriverFeedback(ride.getDriverFeedback());
        dto.setRequestedAt(ride.getRequestedAt());
        dto.setAcceptedAt(ride.getAcceptedAt());
        dto.setStartedAt(ride.getStartedAt());
        dto.setCompletedAt(ride.getCompletedAt());
        dto.setCancelledAt(ride.getCancelledAt());
        dto.setCreatedAt(ride.getCreatedAt());
        return dto;
    }
}

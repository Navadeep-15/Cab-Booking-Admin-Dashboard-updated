package com.admindashboard.ridelogs;

import com.admindashboard.enums.PaymentMethod;
import com.admindashboard.enums.PaymentStatus;
import com.admindashboard.enums.RideStatus;
import com.admindashboard.enums.VehicleType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.sql.Timestamp;

/**
 * Data Transfer Object for Ride information.
 * This class is used to transfer data between the client and the server,
 * providing validation and a clear separation from the database entity.
 * Renamed from TripDTO to RideDTO.
 */
@Data
public class RideDTO {

    private Long id;

    @NotNull(message = "Passenger ID is mandatory")
    private Long passengerId;

    @NotNull(message = "Driver ID is mandatory")
    private Long driverId;

    @NotNull(message = "Vehicle type is mandatory")
    private VehicleType vehicleType;

    @NotBlank(message = "Pickup address is mandatory")
    @Size(max = 255)
    private String pickupAddress;

    @NotNull(message = "Pickup latitude is mandatory")
    @DecimalMin(value = "-90.0", message = "Pickup latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Pickup latitude must be between -90 and 90")
    private Double pickupLatitude;

    @NotNull(message = "Pickup longitude is mandatory")
    @DecimalMin(value = "-180.0", message = "Pickup longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Pickup longitude must be between -180 and 180")
    private Double pickupLongitude;

    @NotBlank(message = "Dropoff address is mandatory")
    @Size(max = 255)
    private String dropoffAddress;

    @NotNull(message = "Dropoff latitude is mandatory")
    @DecimalMin(value = "-90.0", message = "Dropoff latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Dropoff latitude must be between -180 and 180")
    private Double dropoffLatitude;

    @NotNull(message = "Dropoff longitude is mandatory")
    @DecimalMin(value = "-180.0", message = "Dropoff longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Dropoff longitude must be between -180 and 180")
    private Double dropoffLongitude;

    @NotNull(message = "Status is mandatory")
    private RideStatus status;

    @PositiveOrZero(message = "Actual fare must be a positive number or zero")
    private Double actualFare;

    @NotNull(message = "Payment method is mandatory")
    private PaymentMethod paymentMethod;

    @NotNull(message = "Payment status is mandatory")
    private PaymentStatus paymentStatus;

    @Size(max = 255)
    private String cancellationReason;

    @Min(value = 1, message = "Customer rating must be between 1 and 5")
    @Max(value = 5, message = "Customer rating must be between 1 and 5")
    private Integer customerRating;

    @Size(max = 255)
    private String customerFeedback;

    @Min(value = 1, message = "Driver rating must be between 1 and 5")
    @Max(value = 5, message = "Driver rating must be between 1 and 5")
    private Integer driverRating;

    @Size(max = 255)
    private String driverFeedback;

    private Timestamp requestedAt;
    private Timestamp acceptedAt;
    private Timestamp startedAt;
    private Timestamp completedAt;
    private Timestamp cancelledAt;
    private Timestamp createdAt;
}

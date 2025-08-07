package com.admindashboard.ridelogs;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.sql.Timestamp;
import com.admindashboard.driververification.Driver;
import com.admindashboard.usermanagement.User;
import com.admindashboard.enums.*;

/**
 * Entity representing a ride.
 * Renamed from Trip to Ride for consistency.
 */
@Entity
@Table(name = "rides")
@Data
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @PastOrPresent
    @Column(name = "accepted_at")
    private Timestamp acceptedAt;

    @PositiveOrZero
    @Column(name = "actual_fare")
    private Double actualFare;

    @Size(max = 255)
    @Column(name = "cancellation_reason")
    private String cancellationReason;

    @PastOrPresent
    @Column(name = "cancelled_at")
    private Timestamp cancelledAt;

    @PastOrPresent
    @Column(name = "completed_at")
    private Timestamp completedAt;

    @PastOrPresent
    @Column(name = "created_at")
    private Timestamp createdAt;

    @Size(max = 255)
    @Column(name = "customer_feedback")
    private String customerFeedback;

    @Min(1)
    @Max(5)
    @Column(name = "customer_rating")
    private Integer customerRating;

    @Size(max = 255)
    @Column(name = "destination_address")
    private String destinationAddress;

    @NotBlank(message = "Dropoff address is mandatory")
    @Size(max = 255)
    @Column(name = "dropoff_address", nullable = false)
    private String dropoffAddress;

    @NotNull(message = "Destination latitude is mandatory")
    @DecimalMin(value = "-90.0", message = "Destination latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Destination latitude must be between -90 and 90")
    @Column(name = "destination_latitude", nullable = false)
    private Double destinationLatitude;

    @NotNull(message = "Destination longitude is mandatory")
    @DecimalMin(value = "-180.0", message = "Destination longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Destination longitude must be between -180 and 180")
    @Column(name = "destination_longitude", nullable = false)
    private Double destinationLongitude;

    @Size(max = 255)
    @Column(name = "driver_feedback")
    private String driverFeedback;

    @Min(1)
    @Max(5)
    @Column(name = "driver_rating")
    private Integer driverRating;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private PaymentMethod paymentMethod;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatus paymentStatus;

    @Size(max = 255)
    @Column(name = "pickup_address")
    private String pickupAddress;

    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    @Column(name = "pickup_latitude")
    private Double pickupLatitude;

    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    @Column(name = "pickup_longitude")
    private Double pickupLongitude;

    @PastOrPresent
    @Column(name = "requested_at")
    private Timestamp requestedAt;

    @PastOrPresent
    @Column(name = "started_at")
    private Timestamp startedAt;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private RideStatus status;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type")
    private VehicleType vehicleType;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User passenger;

}

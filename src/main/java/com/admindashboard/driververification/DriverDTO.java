package com.admindashboard.driververification;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import com.admindashboard.enums.*;
import java.sql.Date;

@Data
public class DriverDTO {
    @NotNull(message = "User ID is mandatory")
    private Long userId;

    @NotBlank(message = "License number is mandatory")
    private String licenseNumber;

    @NotNull(message = "Vehicle type is mandatory")
    private VehicleType vehicleType; // Changed to enum

    @NotNull(message = "License expiry date is mandatory")
    private Date licenseExpiryDate;

    // New fields from schema
    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    private Double currentLatitude;

    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    private Double currentLongitude;

    private Boolean isAvailable;
    private Boolean isOnline;

    @Size(max = 255, message = "License image URL too long")
    private String licenseImage;

    @Min(value = 0, message = "Rating cannot be negative")
    @DecimalMax(value = "5.0", message = "Rating cannot exceed 5.0")
    private Double rating;

    @Min(value = 0, message = "Total earnings cannot be negative")
    private Double totalEarnings;

    @Min(value = 0, message = "Total rides cannot be negative")
    private Integer totalRides;

    @Size(max = 255, message = "Vehicle color too long")
    private String vehicleColor;

    @Size(max = 255, message = "Vehicle image URL too long")
    private String vehicleImage;

    @Size(max = 255, message = "Vehicle model too long")
    private String vehicleModel;

    @NotBlank(message = "Vehicle number is mandatory")
    @Size(max = 255, message = "Vehicle number too long")
    private String vehicleNumber;

    private DriverStatus verificationStatus; // This can be set by admin, not user directly on creation
}

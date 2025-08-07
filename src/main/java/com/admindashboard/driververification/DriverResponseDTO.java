package com.admindashboard.driververification;

import lombok.Data;
import lombok.NoArgsConstructor;
import com.admindashboard.enums.*;
import java.sql.Date;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
public class DriverResponseDTO {
    private Long driverId;
    private Long userId;
    private String licenseNumber;
    private VehicleType vehicleType; // Changed to enum
    private Date licenseExpiryDate;
    private DriverStatus verificationStatus;
    private String firstName;
    private String lastName;
    private String email;

    // New fields from schema
    private Timestamp createdAt;
    private Double currentLatitude;
    private Double currentLongitude;
    private Boolean isAvailable;
    private Boolean isOnline;
    private String licenseImage;
    private Double rating;
    private Double totalEarnings;
    private Integer totalRides;
    private Timestamp updatedAt;
    private String vehicleColor;
    private String vehicleImage;
    private String vehicleModel;
    private String vehicleNumber;
}

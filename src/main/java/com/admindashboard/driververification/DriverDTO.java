package com.admindashboard.driververification;

import com.admindashboard.driververification.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.sql.Date;

@Data
public class DriverDTO {
    @NotBlank(message = "License number is mandatory")
    private String licenseNumber;

    @NotBlank(message = "Vehicle type is mandatory")
    private String vehicleType;

    @NotNull(message = "License expiry date is mandatory")
    private Date licenseExpiryDate;

    @NotNull(message = "User ID is mandatory")
    private Long userId;

    private DriverVerificationStatus verificationStatus;
}
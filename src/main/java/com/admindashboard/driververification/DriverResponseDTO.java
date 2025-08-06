package com.admindashboard.driververification;

import com.admindashboard.driververification.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@NoArgsConstructor
public class DriverResponseDTO {
    private Long driverId;
    private Long userId;
    private String licenseNumber;
    private String vehicleType;
    private Date licenseExpiryDate;
    private DriverVerificationStatus verificationStatus;
    private String firstName;
    private String lastName;
    private String email;
}
package com.admindashboard.driververification;

import jakarta.persistence.*;
import lombok.Data;
import java.sql.Date;
import com.admindashboard.usermanagement.User;

@Entity
@Table(name = "drivers")
@Data
public class Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "driver_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;
    
    @Column(name = "license_number", unique = true, nullable = false)
    private String licenseNumber;

    @Column(name = "vehicle_type", nullable = false)
    private String vehicleType;

    @Column(name = "license_expiry_date", nullable = false)
    private Date licenseExpiryDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false)
    private DriverVerificationStatus verificationStatus;
}
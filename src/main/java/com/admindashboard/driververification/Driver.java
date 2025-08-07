package com.admindashboard.driververification;

import jakarta.persistence.*;
import lombok.Data;
import java.sql.Date;
import java.sql.Timestamp;
import com.admindashboard.usermanagement.User;
import com.admindashboard.enums.*;

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
    @Enumerated(EnumType.STRING) // Assuming VehicleType is an enum
    private VehicleType vehicleType;

    @Column(name = "license_expiry_date", nullable = false)
    private Date licenseExpiryDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false)
    private DriverStatus verificationStatus;

    // New fields from schema
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;

    @Column(name = "current_latitude")
    private Double currentLatitude;

    @Column(name = "current_longitude")
    private Double currentLongitude;

    @Column(name = "is_available")
    private Boolean isAvailable;

    @Column(name = "is_online")
    private Boolean isOnline;

    @Column(name = "license_image")
    private String licenseImage;

    @Column(name = "rating")
    private Double rating;

    @Column(name = "total_earnings")
    private Double totalEarnings;

    @Column(name = "total_rides")
    private Integer totalRides;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "vehicle_color")
    private String vehicleColor;

    @Column(name = "vehicle_image")
    private String vehicleImage;

    @Column(name = "vehicle_model")
    private String vehicleModel;

    @Column(name = "vehicle_number")
    private String vehicleNumber;

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Timestamp(System.currentTimeMillis());
        this.updatedAt = this.createdAt;
        if (this.isAvailable == null) this.isAvailable = false;
        if (this.isOnline == null) this.isOnline = false;
        if (this.rating == null) this.rating = 0.0;
        if (this.totalEarnings == null) this.totalEarnings = 0.0;
        if (this.totalRides == null) this.totalRides = 0;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }
}

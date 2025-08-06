package com.admindashboard.driververification;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Date;

import com.admindashboard.usermanagement.User;


public enum DriverVerificationStatus {
    PENDING, VERIFIED, REJECTED
}
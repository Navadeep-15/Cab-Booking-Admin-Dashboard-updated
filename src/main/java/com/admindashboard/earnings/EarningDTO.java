package com.admindashboard.earnings;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class EarningDTO {
    private Long id;
    private Long driverId;
    private String driverName;
    private BigDecimal amount;
    private LocalDateTime transactionDate;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
}
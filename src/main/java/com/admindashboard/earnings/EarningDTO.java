package com.admindashboard.earnings;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import com.admindashboard.enums.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class EarningDTO {
    
    private Long id;

    @NotNull(message = "Driver ID cannot be null")
    @Min(value = 1, message = "Driver ID must be a positive number")
    private Long driverId;

    @NotBlank(message = "Driver name cannot be blank")
    @Size(max = 255, message = "Driver name cannot exceed 255 characters")
    private String driverName;

    @NotNull(message = "Amount cannot be null")
    @DecimalMin(value = "0.01", inclusive = true, message = "Amount must be greater than or equal to 0.01")
    private BigDecimal amount;

    @NotNull(message = "Transaction date cannot be null")
    @PastOrPresent(message = "Transaction date must be in the past or present")
    private LocalDateTime transactionDate;

    @NotNull(message = "Payment method cannot be null")
    private PaymentMethod paymentMethod;

    @NotNull(message = "Payment status cannot be null")
    private PaymentStatus paymentStatus;
}
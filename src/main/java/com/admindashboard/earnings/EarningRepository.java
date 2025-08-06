package com.admindashboard.earnings;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface EarningRepository extends JpaRepository<Earning, Long> {
    @Query("SELECT e FROM Earning e WHERE e.driver.id = :driverId")
    List<Earning> findByDriverId(@Param("driverId") Long driverId);
    
    // Keep these methods the same
    @Query("SELECT e FROM Earning e WHERE e.transactionDate BETWEEN :startDate AND :endDate")
    List<Earning> findByDateRange(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    List<Earning> findByPaymentStatus(PaymentStatus status);
}
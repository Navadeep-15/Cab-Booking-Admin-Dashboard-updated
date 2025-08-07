package com.admindashboard.ridelogs;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.admindashboard.enums.RideStatus;

import java.util.List;

/**
 * Repository for managing Ride entities.
 * Renamed from TripRepository to RideRepository.
 */
@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {

    List<Ride> findByDriverId(Long driverId);

    List<Ride> findByPassengerId(Long passengerId);

    List<Ride> findByPassengerIdAndStatus(Long passengerId, RideStatus status);

    List<Ride> findByDriverIdAndStatus(Long driverId, RideStatus status);

    List<Ride> findByStatus(RideStatus status);

    /**
     * Finds all rides where either customer feedback or driver feedback is not null.
     * Uses a JPQL query to explicitly define the condition, avoiding ambiguity
     * with Spring Data JPA's derived query parsing.
     * @return A list of rides with existing feedback.
     */
    @Query("SELECT r FROM Ride r WHERE r.customerFeedback IS NOT NULL OR r.driverFeedback IS NOT NULL")
    List<Ride> findRidesWithAnyFeedback();

    List<Ride> findByCustomerRatingLessThanOrCustomerFeedbackContainingIgnoreCase(int rating, String keyword);
}

package com.admindashboard.ridelogs;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long> {

    List<Trip> findByDriverId(Long driverId);

    List<Trip> findByPassengerId(Long passengerId);

    List<Trip> findByPassengerIdAndStatus(Long passengerId, TripStatus status);

    List<Trip> findByDriverIdAndStatus(Long driverId, TripStatus status);

    List<Trip> findByStatus(TripStatus status);

    List<Trip> findByCustomerFeedbackIsNotNullOrDriverFeedbackIsNotNull();

    List<Trip> findByCustomerRatingLessThanOrCustomerFeedbackContainingIgnoreCase(int rating, String keyword);
}

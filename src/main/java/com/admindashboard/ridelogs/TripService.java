package com.admindashboard.ridelogs;

import java.util.List;

public interface TripService {

    List<Trip> getAllTrips();

    Trip getTripById(Long tripId);

    List<Trip> getTripsByDriverId(Long driverId);

    List<Trip> getTripsByPassengerId(Long passengerId);

    List<Trip> getRideHistoryForPassenger(Long passengerId);

    List<Trip> getRideHistoryForDriver(Long driverId);

    List<Trip> getCancelledTrips();

    List<Trip> getTripsWithComplaints();

    List<Trip> getTripsWithFeedback();
}

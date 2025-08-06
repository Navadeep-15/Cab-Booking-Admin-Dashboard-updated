package com.admindashboard.ridelogs;

import com.admindashboard.exception.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TripServiceImpl implements TripService {

    private final TripRepository tripRepository;

    public TripServiceImpl(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    @Override
    public List<Trip> getAllTrips() {
        return tripRepository.findAll();
    }

    @Override
    public Trip getTripById(Long tripId) {
        return tripRepository.findById(tripId)
                .orElseThrow(() -> new EntityNotFoundException("Trip", tripId));
    }

    @Override
    public List<Trip> getTripsByDriverId(Long driverId) {
        List<Trip> trips = tripRepository.findByDriverId(driverId);
        if (trips.isEmpty()) {
            throw new EntityNotFoundException("Trips for Driver ID", driverId);
        }
        return trips;
    }

    @Override
    public List<Trip> getTripsByPassengerId(Long passengerId) {
        List<Trip> trips = tripRepository.findByPassengerId(passengerId);
        if (trips.isEmpty()) {
            throw new EntityNotFoundException("Trips for Passenger ID", passengerId);
        }
        return trips;
    }

    @Override
    public List<Trip> getRideHistoryForPassenger(Long passengerId) {
        return tripRepository.findByPassengerIdAndStatus(passengerId, TripStatus.COMPLETED);
    }

    @Override
    public List<Trip> getRideHistoryForDriver(Long driverId) {
        return tripRepository.findByDriverIdAndStatus(driverId, TripStatus.COMPLETED);
    }

    @Override
    public List<Trip> getCancelledTrips() {
        return tripRepository.findByStatus(TripStatus.CANCELLED);
    }

    @Override
    public List<Trip> getTripsWithComplaints() {
        return tripRepository.findByCustomerRatingLessThanOrCustomerFeedbackContainingIgnoreCase(3, "complaint");
    }

    @Override
    public List<Trip> getTripsWithFeedback() {
        return tripRepository.findByCustomerFeedbackIsNotNullOrDriverFeedbackIsNotNull();
    }
}

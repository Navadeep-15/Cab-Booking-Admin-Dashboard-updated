package com.admindashboard.ridelogs;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rides")
public class TripController {

    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @GetMapping
    public ResponseEntity<List<Trip>> getAllTrips() {
        return ResponseEntity.ok(tripService.getAllTrips());
    }

    @GetMapping("/{tripId}")
    public ResponseEntity<Trip> getTripById(@PathVariable Long tripId) {
        return ResponseEntity.ok(tripService.getTripById(tripId));
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<List<Trip>> getTripsByDriverId(@PathVariable Long driverId) {
        return ResponseEntity.ok(tripService.getTripsByDriverId(driverId));
    }

    @GetMapping("/passenger/{passengerId}")
    public ResponseEntity<List<Trip>> getTripsByPassengerId(@PathVariable Long passengerId) {
        return ResponseEntity.ok(tripService.getTripsByPassengerId(passengerId));
    }

    @GetMapping("/history/passenger/{passengerId}")
    public ResponseEntity<List<Trip>> getRideHistoryForPassenger(@PathVariable Long passengerId) {
        return ResponseEntity.ok(tripService.getRideHistoryForPassenger(passengerId));
    }

    @GetMapping("/history/driver/{driverId}")
    public ResponseEntity<List<Trip>> getRideHistoryForDriver(@PathVariable Long driverId) {
        return ResponseEntity.ok(tripService.getRideHistoryForDriver(driverId));
    }

    @GetMapping("/cancelled")
    public ResponseEntity<List<Trip>> getCancelledTrips() {
        return ResponseEntity.ok(tripService.getCancelledTrips());
    }

    @GetMapping("/complaints")
    public ResponseEntity<List<Trip>> getTripsWithComplaints() {
        return ResponseEntity.ok(tripService.getTripsWithComplaints());
    }

    @GetMapping("/feedback")
    public ResponseEntity<List<Trip>> getTripsWithFeedback() {
        return ResponseEntity.ok(tripService.getTripsWithFeedback());
    }
}

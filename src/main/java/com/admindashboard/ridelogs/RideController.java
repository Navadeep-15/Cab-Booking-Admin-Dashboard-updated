package com.admindashboard.ridelogs;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing Ride resources.
 * This controller now directly injects the RideService class, which
 * contains the implementation logic.
 */
@RestController
@RequestMapping("/api/rides")
public class RideController {

    private final RideService rideService;

    public RideController(RideService rideService) {
        this.rideService = rideService;
    }

    @PostMapping
    public ResponseEntity<RideDTO> createRide(@Valid @RequestBody RideDTO rideDTO) { // Changed return type to RideDTO
        RideDTO createdRide = rideService.createRide(rideDTO);
        return new ResponseEntity<>(createdRide, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<RideDTO>> getAllRides() { // Changed return type to List<RideDTO>
        return ResponseEntity.ok(rideService.getAllRides());
    }

    @GetMapping("/{rideId}")
    public ResponseEntity<RideDTO> getRideById(@PathVariable Long rideId) { // Changed return type to RideDTO
        return ResponseEntity.ok(rideService.getRideById(rideId));
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<List<RideDTO>> getRidesByDriverId(@PathVariable Long driverId) { // Changed return type to List<RideDTO>
        return ResponseEntity.ok(rideService.getRidesByDriverId(driverId));
    }

    @GetMapping("/passenger/{passengerId}")
    public ResponseEntity<List<RideDTO>> getRidesByPassengerId(@PathVariable Long passengerId) { // Changed return type to List<RideDTO>
        return ResponseEntity.ok(rideService.getRidesByPassengerId(passengerId));
    }

    @GetMapping("/history/passenger/{passengerId}")
    public ResponseEntity<List<RideDTO>> getRideHistoryForPassenger(@PathVariable Long passengerId) { // Changed return type to List<RideDTO>
        return ResponseEntity.ok(rideService.getRideHistoryForPassenger(passengerId));
    }

    @GetMapping("/history/driver/{driverId}")
    public ResponseEntity<List<RideDTO>> getRideHistoryForDriver(@PathVariable Long driverId) { // Changed return type to List<RideDTO>
        return ResponseEntity.ok(rideService.getRideHistoryForDriver(driverId));
    }

    @GetMapping("/cancelled")
    public ResponseEntity<List<RideDTO>> getCancelledRides() { // Changed return type to List<RideDTO>
        return ResponseEntity.ok(rideService.getCancelledRides());
    }

    @GetMapping("/complaints")
    public ResponseEntity<List<RideDTO>> getRidesWithComplaints() { // Changed return type to List<RideDTO>
        return ResponseEntity.ok(rideService.getRidesWithComplaints());
    }

    @GetMapping("/feedback")
    public ResponseEntity<List<RideDTO>> getRidesWithFeedback() { // Changed return type to List<RideDTO>
        return ResponseEntity.ok(rideService.getRidesWithFeedback());
    }

    @PutMapping("/{rideId}")
    public ResponseEntity<RideDTO> updateRide(@PathVariable Long rideId, @Valid @RequestBody RideDTO rideDTO) { // Changed return type to RideDTO
        RideDTO updatedRide = rideService.updateRide(rideId, rideDTO);
        return new ResponseEntity<>(updatedRide, HttpStatus.OK);
    }

    @DeleteMapping("/{rideId}")
    public ResponseEntity<Void> deleteRide(@PathVariable Long rideId) {
        rideService.deleteRide(rideId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

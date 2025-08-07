package com.admindashboard.driververification;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.admindashboard.enums.*;
import java.util.List;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @PostMapping("/register")
    public ResponseEntity<DriverResponseDTO> registerDriver(@Valid @RequestBody DriverDTO driverDTO) {
        DriverResponseDTO response = driverService.registerDriver(driverDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<DriverResponseDTO>> getAllDrivers() {
        List<DriverResponseDTO> drivers = driverService.getAllDrivers();
        return new ResponseEntity<>(drivers, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DriverResponseDTO> getDriverById(@PathVariable Long id) {
        DriverResponseDTO driver = driverService.getDriverById(id);
        return new ResponseEntity<>(driver, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DriverResponseDTO> updateDriver(@PathVariable Long id, @Valid @RequestBody DriverDTO driverDTO) {
        DriverResponseDTO updatedDriver = driverService.updateDriver(id, driverDTO);
        return new ResponseEntity<>(updatedDriver, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDriver(@PathVariable Long id) {
        driverService.deleteDriver(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}/verify")
    public ResponseEntity<DriverResponseDTO> verifyDriver(@PathVariable Long id) {
        DriverResponseDTO updatedDriver = driverService.updateDriverStatus(id, DriverStatus.VERIFIED); // Changed to VERIFIED
        return new ResponseEntity<>(updatedDriver, HttpStatus.OK);
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<DriverResponseDTO> rejectDriver(@PathVariable Long id) {
        DriverResponseDTO updatedDriver = driverService.updateDriverStatus(id, DriverStatus.REJECTED);
        return new ResponseEntity<>(updatedDriver, HttpStatus.OK);
    }
}

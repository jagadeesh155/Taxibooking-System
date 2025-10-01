package com.ben.Taxi_Booking.controller;

import com.ben.Taxi_Booking.entity.Driver;
import com.ben.Taxi_Booking.entity.Ride;
import com.ben.Taxi_Booking.exception.DriverException;
import com.ben.Taxi_Booking.service.DriverService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/driver")
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }



    @GetMapping("/profile")
    public ResponseEntity<Driver> getReqDriverProfileHandler(String jwt) throws DriverException {

        Driver driver = driverService.getReqDriverProfile(jwt);

        return new ResponseEntity
                <Driver>(driver, HttpStatus.OK);
    }

    @GetMapping("/{id}/currentRide")
    public ResponseEntity<Ride> getDriverCurrentRide(@PathVariable int id) throws DriverException {

        Ride ride = driverService.getDriverCurrentRide(id);

        return new ResponseEntity<>(ride, HttpStatus.OK);
    }

    @GetMapping("/{id}/allocatedRides")
    public ResponseEntity<List<Ride>> getAllocatedRidesHandler(@PathVariable int id) throws DriverException {

        List<Ride> allocatedRides = driverService.getAllocatedRides(id);

        return new ResponseEntity<>(allocatedRides, HttpStatus.OK);
    }


    @GetMapping("/rides/completed")
    public ResponseEntity<List<Ride>> getCompletedRidesHandler(@RequestHeader("Authorization") String jwt) throws DriverException {

        Driver driver = driverService.getReqDriverProfile(jwt);

        List<Ride> rides = driverService.completedRids(driver.getId());

        return new ResponseEntity<>(rides, HttpStatus.ACCEPTED);
    }
}

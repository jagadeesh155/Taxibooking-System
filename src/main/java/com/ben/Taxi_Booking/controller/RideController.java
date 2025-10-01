package com.ben.Taxi_Booking.controller;

import com.ben.Taxi_Booking.dto.RideDto;
import com.ben.Taxi_Booking.entity.Driver;
import com.ben.Taxi_Booking.entity.Ride;
import com.ben.Taxi_Booking.entity.User;
import com.ben.Taxi_Booking.exception.DriverException;
import com.ben.Taxi_Booking.exception.RideException;
import com.ben.Taxi_Booking.exception.UserException;
import com.ben.Taxi_Booking.mapper.RideMapper;
import com.ben.Taxi_Booking.request.RideRequest;
import com.ben.Taxi_Booking.request.StartRideRequest;
import com.ben.Taxi_Booking.service.DriverService;
import com.ben.Taxi_Booking.service.RideService;
import com.ben.Taxi_Booking.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ride")
public class RideController {

    private final RideService rideService;
    private final DriverService driverService;
    private final UserService userService;

    public RideController(RideService rideService, DriverService driverService, UserService userService) {
        this.rideService = rideService;
        this.driverService = driverService;
        this.userService = userService;
    }


    @PostMapping("/request")
    public ResponseEntity<RideDto> userRequestRide(@RequestBody RideRequest request) throws DriverException, UserException {

        // ⭐ 403 FIX: Get the authenticated user's email directly from the Security Context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        User user = userService.findUserByEmail(userEmail);

        Ride ride = rideService.requestRide(request, user);

        RideDto rideDto = RideMapper.toRideDto(ride);

        return new ResponseEntity<>(rideDto, HttpStatus.OK);
    }

    @PutMapping("/accept/{rideId}")
    public ResponseEntity<String> acceptRide(@PathVariable int rideId) throws DriverException, UserException, RideException {

        rideService.acceptRide(rideId);

        Driver driver = rideService.findRideById(rideId).getDriver();
        Ride ride = driver.getCurrRide();

        String message = "Ride Accepted";

        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @PutMapping("/reject/{rideId}")
    public ResponseEntity<String> rejectRide(@PathVariable int rideId, @RequestHeader("Authorization") String jwt) throws DriverException, UserException, RideException {

        // Note: For full consistency, this should also use SecurityContextHolder for the Driver profile
        Driver driver = driverService.getReqDriverProfile(jwt);

        rideService.declineRide(rideId, driver.getId());

        String message = "Ride declined";

        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @PutMapping("/start/{rideId}")
    public ResponseEntity<String> startRide(@PathVariable int rideId, @RequestBody StartRideRequest otp) throws DriverException, UserException, RideException {


        rideService.startRide(rideId, otp.getOtp());

        String message = "Ride Started";

        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @PutMapping("/complete/{rideId}")
    public ResponseEntity<String> completeRide(@PathVariable int rideId) throws DriverException, UserException, RideException {

        rideService.completeRide(rideId);

        String message = "Ride Completed";

        return new ResponseEntity<>(message, HttpStatus.OK);
    }


    @GetMapping("/{rideId}")
    public ResponseEntity<RideDto> findRideById(@PathVariable int rideId, @RequestHeader("Authorization") String jwt) throws DriverException, UserException, RideException {

        // Note: For full consistency, this should also use SecurityContextHolder for the User profile
        User user = userService.getUserProfile(jwt);

        Ride ride = rideService.findRideById(rideId);

        RideDto rideDto = RideMapper.toRideDto(ride);

        return new ResponseEntity<>(rideDto, HttpStatus.OK);
    }
}
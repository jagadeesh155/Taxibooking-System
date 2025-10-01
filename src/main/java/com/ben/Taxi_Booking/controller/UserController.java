package com.ben.Taxi_Booking.controller;


import com.ben.Taxi_Booking.dto.RideDto;
import com.ben.Taxi_Booking.entity.Ride;
import com.ben.Taxi_Booking.entity.User;
import com.ben.Taxi_Booking.exception.DriverException;
import com.ben.Taxi_Booking.exception.UserException;
import com.ben.Taxi_Booking.mapper.RideMapper;
import com.ben.Taxi_Booking.request.RideRequest;
import com.ben.Taxi_Booking.service.RideService;
import com.ben.Taxi_Booking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {


    @Autowired
    private UserService userService;

    @Autowired
    private RideService rideService;

    // --- RIDE BOOKING (Protected Endpoint) ---
    @PostMapping("/ride/book")
    public ResponseEntity<RideDto> bookRide(@RequestBody RideRequest request) throws DriverException, UserException {

        // ⭐ 403 FIX: Get user email from Security Context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        // Must have a findUserByEmail method
        User user = userService.findUserByEmail(userEmail);

        Ride ride = rideService.requestRide(request, user);

        RideDto rideDto = RideMapper.toRideDto(ride);

        return new ResponseEntity<>(rideDto, HttpStatus.OK);
    }
    // ------------------------------------------------------


    @GetMapping("/{userId}")
    public ResponseEntity<User> findUserById(@PathVariable int userId) throws UserException {
        User user = userService.findUserById(userId);
        return ResponseEntity.ok(user);
    }


    @GetMapping("/profile")
    public ResponseEntity<User> getUserProfile() throws UserException {
        // ⭐ 403 FIX: Get user email from Security Context, not raw JWT header
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        User user = userService.findUserByEmail(userEmail);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/rides/completed")
    public ResponseEntity<List<Ride>> getCompletedRides() throws UserException {
        // ⭐ 403 FIX: Get user email from Security Context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        User user = userService.findUserByEmail(userEmail);

        List<Ride> completedRides = userService.completedRide(user.getId());
        return ResponseEntity.ok(completedRides);
    }

    @GetMapping()
    public ResponseEntity<List<User>> getAllUsers() throws UserException {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
}
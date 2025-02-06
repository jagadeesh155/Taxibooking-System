package com.ben.Taxi_Booking.controller;


import com.ben.Taxi_Booking.entity.Ride;
import com.ben.Taxi_Booking.entity.User;
import com.ben.Taxi_Booking.exception.UserException;
import com.ben.Taxi_Booking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {


    @Autowired
    private UserService userService;


    @GetMapping("/{userId}")
    public ResponseEntity<User> findUserById(@PathVariable int userId) throws UserException {
        User user = userService.findUserById(userId);
        return ResponseEntity.ok(user);
    }


    @GetMapping("/profile")
    public ResponseEntity<User> getUserProfile(@RequestHeader("Authorization") String token) throws UserException, UserException {
        User user = userService.getUserProfile(token);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/rides/completed")
    public ResponseEntity<List<Ride>> getCompletedRides(@RequestHeader("Authorization") String token) throws UserException, UserException {
        User user = userService.getUserProfile(token);
        List<Ride> completedRides = userService.completedRide(user.getId());
        return ResponseEntity.ok(completedRides);
    }

    @GetMapping()
    public ResponseEntity<List<User>> getAllUsers() throws UserException {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
}

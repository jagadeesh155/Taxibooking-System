package com.ben.Taxi_Booking.service;



import com.ben.Taxi_Booking.entity.Ride;
import com.ben.Taxi_Booking.entity.User;
import com.ben.Taxi_Booking.exception.UserException;

import java.util.List;

public interface UserService {



    public User getUserProfile(String token) throws UserException;

    public User findUserByEmail(String email) throws UserException;

    public User findUserById(Integer id) throws UserException;

    public List<User> getAllUsers() throws UserException;

    public List<Ride> completedRide(Integer userId) throws UserException;
}

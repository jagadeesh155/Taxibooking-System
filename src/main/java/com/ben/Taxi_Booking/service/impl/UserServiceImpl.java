package com.ben.Taxi_Booking.service.impl;

import com.ben.Taxi_Booking.config.JwtProviderUtil;
import com.ben.Taxi_Booking.entity.Ride;
import com.ben.Taxi_Booking.entity.User;
import com.ben.Taxi_Booking.exception.UserException;
import com.ben.Taxi_Booking.repo.UserRepo;
import com.ben.Taxi_Booking.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final JwtProviderUtil jwtProviderUtil;

    public UserServiceImpl(UserRepo userRepo, JwtProviderUtil jwtProviderUtil) {
        this.userRepo = userRepo;
        this.jwtProviderUtil = jwtProviderUtil;
    }

    // This method is fine, but relies on a null check. Keeping it for now.
    @Override
    public User getUserProfile(String token) throws UserException {

        String email = jwtProviderUtil.getEmailFromToken(token);

        User user = userRepo.findByEmail(email);

        if(user != null) {
            return user;
        }

        throw new UserException("User not found");
    }

    // This method is fine, but relies on a null check. Keeping it for now.
    @Override
    public User findUserByEmail(String email) throws UserException {
        User user = userRepo.findByEmail(email);

        if(user != null) {
            return user;
        }

        throw new UserException("User not found");
    }

    // ⭐ CRITICAL FIX: Use orElseThrow to handle Optional correctly.
    @Override
    public User findUserById(Integer id) throws UserException {
        // Correct way to handle Optional: Throw the custom exception if the user is not found.
        User user = userRepo.findById(id)
                .orElseThrow(() -> new UserException("User not found with ID: " + id));

        return user;
    }

    @Override
    public List<User> getAllUsers() throws UserException {
        List<User> users = userRepo.findAll();

        if(users == null || users.isEmpty()) { // Added check for empty list
            throw new UserException("No User found");
        }

        return users;
    }

    @Override
    public List<Ride> completedRide(Integer userId) throws UserException {
        // Assuming getCompletedRides handles null/empty list or throws an exception internally
        List<Ride> completedRides = userRepo.getCompletedRides(userId);
        return completedRides;
    }
}
package com.ben.Taxi_Booking.service;

import com.ben.Taxi_Booking.entity.Driver;
import com.ben.Taxi_Booking.entity.User;
import com.ben.Taxi_Booking.repo.DriverRepo;
import com.ben.Taxi_Booking.repo.UserRepo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final DriverRepo driverRepo;
    private final UserRepo userRepo;

    public CustomUserDetailsService(DriverRepo driverRepo, UserRepo userRepo) {
        this.driverRepo = driverRepo;
        this.userRepo = userRepo;
    }

    /**
     * Loads the user details by email (username) from either the User or Driver repository.
     * @param username The email (used as username in Spring Security context).
     * @return UserDetails object containing email, encoded password, and role.
     * @throws UsernameNotFoundException if no user or driver is found with the given email.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // 1. Check for a Regular User
        User user = userRepo.findByEmail(username);

        if(user != null) {
            List<GrantedAuthority> authorities = new ArrayList<>();
            // Assign the correct role for authorization checks later
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

            return new org.springframework.security.core.userdetails.
                    User(user.getEmail(), user.getPassword(), authorities);
        }

        // 2. Check for a Driver
        Driver driver = driverRepo.findByEmail(username);

        if(driver != null) {
            List<GrantedAuthority> authorities = new ArrayList<>();
            // Assign the correct role for authorization checks later
            authorities.add(new SimpleGrantedAuthority("ROLE_DRIVER"));

            return new org.springframework.security.core.userdetails.
                    User(driver.getEmail(), driver.getPassword(), authorities);
        }

        // 3. If neither is found, throw an exception
        throw new UsernameNotFoundException("User or Driver not found with email: " + username);
    }
}

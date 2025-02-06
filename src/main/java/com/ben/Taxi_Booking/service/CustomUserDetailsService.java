package com.ben.Taxi_Booking.service;



import com.ben.Taxi_Booking.entity.Driver;
import com.ben.Taxi_Booking.entity.User;
import com.ben.Taxi_Booking.repo.DriverRepo;
import com.ben.Taxi_Booking.repo.UserRepo;
import org.springframework.security.core.GrantedAuthority;
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


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<GrantedAuthority> authorities = new ArrayList<>();

        User user = userRepo.findByEmail(username);

        if(user != null) {
            return new org.springframework.security.core.userdetails.
                    User(user.getEmail(), user.getPassword(), authorities);
        }

        Driver driver = driverRepo.findByEmail(username);

        if(driver != null) {
            return new org.springframework.security.core.userdetails.
                    User(driver.getEmail(), driver.getPassword(), authorities);
        }

        throw new UsernameNotFoundException("User not found");
    }
}


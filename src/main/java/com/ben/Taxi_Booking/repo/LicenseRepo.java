package com.ben.Taxi_Booking.repo;


import com.ben.Taxi_Booking.entity.License;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LicenseRepo extends JpaRepository<License,Integer> {
}

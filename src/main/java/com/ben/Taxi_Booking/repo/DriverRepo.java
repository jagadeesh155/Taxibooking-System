package com.ben.Taxi_Booking.repo;

import com.ben.Taxi_Booking.entity.Driver;
import com.ben.Taxi_Booking.entity.Ride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DriverRepo extends JpaRepository<Driver, Integer> {

    public Driver findByEmail(String email);

    @Query("select r from Ride r where r.status='REQUESTED' and r.driver.id=:driverId")
    List<Ride> getAllocatedRides(@Param("driverId") int driverId);


    @Query("select r from Ride r where r.status='COMPLETED' and r.driver.id=:driverId")
    List<Ride> getCompletedRides(@Param("driverId") int driverId);
}

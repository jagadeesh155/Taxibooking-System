package com.ben.Taxi_Booking.repo;

import com.ben.Taxi_Booking.entity.Driver;
import com.ben.Taxi_Booking.entity.Ride;
import com.ben.Taxi_Booking.enums.RideStatus;  // <-- NEW IMPORT
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DriverRepo extends JpaRepository<Driver, Integer> {

    public Driver findByEmail(String email);

    // FIXED: Use the full Enum constant path
    @Query("select r from Ride r where r.status = com.ben.Taxi_Booking.enums.RideStatus.REQUESTED and r.driver.id=:driverId")
    List<Ride> getAllocatedRides(@Param("driverId") int driverId);


    // FIXED: Use the full Enum constant path
    @Query("select r from Ride r where r.status = com.ben.Taxi_Booking.enums.RideStatus.COMPLETED and r.driver.id=:driverId")
    List<Ride> getCompletedRides(@Param("driverId") int driverId);
}
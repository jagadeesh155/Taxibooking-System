package com.ben.Taxi_Booking.repo;

import com.ben.Taxi_Booking.entity.Ride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RideRepo extends JpaRepository<Ride, Integer> {
}

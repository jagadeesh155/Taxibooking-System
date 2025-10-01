package com.ben.Taxi_Booking.repo;

import com.ben.Taxi_Booking.entity.Ride;
import com.ben.Taxi_Booking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepo extends JpaRepository<User, Integer> {

    public User findByEmail(String email);

    @Query("SELECT r FROM Ride r WHERE r.user.id = :userId AND r.status = com.ben.Taxi_Booking.enums.RideStatus.COMPLETED")
    List<Ride> getCompletedRides(@Param("userId") Integer userId);
}

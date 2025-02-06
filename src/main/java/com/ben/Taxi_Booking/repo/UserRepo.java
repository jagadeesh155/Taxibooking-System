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

    @Query("select r from Ride r where r.status='COMPLETED' and r.user.id=:userId")
    List<Ride> getCompletedRides(@Param("userId") Integer userId);
}

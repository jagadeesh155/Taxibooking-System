package com.ben.Taxi_Booking.service;


import com.ben.Taxi_Booking.entity.Driver;
import com.ben.Taxi_Booking.entity.Ride;
import com.ben.Taxi_Booking.entity.User;
import com.ben.Taxi_Booking.exception.DriverException;
import com.ben.Taxi_Booking.exception.RideException;
import com.ben.Taxi_Booking.request.RideRequest;
import com.stripe.net.StripeResponse;
import org.springframework.stereotype.Service;

@Service
public interface RideService {

    public Ride requestRide(RideRequest request, User user) throws DriverException;

    public Ride createRide(User user, Driver driver,
                           double pickupLatitude, double pickupLongitude,
                           double dropoffLatitude, double dropoffLongitude,
                           String pickupArea, String destinationArea);

    public void acceptRide(int rideId) throws RideException;

    public void declineRide(int rideId, int driverId) throws RideException;

    public void startRide(int rideId, int otp) throws RideException;

    public void completeRide(int rideId) throws RideException;

    public StripeResponse onlinePayment(Long amount);

    public void cancelRide(int rideId) throws RideException;

    public Ride findRideById(int rideId) throws RideException;
}

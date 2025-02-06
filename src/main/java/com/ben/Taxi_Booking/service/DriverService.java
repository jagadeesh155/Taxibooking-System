package com.ben.Taxi_Booking.service;



import com.ben.Taxi_Booking.entity.Driver;
import com.ben.Taxi_Booking.entity.Ride;
import com.ben.Taxi_Booking.exception.DriverException;
import com.ben.Taxi_Booking.request.DriverSignUpRequest;

import java.util.List;

public interface DriverService {

    public Driver registerDriver(DriverSignUpRequest driverSignUpRequest);

    public List<Driver> getAvailableDrivers(double pickupLatitude, double pickupLongitude,
                                             Ride ride);

    public Driver findNearestDrive(List<Driver> availableDrivers, double pickupLatitude, double pickupLongitude);

    public Driver getReqDriverProfile(String jwt) throws DriverException;

    public Ride getDriverCurrentRide(int driverId) throws DriverException;

    public List<Ride> getAllocatedRides(int driverId) throws DriverException;

    public Driver findDriverById(int driverId) throws DriverException;

    public List<Ride> completedRids(int driverId) throws DriverException;

}

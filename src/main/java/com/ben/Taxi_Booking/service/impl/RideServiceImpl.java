package com.ben.Taxi_Booking.service.impl;


import com.ben.Taxi_Booking.entity.Driver;
import com.ben.Taxi_Booking.entity.Ride;
import com.ben.Taxi_Booking.entity.User;
import com.ben.Taxi_Booking.enums.RideStatus;
import com.ben.Taxi_Booking.exception.DriverException;
import com.ben.Taxi_Booking.exception.RideException;
import com.ben.Taxi_Booking.repo.DriverRepo;
import com.ben.Taxi_Booking.repo.RideRepo;
import com.ben.Taxi_Booking.request.RideRequest;
import com.ben.Taxi_Booking.service.Calculaters;
import com.ben.Taxi_Booking.service.DriverService;
import com.ben.Taxi_Booking.service.EmailService;
import com.ben.Taxi_Booking.service.RideService;
import com.stripe.net.StripeResponse;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class RideServiceImpl implements RideService {

    private final RideRepo rideRepo;
    private final DriverService driverService;
    private final DriverRepo driverRepo;
    private final Calculaters calculaters;

    private final EmailService emailService;


    public RideServiceImpl(RideRepo rideRepo, DriverService driverService, DriverRepo driverRepo, Calculaters calculaters, EmailService emailService) {
        this.rideRepo = rideRepo;
        this.driverService = driverService;
        this.driverRepo = driverRepo;
        this.calculaters = calculaters;
        this.emailService = emailService;
    }

    @Override
    public Ride requestRide(RideRequest request, User user) throws DriverException {
        double pickupLatitude = request.getPickupLatitude();
        double pickupLongitude = request.getPickupLongitude();
        double dropoffLatitude = request.getDropoffLatitude();
        double dropoffLongitude = request.getDropoffLongitude();
        String pickupArea = request.getPickupArea();
        String destinationArea = request.getDestinationArea();

        Ride existingRide = new Ride();

        List<Driver> avaliableDrivers = driverService.getAvailableDrivers(pickupLatitude, pickupLongitude,
                 existingRide);

        Driver nearestDriver = driverService.findNearestDrive(avaliableDrivers, pickupLatitude, pickupLongitude);

        if(nearestDriver == null) {
            throw new DriverException("No driver available");
        }

        Ride ride = createRide(user, nearestDriver,
                pickupLatitude, pickupLongitude,
                dropoffLatitude, dropoffLongitude, pickupArea, destinationArea);

        return ride;

    }

    @Override
    public Ride createRide(User user, Driver driver, double pickupLatitude, double pickupLongitude, double dropoffLatitude, double dropoffLongitude, String pickupArea, String destinationArea) {
        Ride ride = new Ride();

        ride.setDriver(driver);
        ride.setUser(user);
        ride.setPickupLatitude(pickupLatitude);
        ride.setPickupLongitude(pickupLongitude);
        ride.setDropoffLatitude(dropoffLatitude);
        ride.setDropoffLongitude(dropoffLongitude);
        ride.setStatus(RideStatus.REQUESTED);
        ride.setPickupArea(pickupArea);
        ride.setDestinationArea(destinationArea);

        Ride savedRide = rideRepo.save(ride);

        return savedRide;
    }

    @Override
    public void acceptRide(int rideId) throws RideException {

        Ride ride = findRideById(rideId);

        ride.setStatus(RideStatus.ACCEPTED);

        Driver driver = ride.getDriver();

        driver.setCurrRide(ride);

         Random random = new Random();

         int otp = random.nextInt(10000);

         emailService.sendOtp(driver.getEmail(), otp);

         ride.setOtp(otp);

         driverRepo.save(driver);

         rideRepo.save(ride);
    }

    @Override
    public void declineRide(int rideId, int driverId) throws RideException {

        Ride ride = findRideById(rideId);

        ride.getDeclinedDrivers().add(driverId);

        List<Driver> availableDrivers = driverService
                .getAvailableDrivers(ride.getPickupLatitude(), ride.getPickupLongitude(), ride);

        Driver nearestDriver = driverService.findNearestDrive(availableDrivers, ride.getPickupLatitude(), ride.getPickupLongitude());

        ride.setDriver(nearestDriver);

        rideRepo.save(ride);
    }

    @Override
    public void startRide(int rideId, int otp) throws RideException {

        Ride ride = findRideById(rideId);

        if(otp != ride.getOtp()) {
            throw new RideException("Invalid OTP");
        }

        ride.setStatus(RideStatus.STARTED);
        ride.setStartTime(LocalDateTime.now());

        rideRepo.save(ride);
    }

    @Override
    public void completeRide(int rideId) throws RideException{

        Ride ride = findRideById(rideId);

        ride.setStatus(RideStatus.COMPLETED);
        ride.setEndTime(LocalDateTime.now());

        double distance = calculaters.calculateDistance(ride.getDropoffLatitude(),
                ride.getDropoffLongitude(), ride.getPickupLatitude(), ride.getPickupLongitude());

        LocalDateTime start = ride.getStartTime();

        LocalDateTime end = ride.getEndTime();

        Duration duration = Duration.between(start, end);

        long seconds = duration.getSeconds();

        double fair = calculaters.calculateFair(distance);

        ride.setDistance(Math.round(distance * 100.0) / 100.0);
        ride.setFair((int) Math.round(fair));
        ride.setDuration(seconds);
        ride.setEndTime(LocalDateTime.now());


        Driver driver = ride.getDriver();
        driver.getRides().add(ride);
        driver.setCurrRide(null);

        Integer driverRevenue = (int) (driver.getTotalEarning() + Math.round(fair * 0.75));
        driver.setTotalEarning(driverRevenue);

        driverRepo.save(driver);

        rideRepo.save(ride);
    }

    @Override
    public StripeResponse onlinePayment(Long amount) {


        return null;
    }

    @Override
    public void cancelRide(int rideId) throws RideException {
        Ride ride = findRideById(rideId);
        ride.setStatus(RideStatus.CANCELLED);

        rideRepo.save(ride);
    }

    @Override
    public Ride findRideById(int rideId) throws RideException {
        Ride ride = rideRepo.findById(rideId).get();

        return ride;
    }
}

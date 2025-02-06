package com.ben.Taxi_Booking.service.impl;


import com.ben.Taxi_Booking.config.JwtProviderUtil;
import com.ben.Taxi_Booking.entity.Driver;
import com.ben.Taxi_Booking.entity.License;
import com.ben.Taxi_Booking.entity.Ride;
import com.ben.Taxi_Booking.entity.Vehicle;
import com.ben.Taxi_Booking.enums.RideStatus;
import com.ben.Taxi_Booking.enums.UserRole;
import com.ben.Taxi_Booking.exception.DriverException;
import com.ben.Taxi_Booking.repo.DriverRepo;
import com.ben.Taxi_Booking.repo.LicenseRepo;
import com.ben.Taxi_Booking.repo.RideRepo;
import com.ben.Taxi_Booking.repo.VehicleRepo;
import com.ben.Taxi_Booking.request.DriverSignUpRequest;
import com.ben.Taxi_Booking.service.Calculaters;
import com.ben.Taxi_Booking.service.DriverService;
import com.ben.Taxi_Booking.service.EmailService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DriverServiceImpl implements DriverService {

    private final DriverRepo driverRepo;

    private final VehicleRepo vehicleRepo;
    private final LicenseRepo licenseRepo;
    private final RideRepo rideRepo;
    private final Calculaters distanceCalculator;

    private final PasswordEncoder passwordEncoder;

    private final JwtProviderUtil jwtProviderUtil;

    private final EmailService emailService;

    public DriverServiceImpl(DriverRepo driverRepo, VehicleRepo vehicleRepo, LicenseRepo licenseRepo, RideRepo rideRepo, Calculaters distanceCalculator, PasswordEncoder passwordEncoder, JwtProviderUtil jwtProviderUtil, EmailService emailService) {
        this.driverRepo = driverRepo;
        this.vehicleRepo = vehicleRepo;
        this.licenseRepo = licenseRepo;
        this.rideRepo = rideRepo;
        this.distanceCalculator = distanceCalculator;
        this.passwordEncoder = passwordEncoder;
        this.jwtProviderUtil = jwtProviderUtil;
        this.emailService = emailService;
    }


    @Override
    public Driver registerDriver(DriverSignUpRequest driverSignUpRequest) {
        License license = driverSignUpRequest.getLicense();
        Vehicle vehicle = driverSignUpRequest.getVehicle();

        License createdLicense = new License();

        createdLicense.setLicenseNumber(license.getLicenseNumber());
        createdLicense.setLicenseState(license.getLicenseState());
        createdLicense.setLicenseExpiryDate(license.getLicenseExpiryDate());
        createdLicense.setId(license.getId());

        License savedLicense = licenseRepo.save(createdLicense);

        Vehicle createdVehicle = new Vehicle();

        createdVehicle.setCapacity(vehicle.getCapacity());
        createdVehicle.setColor(vehicle.getColor());
        createdVehicle.setId(vehicle.getId());
        createdVehicle.setLicenseNumber(vehicle.getLicenseNumber());
        createdVehicle.setMake(vehicle.getMake());
        createdVehicle.setModel(vehicle.getModel());
        createdVehicle.setYear(vehicle.getYear());

        Vehicle savedVehicle = vehicleRepo.save(createdVehicle);

        Driver driver = new Driver();

        String pass = driver.getPassword();
        String password = passwordEncoder.encode(driverSignUpRequest.getPassword());

        driver.setEmail(driverSignUpRequest.getEmail());
        driver.setName(driverSignUpRequest.getName());
        driver.setMobile(driverSignUpRequest.getMobile());
        driver.setPassword(password);
        driver.setLicense(savedLicense);
        driver.setVehicle(savedVehicle);
        driver.setRole(UserRole.DRIVER);

        Driver createDriver = driverRepo.save(driver);

        emailService.driverRegistrationEmail(createDriver.getEmail(), createDriver.getName(), pass);


        savedLicense.setDriver(createDriver);
        savedVehicle.setDriver(createDriver);

        licenseRepo.save(savedLicense);
        vehicleRepo.save(savedVehicle);

        return createDriver;

    }

    @Override
    public List<Driver> getAvailableDrivers(double pickupLatitude, double pickupLongitude,  Ride ride) {

        List<Driver> allDriver = driverRepo.findAll();

        List<Driver> availableDriver = new ArrayList<>();

        for(Driver driver : allDriver) {

            if(driver.getCurrRide()!=null && driver.getCurrRide().getStatus() != RideStatus.COMPLETED ) {
                continue;
            }

            if (ride.getDeclinedDrivers().contains(driver.getId())) {
                continue;
            }

            double driverLatitude = driver.getLatitude();
            double driverLongitude = driver.getLongitude();

            double distance = distanceCalculator.calculateDistance(pickupLatitude, pickupLongitude, driverLatitude, driverLongitude);

           availableDriver.add(driver);

        }

        return availableDriver;
    }

    @Override
    public Driver findNearestDrive(List<Driver> availableDrivers, double pickupLatitude, double pickupLongitude) {
        double minDistance = Double.MAX_VALUE;

        Driver nearestDriver = null;
        for(Driver driver : availableDrivers) {
            double driverLatitude = driver.getLatitude();
            double driverLongitude = driver.getLongitude();

            double distance = distanceCalculator.calculateDistance(pickupLatitude, pickupLongitude, driverLatitude, driverLongitude);

            if(distance < minDistance) {
                minDistance = distance;
                nearestDriver = driver;
            }
        }

        return nearestDriver;
    }

    @Override
    public Driver getReqDriverProfile(String jwt) throws DriverException {
        String email = jwtProviderUtil.getEmailFromToken(jwt);

        Driver driver = driverRepo.findByEmail(email);

        if(driver == null) {
            throw new DriverException("Driver not found with this email" + email);
        }
        return driver;
    }

    @Override
    public Ride getDriverCurrentRide(int driverId) throws DriverException {
        Driver driver = findDriverById(driverId);
        return driver.getCurrRide();
    }

    @Override
    public List<Ride> getAllocatedRides(int driverId) throws DriverException {
        List<Ride> allocatedRides = driverRepo.getAllocatedRides(driverId);
        return allocatedRides;
    }

    @Override
    public Driver findDriverById(int driverId) throws DriverException {
        return driverRepo.findById(driverId).get();
    }

    @Override
    public List<Ride> completedRids(int driverId) throws DriverException {
        List<Ride> completedRides = driverRepo.getCompletedRides(driverId);
        return completedRides;
    }
}

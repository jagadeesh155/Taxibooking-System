package com.ben.Taxi_Booking.mapper;


import com.ben.Taxi_Booking.dto.DriverDto;
import com.ben.Taxi_Booking.entity.Driver;

public class DriverMapper {

    public static DriverDto toDriverDto(Driver driver) {
        DriverDto driverDto = new DriverDto();

        driverDto.setEmail(driver.getEmail());
        driverDto.setId(driver.getId());
        driverDto.setLatitude(driver.getLatitude());
        driverDto.setLongitude(driver.getLongitude());
        driverDto.setMobile(driver.getMobile());
        driverDto.setName(driver.getName());
        driverDto.setRating(driver.getRating());
        driverDto.setRole(driver.getRole());
        driverDto.setVehicle(driver.getVehicle());

        return driverDto;
    }
}

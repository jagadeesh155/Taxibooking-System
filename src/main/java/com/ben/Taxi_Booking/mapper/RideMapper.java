package com.ben.Taxi_Booking.mapper;


import com.ben.Taxi_Booking.dto.DriverDto;
import com.ben.Taxi_Booking.dto.RideDto;
import com.ben.Taxi_Booking.dto.UserDto;
import com.ben.Taxi_Booking.entity.Ride;

public class RideMapper {

    private static DriverMapper driverMapper;
    private static UserMapper userMapper;

    public RideMapper(DriverMapper driverMapper, UserMapper userMapper) {
        this.driverMapper = driverMapper;
        this.userMapper = userMapper;
    }

    public static RideDto toRideDto(Ride ride) {

        DriverDto driverDto = driverMapper.toDriverDto(ride.getDriver());
        UserDto userDto = userMapper.toUserDto(ride.getUser());

        RideDto rideDto = new RideDto();

        rideDto.setDropoffLatitude(ride.getDropoffLatitude());
        rideDto.setDropoffLongitude(ride.getDropoffLongitude());
        rideDto.setDuration(ride.getDuration());
        rideDto.setDistance(ride.getDistance());
        rideDto.setFair(ride.getFair());
        rideDto.setEndTime(ride.getEndTime());
        rideDto.setId(ride.getId());
        rideDto.setOtp(ride.getOtp());
        rideDto.setPickupArea(ride.getPickupArea());
        rideDto.setPickupLatitude(ride.getPickupLatitude());
        rideDto.setPickupLongitude(ride.getPickupLongitude());
        rideDto.setPickupArea(ride.getPickupArea());
        rideDto.setDestinationArea(ride.getDestinationArea());
        rideDto.setStartTime(ride.getStartTime());
        rideDto.setStatus(ride.getStatus());
        rideDto.setUser(userDto);
        rideDto.setDriver(driverDto);

        return rideDto;

    }
}

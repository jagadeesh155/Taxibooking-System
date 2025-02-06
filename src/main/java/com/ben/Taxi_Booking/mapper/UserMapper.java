package com.ben.Taxi_Booking.mapper;


import com.ben.Taxi_Booking.dto.UserDto;
import com.ben.Taxi_Booking.entity.User;

public class UserMapper {

    public static UserDto toUserDto(User user) {
        UserDto userDto = new UserDto();

        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setName(user.getName());
        userDto.setMobile(user.getMobile());

        return userDto;
    }
}

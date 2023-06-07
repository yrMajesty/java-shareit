package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserRequest;
import ru.practicum.shareit.user.dto.UserResponse;

import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {

    public static UserResponse objectToDto(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    public static User dtoToObject(UserRequest userDto) {
        return User.builder()
                .email(userDto.getEmail())
                .name(userDto.getName())
                .build();
    }

    public static List<UserResponse> objectToDto(List<User> users) {
        return users.stream()
                .map(UserMapper::objectToDto)
                .collect(Collectors.toList());
    }
}
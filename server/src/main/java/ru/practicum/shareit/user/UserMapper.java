package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {

    public static UserResponseDto objectToDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    public static User dtoToObject(UserRequestDto userDto) {
        return User.builder()
                .email(userDto.getEmail())
                .name(userDto.getName())
                .build();
    }

    public static User dtoToObject(UserResponseDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .email(userDto.getEmail())
                .name(userDto.getName())
                .build();
    }

    public static List<UserResponseDto> objectToDto(List<User> users) {
        return users.stream()
                .map(UserMapper::objectToDto)
                .collect(Collectors.toList());
    }
}

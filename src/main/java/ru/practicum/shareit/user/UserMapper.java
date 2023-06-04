package ru.practicum.shareit.user;

import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {

    public static UserDto objectToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    public static User dtoToObject(UserDto userDto) {
        return User.builder()
                .email(userDto.getEmail())
                .name(userDto.getName())
                .build();
    }

    public static List<UserDto> objectToDto(List<User> users) {
        return users.stream()
                .map(UserMapper::objectToDto)
                .collect(Collectors.toList());
    }
}

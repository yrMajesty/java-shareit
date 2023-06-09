package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.util.List;

public interface UserService {
    UserResponseDto createUser(UserRequestDto userRequestDto);

    List<UserResponseDto> getAllUsers();

    UserResponseDto updateUserById(UserRequestDto userRequestDto, Long userId);

    UserResponseDto getUserById(Long userId);

    User findUserById(Long userId);

    void deleteUserById(Long id);

    void checkExistUserById(Long userId);
}

package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserRequest;
import ru.practicum.shareit.user.dto.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse createUser(UserRequest userRequest);

    List<UserResponse> getAllUsers();

    UserResponse updateUserById(UserRequest userRequest, Long userId);

    UserResponse getUserById(Long userId);

    User findUserById(Long userId);

    void deleteUserById(Long id);

    void checkExistUserById(Long userId);
}

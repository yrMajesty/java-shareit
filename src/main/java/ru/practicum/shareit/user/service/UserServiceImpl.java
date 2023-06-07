package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NoFoundObjectException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserResponse createUser(UserRequest userRequest) {
        User newUser = UserMapper.dtoToObject(userRequest);
        return UserMapper.objectToDto(userRepository.save(newUser));
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return UserMapper.objectToDto(userRepository.findAll());
    }

    @Override
    @Transactional
    public UserResponse updateUserById(UserRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoFoundObjectException(String.format("User with id='%s' not found", userId)));

        if (request.getEmail() != null && !Objects.equals(request.getEmail(), user.getEmail())) {
            user.setEmail(request.getEmail());
        }

        if (request.getName() != null) {
            user.setName(request.getName());
        }

        User savedUser = userRepository.save(user);

        return UserMapper.objectToDto(savedUser);
    }

    @Override
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoFoundObjectException(String.format("User with id='%s' not found", userId)));

        return UserMapper.objectToDto(user);
    }

    @Override
    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoFoundObjectException(String.format("User with id='%s' not found", userId)));
    }

    @Override
    public void checkExistUserById(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NoFoundObjectException(String.format("User with id='%s' not found", userId));
        }
    }

    @Override
    @Transactional
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

}

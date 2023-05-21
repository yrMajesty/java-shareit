package ru.practicum.shareit.user.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ExistEmailException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.MemoryUserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final MemoryUserRepository userRepository;

    @Override
    public UserDto create(UserDto userDto) {
        checkExistEmail(userDto.getEmail());

        User newUser = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userRepository.save(newUser));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return UserMapper.toListUserDtos(userRepository.findAll());
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long userId) {
        User foundUser = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(String.format("User with id='%S' not found", userId)));

        if (userDto.getEmail() != null && !Objects.equals(userDto.getEmail(), foundUser.getEmail())) {
            checkExistEmail(userDto.getEmail());
            foundUser.setEmail(userDto.getEmail());
        }

        if (userDto.getName() != null) {
            foundUser.setName(userDto.getName());
        }

        return UserMapper.toUserDto(userRepository.updateById(foundUser, userId));
    }

    @Override
    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(String.format("User with id='%S' not found", userId)));

        return UserMapper.toUserDto(user);
    }

    @Override
    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(String.format("User with id='%S' not found", userId)));
    }

    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    private void checkExistEmail(String email) {
        if (userRepository.isExistEmail(email)) {
            String error = String.format("Email %s already exist", email);
            throw new ExistEmailException(error);
        }
    }
}
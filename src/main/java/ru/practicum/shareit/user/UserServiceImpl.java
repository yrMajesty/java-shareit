package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NoFoundObjectException;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        User newUser = UserMapper.dtoToObject(userRequestDto);
        return UserMapper.objectToDto(userRepository.save(newUser));
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        return UserMapper.objectToDto(userRepository.findAll());
    }

    @Override
    @Transactional
    public UserResponseDto updateUserById(UserRequestDto request, Long userId) {
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
    public UserResponseDto getUserById(Long userId) {
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

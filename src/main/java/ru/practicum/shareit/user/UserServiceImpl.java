package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ExistEmailException;

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

        User newUser = UserMapper.dtoToObject(userDto);
        return UserMapper.objectToDto(userRepository.save(newUser));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return UserMapper.objectToDto(userRepository.findAll());
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

        return UserMapper.objectToDto(userRepository.updateById(foundUser, userId));
    }

    @Override
    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(String.format("User with id='%S' not found", userId)));

        return UserMapper.objectToDto(user);
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

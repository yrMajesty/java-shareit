package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserResponseDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public UserResponseDto getUser(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }

    @PostMapping
    public UserResponseDto createUser(@Valid @RequestBody UserRequestDto userRequestDto) {
        return userService.createUser(userRequestDto);
    }

    @PatchMapping("/{userId}")
    public UserResponseDto updateUser(@RequestBody UserRequestDto userRequestDto, @PathVariable Long userId) {
        return userService.updateUserById(userRequestDto, userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUserById(userId);
    }
}

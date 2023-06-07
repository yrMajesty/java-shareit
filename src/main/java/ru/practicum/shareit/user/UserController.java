package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public UserResponse getUser(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }

    @PostMapping
    public UserResponse createUser(@Valid @RequestBody UserRequest userRequest) {
        return userService.createUser(userRequest);
    }

    @PatchMapping("/{userId}")
    public UserResponse updateUser(@RequestBody UserRequest userRequest, @PathVariable Long userId) {
        return userService.updateUserById(userRequest, userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUserById(userId);
    }
}

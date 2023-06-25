package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceIntegrationTest {
    private final UserService userService;

    @Test
    void createUser_shouldException_createUserWithNotUniqueEmail() {
        UserRequestDto userRequest1 = UserRequestDto.builder().name("username").email("uniquemail@mail.mail").build();
        UserRequestDto userRequest2 = UserRequestDto.builder().name("secondUser").email("uniquemail@mail.mail").build();

        UserResponseDto user1 = userService.createUser(userRequest1);

        User foundUser1 = userService.findUserById(user1.getId());
        assertEquals("uniquemail@mail.mail", user1.getEmail());
        assertNotNull(foundUser1);

        assertThrows(DataIntegrityViolationException.class, () -> userService.createUser(userRequest2));
    }
}
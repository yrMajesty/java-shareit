package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.exception.NoFoundObjectException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceIntegrationTest {
    private final UserService userService;
    private final EntityManager entityManager;

    @BeforeEach
    void prepare() {
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY = 0; " +
                "TRUNCATE TABLE comments, items, users RESTART IDENTITY;" +
                "SET REFERENTIAL_INTEGRITY = 1;");
    }

    @Test
    void createUser_shouldException_createUserWithNotUniqueEmail() {
        UserRequestDto userRequest1 = UserRequestDto.builder().name("username").email("uniquemail@mail.mail").build();
        UserRequestDto userRequest2 = UserRequestDto.builder().name("secondUser").email("uniquemail@mail.mail").build();

        UserResponseDto user1 = userService.createUser(userRequest1);

        User foundUser1 = userService.findUserById(user1.getId());
        Assertions.assertEquals("uniquemail@mail.mail", user1.getEmail());
        assertNotNull(foundUser1);

        assertThrows(DataIntegrityViolationException.class, () -> userService.createUser(userRequest2));
    }

    @Test
    void getAllUsers_emptyList_usersDoNotExist() {
        List<UserResponseDto> result = userService.getAllUsers();

        org.assertj.core.api.Assertions.assertThat(result).isEmpty();
    }

    @Test
    void getAllUsers_notEmptyList_usersExist() {
        UserRequestDto userRequest1 = UserRequestDto.builder().name("firstUser").email("first@mail.mail").build();
        UserRequestDto userRequest2 = UserRequestDto.builder().name("secondUser").email("second@mail.mail").build();

        UserResponseDto user1 = userService.createUser(userRequest1);
        UserResponseDto user2 = userService.createUser(userRequest2);

        List<UserResponseDto> result = userService.getAllUsers();

        org.assertj.core.api.Assertions.assertThat(result).isNotEmpty();
        org.assertj.core.api.Assertions.assertThat(result.get(0).getEmail()).isEqualTo(user1.getEmail());
        org.assertj.core.api.Assertions.assertThat(result.get(1).getName()).isEqualTo(user2.getName());
    }

    @Test
    void updateUserById_updatedUser_userExist() {
        UserRequestDto createRequest = UserRequestDto.builder().name("firstUser").email("first@mail.mail").build();
        UserRequestDto updateRequest = UserRequestDto.builder().name("new name").email("first@mail.mail").build();

        UserResponseDto user1 = userService.createUser(createRequest);

        org.assertj.core.api.Assertions.assertThat(userService.getUserById(user1.getId())).isNotNull();

        UserResponseDto result = userService.updateUserById(updateRequest, user1.getId());

        org.assertj.core.api.Assertions.assertThat(result).isNotNull();
        org.assertj.core.api.Assertions.assertThat(result.getName()).isEqualTo(updateRequest.getName());
    }

    @Test
    void updateUserById_noFoundObjectException_userDoesNotExist() {
        UserRequestDto updateRequest = UserRequestDto.builder().name("new name").email("first@mail.mail").build();

        assertThrows(NoFoundObjectException.class, () -> userService.updateUserById(updateRequest, 100L));
    }

    @Test
    void findUserById_noFoundObjectException_userDoesNotExist() {
        assertThrows(NoFoundObjectException.class, () -> userService.findUserById(100L));
    }

    @Test
    void findUserById_foundUser_userExist() {
        UserRequestDto userRequest = UserRequestDto.builder().name("username").email("uniquemail@mail.mail").build();
        UserResponseDto savedUser = userService.createUser(userRequest);

        User foundUser = userService.findUserById(savedUser.getId());

        org.assertj.core.api.Assertions.assertThat(foundUser.getEmail()).isEqualTo(userRequest.getEmail());

    }

    @Test
    void getUserById_noFoundObjectException_userDoesNotExist() {
        assertThrows(NoFoundObjectException.class, () -> userService.getUserById(100L));
    }

    @Test
    void getUserById_foundUser_userExist() {
        UserRequestDto userRequest = UserRequestDto.builder().name("username").email("uniquemail@mail.mail").build();
        UserResponseDto savedUser = userService.createUser(userRequest);

        UserResponseDto foundUser = userService.getUserById(savedUser.getId());

        org.assertj.core.api.Assertions.assertThat(foundUser.getEmail()).isEqualTo(userRequest.getEmail());

    }

    @Test
    void checkExistUserById_notNoFoundObjectException_userDoesNotExist() {
        UserRequestDto userRequest = UserRequestDto.builder().name("username").email("uniquemail@mail.mail").build();
        UserResponseDto savedUser = userService.createUser(userRequest);

        assertDoesNotThrow(() -> userService.checkExistUserById(savedUser.getId()));
    }

    @Test
    void checkExistUserById_noFoundObjectException_userDoesNotExist() {
        assertThrows(NoFoundObjectException.class, () -> userService.checkExistUserById(100L));
    }

    @Test
    void deleteUserById_foundUser_userExist() {
        UserRequestDto userRequest = UserRequestDto.builder().name("username").email("uniquemail@mail.mail").build();
        UserResponseDto savedUser = userService.createUser(userRequest);

        UserResponseDto foundUser = userService.getUserById(savedUser.getId());

        userService.deleteUserById(foundUser.getId());

        assertThrows(NoFoundObjectException.class, () -> userService.getUserById(foundUser.getId()));
    }

}
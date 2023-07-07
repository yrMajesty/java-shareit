package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.exception.NoFoundObjectException;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserServiceImplTest {

    @Autowired
    UserServiceImpl underTest;

    @MockBean
    UserRepository userRepository;

    User user1;

    User user2;

    @BeforeEach
    void prepare() {
        user1 = User.builder()
                .id(1L)
                .name("Nikita")
                .email("nikita@mail.ru")
                .build();

        user2 = User.builder()
                .id(2L)
                .name("Mike")
                .email("mike@mail.ru")
                .build();
    }

    @Test
    void createUser_successfulAdd_userIdAndRequestAreCorrect() {
        UserRequestDto request = UserRequestDto.builder()
                .name("Nikita")
                .email("nikita@mail.ru")
                .build();

        when(userRepository.save(any(User.class))).thenReturn(user1);

        underTest.createUser(request);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void getAllUsers_notEmptyUsersList_usersExists() {
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<UserResponseDto> results = underTest.getAllUsers();

        verify(userRepository, times(1)).findAll();

        assertFalse(results.isEmpty());
    }

    @Test
    void getAllUsers_emptyUsersList_usersDoNotExists() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<UserResponseDto> results = underTest.getAllUsers();

        verify(userRepository, times(1)).findAll();

        assertTrue(results.isEmpty());
    }

    @Test
    void updateUserById_notFoundObjectException_userIdIsIncorrect() {
        UserRequestDto request = UserRequestDto.builder()
                .name("Nikita")
                .email("nikita@mail.ru")
                .build();

        doThrow(NoFoundObjectException.class)
                .when(userRepository).findById(anyLong());

        assertThrows(NoFoundObjectException.class, () -> underTest.updateUserById(request, 1L));
    }


    @Test
    void updateUserById_successfulUpdate_userIdIsCorrect() {
        UserRequestDto request = UserRequestDto.builder()
                .name("Nikita new")
                .email("nikita555@mail.ru")
                .build();

        User newUser = User.builder()
                .id(1L)
                .name("Nikita new")
                .email("nikita555@mail.ru")
                .build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user1));
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        UserResponseDto result = underTest.updateUserById(request, 1L);

        verify(userRepository, times(1)).save(any(User.class));

        assertThat(result.getName()).isEqualTo("Nikita new");
        assertThat(result.getEmail()).isEqualTo(request.getEmail());
    }

    @Test
    void getUserById_correctUser_userIsExist() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user1));

        UserResponseDto result = underTest.getUserById(1L);

        verify(userRepository, times(1)).findById(anyLong());

        assertNotNull(result);
        assertThat(result.getName()).isEqualTo("Nikita");
    }

    @Test
    void getUserById_notFoundObjectException_userIdIsIncorrect() {
        doThrow(NoFoundObjectException.class)
                .when(userRepository).findById(anyLong());

        assertThrows(NoFoundObjectException.class, () -> underTest.getUserById(100L));
    }

    @Test
    void findUserById_notFoundObjectException_userIdIsIncorrect() {
        doThrow(NoFoundObjectException.class)
                .when(userRepository).findById(anyLong());

        assertThrows(NoFoundObjectException.class, () -> underTest.findUserById(100L));
    }

    @Test
    void findUserById_correctUser_userIsExist() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));

        User result = underTest.findUserById(1L);

        verify(userRepository, times(1)).findById(anyLong());
        assertNotNull(result);
        assertThat(result.getName()).isEqualTo("Nikita");
    }

    @Test
    void checkExistUserById_notFoundObjectException_userIdIsIncorrect() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NoFoundObjectException.class, () -> underTest.checkExistUserById(100L));
    }

    @Test
    void checkExistUserById_returnTrue_userIsExist() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));

        underTest.checkExistUserById(1L);
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void deleteUserById_successfulDelete_userIsExist() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));

        underTest.deleteUserById(1L);

        verify(userRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void deleteUserById_notFoundObjectException_userIdIsIncorrect() {
        doThrow(NoFoundObjectException.class)
                .when(userRepository).findById(anyLong());

        assertThrows(NoFoundObjectException.class, () -> underTest.deleteUserById(100L));
    }
}
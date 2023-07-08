package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.exception.NoFoundObjectException;
import ru.practicum.shareit.user.dto.UserRequestDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    UserService userService;

    @Autowired
    ObjectMapper objectMapper;


    @Test
    void getAllUsers_statusOk_usersDoNotExist() throws Exception {
        when(userService.getAllUsers())
                .thenReturn(List.of());

        mvc.perform(MockMvcRequestBuilders.get("/users"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").doesNotExist());
    }

    @Test
    void getAllUsers_statusOk_usersExist() throws Exception {
        User user = User.builder()
                .id(1L)
                .name("Mike")
                .email("mike@mail.ru")
                .build();

        when(userService.getAllUsers())
                .thenReturn(UserMapper.objectToDto(List.of(user)));

        mvc.perform(MockMvcRequestBuilders.get("/users"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Mike"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].email").isString())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].email").value("mike@mail.ru"));
    }

    @Test
    void getUser_statusNotFound_userDoesNotExist() throws Exception {
        Mockito.doThrow(NoFoundObjectException.class)
                .when(userService)
                .getUserById(anyLong());

        mvc.perform(MockMvcRequestBuilders.get("/users/1"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").doesNotExist());
    }

    @Test
    void getUser_statusOk_userExist() throws Exception {
        User user = User.builder()
                .id(1L)
                .name("Mike")
                .email("mike@mail.ru")
                .build();

        when(userService.getUserById(anyLong()))
                .thenReturn(UserMapper.objectToDto(user));

        mvc.perform(MockMvcRequestBuilders.get("/users/1"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Mike"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").isString())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("mike@mail.ru"));
    }

    @Test
    void createUser_statusOk_userExist() throws Exception {
        UserRequestDto userRequest = UserRequestDto.builder()
                .name("Mike")
                .email("mike@mail.ru")
                .build();

        User user = User.builder()
                .id(1L)
                .name("Mike")
                .email("mike@mail.ru")
                .build();

        when(userService.createUser(any(UserRequestDto.class)))
                .thenReturn(UserMapper.objectToDto(user));

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Mike"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("mike@mail.ru"));
    }

    @Test
    void deleteUser_statusOk_userExist() throws Exception {
        doNothing()
                .when(userService)
                .deleteUserById(anyLong());

        mvc.perform(delete("/users/1"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void updateUser_statusNotFound_userIdNotFound() throws Exception {
        UserRequestDto userRequest = UserRequestDto.builder()
                .id(1L)
                .name("Nikita")
                .email("nikita@mail.ru")
                .build();


        Mockito.doThrow(NoFoundObjectException.class)
                .when(userService)
                .updateUserById(any(UserRequestDto.class), anyLong());

        mvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void updateUser_statusIsOk_userFoundAndUpdate() throws Exception {
        UserRequestDto userRequest = UserRequestDto.builder()
                .id(1L)
                .name("Nikita")
                .email("nikita@mail.ru")
                .build();

        User user = User.builder()
                .id(1L)
                .name("Nikita")
                .email("nikita@mail.ru")
                .build();

        when(userService.updateUserById(any(UserRequestDto.class), anyLong()))
                .thenReturn(UserMapper.objectToDto(user));

        mvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Nikita"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").isString())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("nikita@mail.ru"));
    }
}
package ru.practicum.shareit.request;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.NoFoundObjectException;
import ru.practicum.shareit.exception.NoValidArgumentException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.request.RequestServiceImpl;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
class RequestServiceImplTest {
    @Autowired
    RequestServiceImpl underTest;

    @MockBean
    RequestRepository requestRepository;

    @MockBean
    ItemService itemService;

    @MockBean
    UserService userService;

    User user;
    User user2;
    Request request;
    Item item;

    @BeforeEach
    void prepare() {
        user = User.builder().id(1L).name("Nikita").email("nikita@mail.ru").build();
        user2 = User.builder().id(2L).name("Mike").email("mike@mail.ru").build();
        request = Request.builder().id(10L)
                .created(LocalDateTime.of(2023, 2, 10, 17, 10, 5))
                .description("I need interesting book")
                .requestor(user2)
                .build();

        item = Item.builder().id(1L)
                .name("Book")
                .description("Good old book")
                .owner(user)
                .available(true)
                .request(request)
                .build();
    }

    @Test
    void getRequestById_correctRequest_requestIsExist() {
        doNothing()
                .when(userService)
                .checkExistUserById(anyLong());

        when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(request));

        when(itemService.getItemByRequestId(anyLong()))
                .thenReturn(item);

        RequestDto result = underTest.getRequestById(1L, 1L);

        verify(requestRepository, times(1)).findById(anyLong());

        assertNotNull(result);
        Assertions.assertThat(result.getDescription()).isEqualTo("I need interesting book");
    }

    @Test
    void getRequestById_notFoundObjectException_requestIdIsIncorrect() {
        doNothing()
                .when(userService)
                .checkExistUserById(anyLong());

        Mockito.doThrow(NoFoundObjectException.class)
                .when(requestRepository).findById(anyLong());

        assertThrows(NoFoundObjectException.class, () -> underTest.getRequestById(100L, 1L));
    }

    @Test
    void createRequest_successfulCreated_userIdExistAndRequestIsCorrect() {
        RequestDto request = RequestDto.builder()
                .description("I need interesting book")
                .build();

        when(userService.findUserById(anyLong())).thenReturn(user2);
        when(requestRepository.save(any(Request.class))).thenReturn(this.request);

        underTest.createRequest(request, 1L);

        verify(requestRepository, times(1)).save(any(Request.class));
    }

    @Test
    void createRequest_notFoundObjectException_userIdIsIncorrect() {
        Mockito.doThrow(NoFoundObjectException.class)
                .when(userService).findUserById(anyLong());

        assertThrows(NoFoundObjectException.class, () -> underTest.createRequest(any(RequestDto.class), 100L));
    }

    @Test
    void getOwnerRequestByUserId_notEmptyList_userIdIsCorrectAndRequestExist() {
        doNothing()
                .when(userService)
                .checkExistUserById(anyLong());

        when(itemService.getAllByRequestIds(any()))
                .thenReturn(anyList());

        underTest.getOwnerRequestByUserId(1L);

        verify(requestRepository, times(1)).findAllByRequestorId(anyLong());
    }

    @Test
    void getOwnerRequestByUserId_notFoundObjectException_userIdIsIncorrect() {
        Mockito.doThrow(NoFoundObjectException.class)
                .when(userService).checkExistUserById(anyLong());

        assertThrows(NoFoundObjectException.class, () -> underTest.getOwnerRequestByUserId(anyLong()));
    }

    @Test
    void getRequestByUserId_notEmptyList_userIdIsCorrectAndRequestIdIsCorrect() {
        doNothing()
                .when(userService)
                .checkExistUserById(anyLong());

        when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.of(request));

        when(itemService.getItemByRequestId(anyLong()))
                .thenReturn(item);

        underTest.getRequestById(2L, 10L);

        verify(requestRepository, times(1)).findById(anyLong());
    }

    @Test
    void getRequestByUserId_notFoundObjectException_userIdIsIncorrect() {
        Mockito.doThrow(NoFoundObjectException.class)
                .when(userService).checkExistUserById(anyLong());

        assertThrows(NoFoundObjectException.class, () -> underTest.getRequestById(100L, 1L));
    }

    @Test
    void getRequestsOtherUsers_notFoundObjectException_userIdIsIncorrect() {
        Mockito.doThrow(NoFoundObjectException.class)
                .when(userService).checkExistUserById(anyLong());

        assertThrows(NoFoundObjectException.class, () -> underTest.getRequestsOtherUsers(anyLong(), 0, 10));
    }

    @Test
    void getRequestsOtherUsers_notEmptyList_userIdIsCorrect() {
        doNothing()
                .when(userService)
                .checkExistUserById(anyLong());

        when(requestRepository.findAllByOwnerId(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(request));

        when(itemService.getItemByRequestId(anyLong()))
                .thenReturn(item);

        underTest.getRequestsOtherUsers(2L, 0, 10);

        verify(requestRepository, times(1)).findAllByOwnerId(anyLong(), any(Pageable.class));
    }

    @Test
    void getRequestsOtherUsers_noValidArgumentException_requestParamIsIncorrect() {
        int from = -1;
        int size = -10;

        doNothing()
                .when(userService)
                .checkExistUserById(anyLong());

        assertThrows(NoValidArgumentException.class, () -> underTest.getRequestsOtherUsers(1L, from, size));
    }
}
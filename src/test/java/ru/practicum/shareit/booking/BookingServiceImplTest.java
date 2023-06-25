package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.exception.NoCorrectRequestException;
import ru.practicum.shareit.exception.NoFoundObjectException;
import ru.practicum.shareit.exception.NoValidArgumentException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class BookingServiceImplTest {

    @Autowired
    BookingServiceImpl underTest;

    @MockBean
    BookingRepository bookingRepository;

    @MockBean
    UserService userService;

    @MockBean
    ItemRepository itemRepository;

    User user;
    User user2;

    Item item;

    Booking booking;

    @BeforeEach
    void prepare() {
        user = User.builder().id(1L).name("Nikita").email("nikita@mail.ru").build();
        user2 = User.builder().id(2L).name("Mike").email("mike@mail.ru").build();

        item = Item.builder().id(1L)
                .name("Book")
                .description("Good old book")
                .owner(user)
                .available(true)
                .build();

        booking = Booking.builder()
                .id(1L)
                .item(item)
                .start(LocalDateTime.of(2023, 2, 10, 17, 10, 5))
                .end(LocalDateTime.of(2023, 2, 10, 17, 10, 5).plusDays(15))
                .booker(user2)
                .build();
    }


    @Test
    void createBooking_noFoundObjectException_userIdDoesNotExist() {
        BookingRequestDto request = BookingRequestDto.builder()
                .itemId(1L)
                .start(LocalDateTime.of(2023, 2, 10, 17, 10, 5))
                .end(LocalDateTime.of(2023, 2, 10, 17, 10, 5).plusDays(15))
                .build();

        doThrow(NoFoundObjectException.class)
                .when(userService).findUserById(anyLong());

        assertThrows(NoFoundObjectException.class, () -> underTest.createBooking(1L, request));
    }

    @Test
    void createBooking_noFoundObjectException_itemDoesNotExist() {
        BookingRequestDto request = BookingRequestDto.builder()
                .itemId(10L)
                .start(LocalDateTime.of(2023, 2, 10, 17, 10, 5))
                .end(LocalDateTime.of(2023, 2, 10, 17, 10, 5).plusDays(15))
                .build();

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NoFoundObjectException.class, () -> underTest.createBooking(1L, request));
    }

    @Test
    void createBooking_noCorrectRequestException_availableIsFalse() {
        item.setAvailable(false);

        BookingRequestDto request = BookingRequestDto.builder()
                .itemId(1L)
                .start(LocalDateTime.of(2023, 2, 10, 17, 10, 5))
                .end(LocalDateTime.of(2023, 2, 10, 17, 10, 5).plusDays(15))
                .build();

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThrows(NoCorrectRequestException.class, () -> underTest.createBooking(1L, request));
    }

    @Test
    void createBooking_noFoundObjectException_ownerAndRequestorIsSame() {
        BookingRequestDto request = BookingRequestDto.builder()
                .itemId(1L)
                .start(LocalDateTime.of(2023, 2, 10, 17, 10, 5))
                .end(LocalDateTime.of(2023, 2, 10, 17, 10, 5).plusDays(15))
                .build();

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThrows(NoFoundObjectException.class, () -> underTest.createBooking(1L, request));
    }

    @Test
    void createBooking_successfulCreate_requestIsCorrect() {
        BookingRequestDto request = BookingRequestDto.builder()
                .itemId(1L)
                .start(LocalDateTime.of(2023, 2, 10, 17, 10, 5))
                .end(LocalDateTime.of(2023, 2, 10, 17, 10, 5).plusDays(15))
                .build();

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        underTest.createBooking(2L, request);

        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void updateStatusById_noFoundObjectException_bookingDoNotExist() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NoFoundObjectException.class, () -> underTest.updateStatusById(1L, true, 1L));
    }

    @Test
    void updateStatusById_noFoundObjectException_userIsNotOwnerBooking() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        assertThrows(NoFoundObjectException.class, () -> underTest.updateStatusById(1L, true, 2L));
    }

    @Test
    void updateStatusById_noCorrectRequestException_statusIsNotWaiting() {
        booking.setStatus(Status.REJECTED);

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        assertThrows(NoCorrectRequestException.class, () -> underTest.updateStatusById(1L, true, 1L));
    }

    @Test
    void updateStatusById_successfullyUpdatedWithStatusApproved_correctRequest() {
        boolean approved = true;
        booking.setStatus(Status.WAITING);

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        BookingResponseDto bookingResponse = underTest.updateStatusById(1L, approved, 1L);

        verify(bookingRepository, times(1)).save(any(Booking.class));
        assertEquals(Status.APPROVED, bookingResponse.getStatus());
    }

    @Test
    void updateStatusById_successfullyUpdatedWithStatusRejected_correctRequest() {
        boolean approved = false;
        booking.setStatus(Status.WAITING);

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        BookingResponseDto bookingResponse = underTest.updateStatusById(1L, approved, 1L);

        verify(bookingRepository, times(1)).save(any(Booking.class));
        assertEquals(Status.REJECTED, bookingResponse.getStatus());
    }

    @Test
    void getBookingById_noFoundObjectException_bookingDoNotExist() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NoFoundObjectException.class, () -> underTest.getBookingById(1L, 1L));
    }

    @Test
    void getBookingById_noFoundObjectException_userIsNotItemOwnerOrBooker() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        assertThrows(NoFoundObjectException.class, () -> underTest.getBookingById(1L, 33L));
    }

    @Test
    void getBookingById_correctResult_requestIsCorrect() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        underTest.getBookingById(1L, 1L);

        verify(bookingRepository, times(1)).findById(anyLong());
    }

    @Test
    void getAllByBookerId_noFoundObjectException_userDoNotExist() {
        doThrow(NoFoundObjectException.class)
                .when(userService).checkExistUserById(anyLong());

        assertThrows(NoFoundObjectException.class, () -> underTest.getAllByBookerId(1L, "ALL", 0, 10));
    }

    @Test
    void getAllByBookerId_noValidArgumentException_sizeAndFromAreNotCorrect() {
        int from = -1;
        int size = -1;
        doNothing()
                .when(userService).checkExistUserById(anyLong());

        assertThrows(NoValidArgumentException.class, () -> underTest.getAllByBookerId(1L, "ALL", from, size));
    }

    @Test
    void getAllByBookerId_noCorrectRequestException_stateIsIncorrect() {
        String state = "NONE";
        doNothing()
                .when(userService).checkExistUserById(anyLong());

        assertThrows(NoCorrectRequestException.class, () -> underTest.getAllByBookerId(1L, state, 0, 10));
    }

    @Test
    void getAllByBookerId_correctResultAndEmptyList_requestIsCorrectWithStateAll() {
        String state = "ALL";
        int from = 0;
        int size = 10;
        int page = 0;

        doNothing()
                .when(userService).checkExistUserById(anyLong());

        Pageable pageable = PageRequest.of(page, size, Sort.by("start").descending());

        underTest.getAllByBookerId(1L, state, from, size);

        verify(bookingRepository, times(1))
                .findAllByBookerId(1L, pageable);
    }

    @Test
    void getAllByBookerId_correctResultAndEmptyList_requestIsCorrectWithStateCurrent() {
        String state = "CURRENT";
        int from = 0;
        int size = 10;

        doNothing()
                .when(userService).checkExistUserById(anyLong());

        underTest.getAllByBookerId(1L, state, from, size);

        verify(bookingRepository, times(1))
                .findByBookerIdAndStartIsBeforeAndEndIsAfter(anyLong(), any(LocalDateTime.class),
                        any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void getAllByBookerId_correctResultAndEmptyList_requestIsCorrectWithStatePast() {
        String state = "PAST";
        int from = 0;
        int size = 10;

        doNothing()
                .when(userService).checkExistUserById(anyLong());

        underTest.getAllByBookerId(1L, state, from, size);

        verify(bookingRepository, atLeast(1))
                .findByBookerIdAndEndIsBefore(anyLong(), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void getAllByBookerId_correctResultAndEmptyList_requestIsCorrectWithStateFuture() {
        String state = "FUTURE";
        int from = 0;
        int size = 10;

        doNothing()
                .when(userService).checkExistUserById(anyLong());

        underTest.getAllByBookerId(1L, state, from, size);

        verify(bookingRepository, times(1))
                .findByBookerIdAndStartIsAfter(anyLong(), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void getAllByBookerId_correctResultAndEmptyList_requestIsCorrectWithStateWaiting() {
        String state = "WAITING";
        int from = 0;
        int size = 10;

        doNothing()
                .when(userService).checkExistUserById(anyLong());

        underTest.getAllByBookerId(1L, state, from, size);

        verify(bookingRepository, times(1))
                .findByBookerIdAndStartIsAfterAndStatusIs(anyLong(), any(LocalDateTime.class),
                        any(Status.class), any(Pageable.class));
    }

    @Test
    void getAllByBookerId_correctResultAndEmptyList_requestIsCorrectWithStateRejected() {
        String state = "REJECTED";
        int from = 0;
        int size = 10;

        doNothing()
                .when(userService).checkExistUserById(anyLong());

        underTest.getAllByBookerId(1L, state, from, size);

        verify(bookingRepository, times(1))
                .findByBookerIdAndStartIsAfterAndStatusIs(anyLong(), any(LocalDateTime.class),
                        any(Status.class), any(Pageable.class));
    }

    @Test
    void getAllByOwnerId_noFoundObjectException_userDoNotExist() {
        doThrow(NoFoundObjectException.class)
                .when(userService).findUserById(anyLong());

        assertThrows(NoFoundObjectException.class,
                () -> underTest.getAllByOwnerId(1L, "ALL", 0, 10));
    }

    @Test
    void getAllByOwnerId_noValidArgumentException_sizeAndFromNotCorrect() {
        int from = -1;
        int size = -1;

        when(userService.findUserById(anyLong())).thenReturn(user);

        assertThrows(NoValidArgumentException.class,
                () -> underTest.getAllByOwnerId(1L, "ALL", from, size));
    }

    @Test
    void getAllByOwnerId_correctResultAndEmptyList_requestIsCorrectAndStateAll() {
        String state = "ALL";
        int from = 0;
        int size = 10;
        int page = 0;

        when(userService.findUserById(anyLong()))
                .thenReturn(user);

        when(itemRepository.findAllByOwnerId(anyLong()))
                .thenReturn(List.of(item));

        Pageable pageable = PageRequest.of(page, size, Sort.by("start").descending());

        underTest.getAllByOwnerId(1L, state, from, size);

        verify(bookingRepository, times(1))
                .findAllByItemIdIn(List.of(item.getId()), pageable);
    }

    @Test
    void getAllByOwnerId_correctResultAndEmptyList_requestIsCorrectAndStateCurrent() {
        String state = "CURRENT";
        int from = 0;
        int size = 10;

        when(userService.findUserById(anyLong()))
                .thenReturn(user);

        when(itemRepository.findAllByOwnerId(anyLong()))
                .thenReturn(List.of(item));


        underTest.getAllByOwnerId(1L, state, from, size);

        verify(bookingRepository, times(1))
                .findByItemIdInAndStartIsBeforeAndEndIsAfter(anyList(), any(LocalDateTime.class),
                        any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void getAllByOwnerId_correctResultAndEmptyList_requestIsCorrectAndStatePast() {
        String state = "PAST";
        int from = 0;
        int size = 10;

        when(userService.findUserById(anyLong()))
                .thenReturn(user);

        when(itemRepository.findAllByOwnerId(anyLong()))
                .thenReturn(List.of(item));


        underTest.getAllByOwnerId(1L, state, from, size);

        verify(bookingRepository, times(1))
                .findByItemIdInAndEndIsBefore(anyList(), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void getAllByOwnerId_correctResultAndEmptyList_requestIsCorrectAndStateFuture() {
        String state = "FUTURE";
        int from = 0;
        int size = 10;

        when(userService.findUserById(anyLong()))
                .thenReturn(user);

        when(itemRepository.findAllByOwnerId(anyLong()))
                .thenReturn(List.of(item));


        underTest.getAllByOwnerId(1L, state, from, size);

        verify(bookingRepository, times(1))
                .findByItemIdInAndStartIsAfter(anyList(), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void getAllByOwnerId_correctResultAndEmptyList_requestIsCorrectAndStateWaiting() {
        String state = "WAITING";
        int from = 0;
        int size = 10;

        when(userService.findUserById(anyLong()))
                .thenReturn(user);

        when(itemRepository.findAllByOwnerId(anyLong()))
                .thenReturn(List.of(item));


        underTest.getAllByOwnerId(1L, state, from, size);

        verify(bookingRepository, times(1))
                .findByItemIdInAndStartIsAfterAndStatusIs(anyList(), any(LocalDateTime.class),
                        any(Status.class), any(Pageable.class));
    }

    @Test
    void getAllByOwnerId_correctResultAndEmptyList_requestIsCorrectAndStateRejected() {
        String state = "REJECTED";
        int from = 0;
        int size = 10;

        when(userService.findUserById(anyLong()))
                .thenReturn(user);

        when(itemRepository.findAllByOwnerId(anyLong()))
                .thenReturn(List.of(item));


        underTest.getAllByOwnerId(1L, state, from, size);

        verify(bookingRepository, times(1))
                .findByItemIdInAndStartIsAfterAndStatusIs(anyList(), any(LocalDateTime.class),
                        any(Status.class), any(Pageable.class));
    }
}
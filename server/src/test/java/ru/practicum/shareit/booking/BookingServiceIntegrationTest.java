package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.exception.NoCorrectRequestException;
import ru.practicum.shareit.exception.NoFoundObjectException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceIntegrationTest {
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;
    private final EntityManager entityManager;

    @BeforeEach
    void prepare() {
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY = 0; " +
                "TRUNCATE TABLE bookings, items, users RESTART IDENTITY;" +
                "SET REFERENTIAL_INTEGRITY = 1;");
    }

    @Test
    void createBooking_noFoundObjectException_correctRequest() {
        UserRequestDto userRequest1 = UserRequestDto.builder().name("Nikita").email("nikita@mail.ru").build();
        UserRequestDto userRequest2 = UserRequestDto.builder().name("Tom").email("tom@mail.ru").build();

        UserResponseDto savedUser1 = userService.createUser(userRequest1);
        UserResponseDto savedUser2 = userService.createUser(userRequest2);

        ItemRequestDto itemRequest = ItemRequestDto.builder()
                .name("Book")
                .description("Good old book")
                .available(true)
                .build();

        ItemResponseDto savedItem = itemService.createItem(itemRequest, savedUser1.getId());

        BookingRequestDto request = BookingRequestDto.builder()
                .itemId(savedItem.getId())
                .start(LocalDateTime.of(2023, 2, 10, 17, 10, 5))
                .end(LocalDateTime.of(2023, 2, 10, 17, 10, 5).plusDays(15))
                .build();

        BookingResponseDto result = bookingService.createBooking(savedUser2.getId(), request);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getItem().getName()).isEqualTo(savedItem.getName());
    }

    @Test
    void createBooking_noFoundObjectException_itemNotFound() {
        BookingRequestDto request = BookingRequestDto.builder()
                .itemId(1L)
                .start(LocalDateTime.of(2023, 2, 10, 17, 10, 5))
                .end(LocalDateTime.of(2023, 2, 10, 17, 10, 5).plusDays(15))
                .build();

        assertThrows(NoFoundObjectException.class, () -> bookingService.createBooking(1L, request));
    }

    @Test
    void createBooking_noCorrectRequestException_itemIsNotAvailable() {
        UserRequestDto userRequest1 = UserRequestDto.builder().name("Nikita").email("nikita@mail.ru").build();
        UserRequestDto userRequest2 = UserRequestDto.builder().name("Tom").email("tom@mail.ru").build();

        UserResponseDto savedUser1 = userService.createUser(userRequest1);
        UserResponseDto savedUser2 = userService.createUser(userRequest2);

        ItemRequestDto itemRequest = ItemRequestDto.builder()
                .name("Book")
                .description("Good old book")
                .available(false)
                .build();

        ItemResponseDto savedItem = itemService.createItem(itemRequest, savedUser1.getId());

        BookingRequestDto request = BookingRequestDto.builder()
                .itemId(savedItem.getId())
                .start(LocalDateTime.of(2023, 2, 10, 17, 10, 5))
                .end(LocalDateTime.of(2023, 2, 10, 17, 10, 5).plusDays(15))
                .build();

        assertThrows(NoCorrectRequestException.class, () -> bookingService.createBooking(savedUser2.getId(), request));
    }

    @Test
    void createBooking_noFoundObjectException_ownerBookingItem() {
        UserRequestDto userRequest1 = UserRequestDto.builder().name("Nikita").email("nikita@mail.ru").build();

        UserResponseDto savedUser1 = userService.createUser(userRequest1);

        ItemRequestDto itemRequest = ItemRequestDto.builder()
                .name("Book")
                .description("Good old book")
                .available(false)
                .build();

        ItemResponseDto savedItem = itemService.createItem(itemRequest, savedUser1.getId());

        BookingRequestDto request = BookingRequestDto.builder()
                .itemId(savedItem.getId())
                .start(LocalDateTime.of(2023, 2, 10, 17, 10, 5))
                .end(LocalDateTime.of(2023, 2, 10, 17, 10, 5).plusDays(15))
                .build();

        assertThrows(NoCorrectRequestException.class, () -> bookingService.createBooking(savedUser1.getId(), request));
    }

    @Test
    void updateStatusById_noFoundObjectException_bookingDoesNotExist() {
        UserRequestDto userRequest1 = UserRequestDto.builder().name("Nikita").email("nikita@mail.ru").build();
        UserRequestDto userRequest2 = UserRequestDto.builder().name("Tom").email("tom@mail.ru").build();

        UserResponseDto savedUser1 = userService.createUser(userRequest1);
        UserResponseDto savedUser2 = userService.createUser(userRequest2);

        ItemRequestDto itemRequest = ItemRequestDto.builder()
                .name("Book")
                .description("Good old book")
                .available(true)
                .build();

        ItemResponseDto savedItem = itemService.createItem(itemRequest, savedUser1.getId());

        BookingRequestDto request = BookingRequestDto.builder()
                .itemId(savedItem.getId())
                .start(LocalDateTime.of(2023, 2, 10, 17, 10, 5))
                .end(LocalDateTime.of(2023, 2, 10, 17, 10, 5).plusDays(15))
                .build();

        BookingResponseDto booking = bookingService.createBooking(savedUser2.getId(), request);

        assertThrows(NoFoundObjectException.class, () -> bookingService.updateStatusById(10L, false, savedUser1.getId()));
    }

    @Test
    void updateStatusById_noFoundObjectException_userCanNotChangeStatus() {
        UserRequestDto userRequest1 = UserRequestDto.builder().name("Nikita").email("nikita@mail.ru").build();
        UserRequestDto userRequest2 = UserRequestDto.builder().name("Tom").email("tom@mail.ru").build();

        UserResponseDto savedUser1 = userService.createUser(userRequest1);
        UserResponseDto savedUser2 = userService.createUser(userRequest2);

        ItemRequestDto itemRequest = ItemRequestDto.builder()
                .name("Book")
                .description("Good old book")
                .available(true)
                .build();

        ItemResponseDto savedItem = itemService.createItem(itemRequest, savedUser1.getId());

        BookingRequestDto request = BookingRequestDto.builder()
                .itemId(savedItem.getId())
                .start(LocalDateTime.of(2023, 2, 10, 17, 10, 5))
                .end(LocalDateTime.of(2023, 2, 10, 17, 10, 5).plusDays(15))
                .build();

        BookingResponseDto savedBooking = bookingService.createBooking(savedUser2.getId(), request);

        assertThrows(NoFoundObjectException.class, () -> bookingService.updateStatusById(savedBooking.getId(), false, savedUser2.getId()));
    }

    @Test
    void updateStatusById_npExceptions_correctRequest() {
        UserRequestDto userRequest1 = UserRequestDto.builder().name("Nikita").email("nikita@mail.ru").build();
        UserRequestDto userRequest2 = UserRequestDto.builder().name("Tom").email("tom@mail.ru").build();

        UserResponseDto savedUser1 = userService.createUser(userRequest1);
        UserResponseDto savedUser2 = userService.createUser(userRequest2);

        ItemRequestDto itemRequest = ItemRequestDto.builder()
                .name("Book")
                .description("Good old book")
                .available(true)
                .build();

        ItemResponseDto savedItem = itemService.createItem(itemRequest, savedUser1.getId());

        BookingRequestDto request = BookingRequestDto.builder()
                .itemId(savedItem.getId())
                .start(LocalDateTime.of(2023, 2, 10, 17, 10, 5))
                .end(LocalDateTime.of(2023, 2, 10, 17, 10, 5).plusDays(15))
                .build();

        BookingResponseDto savedBooking = bookingService.createBooking(savedUser2.getId(), request);

        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> bookingService.updateStatusById(savedBooking.getId(), false,
                savedUser1.getId()));

    }

    @Test
    void updateStatusById_noFoundObjectException_statusIsNotWaiting() {
        UserRequestDto userRequest1 = UserRequestDto.builder().name("Nikita").email("nikita@mail.ru").build();
        UserRequestDto userRequest2 = UserRequestDto.builder().name("Tom").email("tom@mail.ru").build();

        UserResponseDto savedUser1 = userService.createUser(userRequest1);
        UserResponseDto savedUser2 = userService.createUser(userRequest2);

        ItemRequestDto itemRequest = ItemRequestDto.builder()
                .name("Book")
                .description("Good old book")
                .available(true)
                .build();

        ItemResponseDto savedItem = itemService.createItem(itemRequest, savedUser1.getId());

        BookingRequestDto request = BookingRequestDto.builder()
                .itemId(savedItem.getId())
                .start(LocalDateTime.of(2023, 2, 10, 17, 10, 5))
                .end(LocalDateTime.of(2023, 2, 10, 17, 10, 5).plusDays(15))
                .build();

        BookingResponseDto savedBooking = bookingService.createBooking(savedUser2.getId(), request);

        bookingService.updateStatusById(savedBooking.getId(), false, savedUser1.getId());

        assertThrows(NoCorrectRequestException.class,
                () -> bookingService.updateStatusById(savedBooking.getId(), false, savedUser1.getId()));
    }

    @Test
    void getBookingById_noFoundObjectException_bookingDoesNotExist() {
        assertThrows(NoFoundObjectException.class,
                () -> bookingService.getBookingById(1L, 1L));
    }

    @Test
    void getBookingById_noFoundObjectException_userIsNotOwnerBooking() {
        UserRequestDto userRequest1 = UserRequestDto.builder().name("Nikita").email("nikita@mail.ru").build();
        UserRequestDto userRequest2 = UserRequestDto.builder().name("Tom").email("tom@mail.ru").build();
        UserRequestDto userRequest3 = UserRequestDto.builder().name("Sam").email("sam@mail.ru").build();

        UserResponseDto savedUser1 = userService.createUser(userRequest1);
        UserResponseDto savedUser2 = userService.createUser(userRequest2);
        UserResponseDto savedUser3 = userService.createUser(userRequest3);

        ItemRequestDto itemRequest = ItemRequestDto.builder()
                .name("Book")
                .description("Good old book")
                .available(true)
                .build();

        ItemResponseDto savedItem = itemService.createItem(itemRequest, savedUser1.getId());

        BookingRequestDto request = BookingRequestDto.builder()
                .itemId(savedItem.getId())
                .start(LocalDateTime.of(2023, 2, 10, 17, 10, 5))
                .end(LocalDateTime.of(2023, 2, 10, 17, 10, 5).plusDays(15))
                .build();

        BookingResponseDto savedBooking = bookingService.createBooking(savedUser2.getId(), request);

        assertThrows(NoFoundObjectException.class,
                () -> bookingService.getBookingById(savedBooking.getId(), savedUser3.getId()));
    }

    @Test
    void getBookingById_correctBooking_requestIsCorrect() {
        UserRequestDto userRequest1 = UserRequestDto.builder().name("Nikita").email("nikita@mail.ru").build();
        UserRequestDto userRequest2 = UserRequestDto.builder().name("Tom").email("tom@mail.ru").build();

        UserResponseDto savedUser1 = userService.createUser(userRequest1);
        UserResponseDto savedUser2 = userService.createUser(userRequest2);

        ItemRequestDto itemRequest = ItemRequestDto.builder()
                .name("Book")
                .description("Good old book")
                .available(true)
                .build();

        ItemResponseDto savedItem = itemService.createItem(itemRequest, savedUser1.getId());

        BookingRequestDto request = BookingRequestDto.builder()
                .itemId(savedItem.getId())
                .start(LocalDateTime.of(2023, 2, 10, 17, 10, 5))
                .end(LocalDateTime.of(2023, 2, 10, 17, 10, 5).plusDays(15))
                .build();

        BookingResponseDto savedBooking = bookingService.createBooking(savedUser2.getId(), request);

        BookingResponseDto result = bookingService.getBookingById(savedBooking.getId(), savedUser2.getId());

        Assertions.assertThat(result.getItem().getName()).isEqualTo(itemRequest.getName());
        Assertions.assertThat(result.getBooker().getName()).isEqualTo(savedUser2.getName());
    }

    @Test
    void getAllByBookerId_emptyResultList_requestIsCorrect() {
        UserRequestDto userRequest1 = UserRequestDto.builder().name("Nikita").email("nikita@mail.ru").build();
        UserRequestDto userRequest2 = UserRequestDto.builder().name("Tom").email("tom@mail.ru").build();

        UserResponseDto savedUser1 = userService.createUser(userRequest1);
        UserResponseDto savedUser2 = userService.createUser(userRequest2);

        ItemRequestDto itemRequest = ItemRequestDto.builder()
                .name("Book")
                .description("Good old book")
                .available(true)
                .build();

        ItemResponseDto savedItem = itemService.createItem(itemRequest, savedUser1.getId());

        BookingRequestDto request = BookingRequestDto.builder()
                .itemId(savedItem.getId())
                .start(LocalDateTime.of(2023, 2, 10, 17, 10, 5))
                .end(LocalDateTime.of(2023, 2, 10, 17, 10, 5).plusDays(15))
                .build();

        BookingResponseDto savedBooking = bookingService.createBooking(savedUser2.getId(), request);

        List<BookingResponseDto> result = bookingService.getAllByBookerId(savedUser2.getId(),
                BookingState.WAITING.toString(), 0, 10);

        Assertions.assertThat(result).isEmpty();
    }

    @Test
    void getAllByOwnerId_emptyResultList_requestIsCorrect() {
        UserRequestDto userRequest1 = UserRequestDto.builder().name("Nikita").email("nikita@mail.ru").build();
        UserRequestDto userRequest2 = UserRequestDto.builder().name("Tom").email("tom@mail.ru").build();

        UserResponseDto savedUser1 = userService.createUser(userRequest1);
        UserResponseDto savedUser2 = userService.createUser(userRequest2);

        ItemRequestDto itemRequest = ItemRequestDto.builder()
                .name("Book")
                .description("Good old book")
                .available(true)
                .build();

        ItemResponseDto savedItem = itemService.createItem(itemRequest, savedUser1.getId());

        BookingRequestDto request = BookingRequestDto.builder()
                .itemId(savedItem.getId())
                .start(LocalDateTime.of(2023, 2, 10, 17, 10, 5))
                .end(LocalDateTime.of(2023, 2, 10, 17, 10, 5).plusDays(15))
                .build();

        BookingResponseDto savedBooking = bookingService.createBooking(savedUser2.getId(), request);

        List<BookingResponseDto> result = bookingService.getAllByOwnerId(savedUser2.getId(),
                BookingState.WAITING.toString(), 0, 10);

        Assertions.assertThat(result).isEmpty();
    }
}
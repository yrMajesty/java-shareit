package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.exception.NoCorrectRequestException;
import ru.practicum.shareit.exception.NoFoundObjectException;
import ru.practicum.shareit.exception.NoValidArgumentException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(BookingController.class)
class BookingControllerTest {
    @Autowired
    MockMvc mvc;

    @MockBean
    BookingService bookingService;

    @Autowired
    ObjectMapper objectMapper;

    String userIdHeader = "X-Sharer-User-Id";

    @Nested
    class ValidationBookingTest {
        Validator validator;

        @BeforeEach
        void prepare() {
            try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
                validator = validatorFactory.getValidator();
            }
        }

        @Test
        void validationBooking_correctSizeValidationList_completelyIncorrectItem() {
            BookingRequestDto test = new BookingRequestDto();

            assertEquals(3, validator.validate(test).size());
        }

        @Test
        void validationBooking_correctSizeValidationList_itemIdIsNull() {
            BookingRequestDto test = BookingRequestDto.builder()
                    .itemId(null)
                    .start(LocalDateTime.now().plusDays(1))
                    .end(LocalDateTime.now().plusDays(2))
                    .build();

            List<ConstraintViolation<BookingRequestDto>> validationSet = new ArrayList<>(validator.validate(test));
            assertAll(
                    () -> assertEquals(1, validationSet.size()),
                    () -> assertEquals("ItemId cannot be empty or null", validationSet.get(0).getMessage())
            );
        }

        @Test
        void validationBooking_correctSizeValidationList_startTimeInThePast() {
            BookingRequestDto test = BookingRequestDto.builder()
                    .itemId(1L)
                    .start(LocalDateTime.now().minusDays(1))
                    .end(LocalDateTime.now().plusDays(1))
                    .build();

            List<ConstraintViolation<BookingRequestDto>> validationSet = new ArrayList<>(validator.validate(test));
            assertAll(
                    () -> assertEquals(1, validationSet.size()),
                    () -> assertEquals("Start time can be only in present or future", validationSet.get(0).getMessage())
            );
        }

        @Test
        void validationBooking_correctSizeValidationList_startTimeIsNull() {
            BookingRequestDto test = BookingRequestDto.builder()
                    .itemId(1L)
                    .start(null)
                    .end(LocalDateTime.now().plusDays(1))
                    .build();

            List<ConstraintViolation<BookingRequestDto>> validationSet = new ArrayList<>(validator.validate(test));
            assertAll(
                    () -> assertEquals(1, validationSet.size()),
                    () -> assertEquals("Start date cannot be empty or null", validationSet.get(0).getMessage())
            );
        }

        @Test
        void validationBooking_correctSizeValidationList_endTimeIsNull() {
            BookingRequestDto test = BookingRequestDto.builder()
                    .itemId(1L)
                    .start(LocalDateTime.now().plusDays(1))
                    .end(null)
                    .build();

            List<ConstraintViolation<BookingRequestDto>> validationSet = new ArrayList<>(validator.validate(test));
            assertAll(
                    () -> assertEquals(1, validationSet.size()),
                    () -> assertEquals("End date cannot be empty or null", validationSet.get(0).getMessage())
            );
        }

        @Test
        void validationBooking_correctSizeValidationList_endEarlyStart() {
            BookingRequestDto test = BookingRequestDto.builder()
                    .itemId(1L)
                    .start(LocalDateTime.now().plusDays(2))
                    .end(LocalDateTime.now().minusDays(3))
                    .build();

            List<ConstraintViolation<BookingRequestDto>> validationSet = new ArrayList<>(validator.validate(test));
            assertAll(
                    () -> assertEquals(2, validationSet.size()),
                    () -> assertTrue(validationSet.stream()
                            .map(ConstraintViolation::getMessage)
                            .collect(Collectors.toList())
                            .contains("End time can be only in future")),
                    () -> assertTrue(validationSet.stream()
                            .map(ConstraintViolation::getMessage)
                            .collect(Collectors.toList())
                            .contains("End date should be after start date"))
            );
        }
    }

    @Test
    void createBooking_statusNotFound_itemDontExist() throws Exception {
        BookingRequestDto bookingRequest = BookingRequestDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(10))
                .end(LocalDateTime.now().plusDays(30))
                .build();

        doThrow(NoFoundObjectException.class)
                .when(bookingService).createBooking(anyLong(), any(BookingRequestDto.class));

        mvc.perform(post("/bookings")
                        .header(userIdHeader, 1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(bookingRequest)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").doesNotExist());
    }

    @Test
    void createBooking_statusBadRequest_itemAvailableIsFalse() throws Exception {
        BookingRequestDto bookingRequest = BookingRequestDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(10))
                .end(LocalDateTime.now().plusDays(30))
                .build();

        doThrow(NoCorrectRequestException.class)
                .when(bookingService).createBooking(anyLong(), any(BookingRequestDto.class));

        mvc.perform(post("/bookings")
                        .header(userIdHeader, 1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(bookingRequest)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").doesNotExist());
    }

    @Test
    void createBooking_statusBadRequest_timeIncorrect() throws Exception {
        BookingRequestDto bookingRequest = BookingRequestDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(10))
                .end(LocalDateTime.now().minusDays(5))
                .build();

        doThrow(NoCorrectRequestException.class)
                .when(bookingService).createBooking(anyLong(), any(BookingRequestDto.class));

        mvc.perform(post("/bookings")
                        .header(userIdHeader, 1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(bookingRequest)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").doesNotExist());
    }

    @Test
    void createBooking_statusOk_itemExist() throws Exception {
        BookingRequestDto bookingRequest = BookingRequestDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(10))
                .end(LocalDateTime.now().plusDays(30))
                .build();

        BookingResponseDto bookingResponse = BookingResponseDto.builder()
                .id(10L)
                .start(LocalDateTime.now().plusDays(10))
                .end(LocalDateTime.now().plusDays(30))
                .booker(new UserDto(1L, "Mike"))
                .item(ItemDto.builder().id(1L).name("Book").build())
                .build();

        when(bookingService.createBooking(anyLong(), any(BookingRequestDto.class)))
                .thenReturn(bookingResponse);

        mvc.perform(post("/bookings")
                        .header(userIdHeader, 1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(bookingRequest)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.booker.name").value("Mike"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.item.name").value("Book"));
    }

    @Test
    void changeBookingStatus_statusNotFound_itemDoesNotExist() throws Exception {

        doThrow(NoFoundObjectException.class)
                .when(bookingService).updateStatusById(anyLong(), anyBoolean(), anyLong());

        mvc.perform(patch("/bookings/1")
                        .header(userIdHeader, 1)
                        .param("approved", "true"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").doesNotExist());
    }

    @Test
    void changeBookingStatus_statusBadRequest_statusBookingIsNotWaiting() throws Exception {
        doThrow(NoCorrectRequestException.class)
                .when(bookingService).updateStatusById(anyLong(), anyBoolean(), anyLong());

        mvc.perform(patch("/bookings/1")
                        .header(userIdHeader, 1)
                        .param("approved", "true"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").doesNotExist());
    }

    @Test
    void changeBookingStatus_statusOk_statusBookingIsNotWaiting() throws Exception {
        BookingResponseDto bookingResponse = BookingResponseDto.builder()
                .id(10L)
                .start(LocalDateTime.now().plusDays(10))
                .end(LocalDateTime.now().plusDays(30))
                .status(Status.APPROVED)
                .booker(new UserDto(1L, "Mike"))
                .item(ItemDto.builder().id(1L).name("Book").build())
                .build();

        when(bookingService.updateStatusById(anyLong(), anyBoolean(), anyLong()))
                .thenReturn(bookingResponse);

        mvc.perform(patch("/bookings/1")
                        .header(userIdHeader, 1)
                        .param("approved", "true"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(Status.APPROVED.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.booker.name").value("Mike"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.item.name").value("Book"));
    }

    @Test
    void getBookingInfo_statusOk_bookingExist() throws Exception {
        BookingResponseDto bookingResponse = BookingResponseDto.builder()
                .id(10L)
                .start(LocalDateTime.now().plusDays(10))
                .end(LocalDateTime.now().plusDays(30))
                .status(Status.APPROVED)
                .booker(new UserDto(1L, "Mike"))
                .item(ItemDto.builder().id(1L).name("Book").build())
                .build();

        when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenReturn(bookingResponse);

        mvc.perform(MockMvcRequestBuilders.get("/bookings/1")
                        .header(userIdHeader, 1))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(Status.APPROVED.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.booker.name").value("Mike"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.item.name").value("Book"));
    }

    @Test
    void getBookingInfo_statusNotFound_bookingDoesNotExist() throws Exception {
        doThrow(NoFoundObjectException.class)
                .when(bookingService).getBookingById(anyLong(), anyLong());

        mvc.perform(MockMvcRequestBuilders.get("/bookings/1")
                        .header(userIdHeader, 1))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").doesNotExist());
    }

    @Test
    void getAllBookingsBooker_statusOkAndEmptyList_bookerExist() throws Exception {
        BookingResponseDto bookingResponse = BookingResponseDto.builder()
                .id(10L)
                .start(LocalDateTime.now().plusDays(10))
                .end(LocalDateTime.now().plusDays(30))
                .status(Status.APPROVED)
                .booker(new UserDto(1L, "Mike"))
                .item(ItemDto.builder().id(1L).name("Book").build())
                .build();

        when(bookingService.getAllByBookerId(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingResponse));

        mvc.perform(MockMvcRequestBuilders.get("/bookings")
                        .header(userIdHeader, 1)
                        .param("from", "1")
                        .param("size", "1")
                        .param("state", "ALL"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].status").value(Status.APPROVED.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].booker.name").value("Mike"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].item.name").value("Book"));
    }

    @Test
    void getAllBookingsBooker_statusOkAndEmptyList_requestParamIncorrect() throws Exception {
        doThrow(NoValidArgumentException.class)
                .when(bookingService)
                .getAllByBookerId(anyLong(), any(), anyInt(), anyInt());

        mvc.perform(MockMvcRequestBuilders.get("/bookings")
                        .header(userIdHeader, 1)
                        .param("from", "-1")
                        .param("size", "-1")
                        .param("state", "ALL"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0]").doesNotExist());
    }

    @Test
    void getAllBookingsOwner_statusOkAndEmptyList_bookerExist() throws Exception {
        BookingResponseDto bookingResponse = BookingResponseDto.builder()
                .id(10L)
                .start(LocalDateTime.now().plusDays(10))
                .end(LocalDateTime.now().plusDays(30))
                .status(Status.APPROVED)
                .booker(new UserDto(1L, "Mike"))
                .item(ItemDto.builder().id(1L).name("Book").build())
                .build();

        when(bookingService.getAllByOwnerId(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingResponse));

        mvc.perform(MockMvcRequestBuilders.get("/bookings/owner")
                        .header(userIdHeader, 1)
                        .param("from", "1")
                        .param("size", "1")
                        .param("state", "ALL"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].status").value(Status.APPROVED.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].booker.name").value("Mike"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].item.name").value("Book"));
    }

    @Test
    void getAllBookingsOwner_statusOkAndEmptyList_requestParamIncorrect() throws Exception {
        doThrow(NoValidArgumentException.class)
                .when(bookingService)
                .getAllByOwnerId(anyLong(), any(), anyInt(), anyInt());

        mvc.perform(MockMvcRequestBuilders.get("/bookings/owner")
                        .header(userIdHeader, 1)
                        .param("from", "-1")
                        .param("size", "-1")
                        .param("state", "ALL"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0]").doesNotExist());
    }
}
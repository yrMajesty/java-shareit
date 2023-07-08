package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.exception.NoCorrectRequestException;
import ru.practicum.shareit.exception.NoFoundObjectException;
import ru.practicum.shareit.exception.NoValidArgumentException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @Test
    void createBooking_statusNotFound_itemDontExist() throws Exception {
        BookingRequestDto bookingRequest = BookingRequestDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(10))
                .end(LocalDateTime.now().plusDays(30))
                .build();

        Mockito.doThrow(NoFoundObjectException.class)
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

        Mockito.doThrow(NoCorrectRequestException.class)
                .when(bookingService)
                .createBooking(anyLong(), any(BookingRequestDto.class));

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

        Mockito.doThrow(NoCorrectRequestException.class)
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
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.booker.name").value("Mike"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.item.name").value("Book"));
    }

    @Test
    void changeBookingStatus_statusNotFound_itemDoesNotExist() throws Exception {
        Mockito.doThrow(NoFoundObjectException.class)
                .when(bookingService).updateStatusById(anyLong(), anyBoolean(), anyLong());

        mvc.perform(patch("/bookings/1")
                        .header(userIdHeader, 1)
                        .param("approved", "true"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").doesNotExist());
    }

//    @Test
//    void changeBookingStatus_statusBadRequest_statusBookingIsNotWaiting() throws Exception {
//        Mockito.doThrow(NoCorrectRequestException.class)
//                .when(bookingService).updateStatusById(anyLong(), anyBoolean(), anyLong());
//
//        mvc.perform(patch("/bookings/1")
//                        .header(userIdHeader, 1)
//                        .param("approved", "true"))
//                .andDo(print())
//                .andExpect(MockMvcResultMatchers.status().isBadRequest())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.id").doesNotExist());
//    }

    @Test
    void changeBookingStatus_statusOk_requestIsCorrect() throws Exception {
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

        mvc.perform(get("/bookings/1")
                        .header(userIdHeader, 1))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(Status.APPROVED.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.booker.name").value("Mike"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.item.name").value("Book"));
    }

    @Test
    void getBookingInfo_statusNotFound_bookingDoesNotExist() throws Exception {
        Mockito.doThrow(NoFoundObjectException.class)
                .when(bookingService).getBookingById(anyLong(), anyLong());

        mvc.perform(get("/bookings/1")
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

        mvc.perform(get("/bookings")
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

//    @Test
//    void getAllBookingsBooker_statusOkAndEmptyList_requestParamIncorrect() throws Exception {
//        Mockito.doThrow(NoValidArgumentException.class)
//                .when(bookingService)
//                .getAllByBookerId(anyLong(), any(), anyInt(), anyInt());
//
//        mvc.perform(MockMvcRequestBuilders.get("/bookings")
//                        .header(userIdHeader, 1)
//                        .param("from", "-1")
//                        .param("size", "-1")
//                        .param("state", "ALL"))
//                .andDo(print())
//                .andExpect(MockMvcResultMatchers.status().isBadRequest())
//                .andExpect(MockMvcResultMatchers.jsonPath("$[0]").doesNotExist());
//    }

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

        mvc.perform(get("/bookings/owner")
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
        Mockito.doThrow(NoValidArgumentException.class)
                .when(bookingService)
                .getAllByOwnerId(anyLong(), any(), anyInt(), anyInt());

        mvc.perform(get("/bookings/owner")
                        .header(userIdHeader, 1)
                        .param("from", "-1")
                        .param("size", "-1")
                        .param("state", "ALL"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0]").doesNotExist());
    }
}
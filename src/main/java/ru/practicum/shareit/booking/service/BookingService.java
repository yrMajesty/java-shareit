package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingService {
    BookingResponse createBooking(Long userId, BookingRequest bookingRequest);

    BookingResponse updateStatusById(Long bookingId, Boolean approved, Long userId);

    BookingResponse getBookingById(Long bookingId, Long userId);

    List<BookingResponse> getAllByBookerId(Long userId, String state);

    List<BookingResponse> getAllByOwnerId(Long userId, String state);

    List<Booking> getAllByItemId(Long id);

    List<Booking> getAllByItemAndEndBeforeDate(Long itemId, LocalDateTime created);

    List<Booking> getAllByItemIdIn(List<Long> itemsId);
}


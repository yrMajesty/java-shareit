package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;

import java.util.List;
import java.util.stream.Collectors;

public class BookingMapper {

    public static BookingResponse objectToDto(Booking booking) {
        BookingResponse.Item itemDto = BookingResponse.Item.builder()
                .id(booking.getItem().getId())
                .name(booking.getItem().getName())
                .build();

        BookingResponse.Booker booker = BookingResponse.Booker.builder()
                .id(booking.getBooker().getId())
                .build();

        return BookingResponse.builder()
                .id(booking.getId())
                .booker(booker)
                .item(itemDto)
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .build();
    }

    public static Booking dtoToObject(BookingRequest bookingDto) {
        return Booking.builder()
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .build();
    }


    public static List<BookingResponse> objectToDto(List<Booking> bookings) {
        return bookings.stream()
                .map(BookingMapper::objectToDto)
                .collect(Collectors.toList());
    }
}
package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

public class BookingMapper {

    public static BookingResponseDto objectToDto(Booking booking) {
        ItemDto itemDto = ItemDto.builder()
                .id(booking.getItem().getId())
                .name(booking.getItem().getName())
                .build();

        UserDto booker = UserDto.builder()
                .id(booking.getBooker().getId())
                .name(booking.getBooker().getName())
                .build();

        return BookingResponseDto.builder()
                .id(booking.getId())
                .booker(booker)
                .item(itemDto)
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .build();
    }

    public static Booking dtoToObject(BookingRequestDto bookingDto) {
        return Booking.builder()
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .build();
    }


    public static List<BookingResponseDto> objectToDto(List<Booking> bookings) {
        return bookings.stream()
                .map(BookingMapper::objectToDto)
                .collect(Collectors.toList());
    }
}
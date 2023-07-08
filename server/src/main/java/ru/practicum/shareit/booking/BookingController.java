package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponseDto createBooking(@RequestHeader(name = USER_ID_HEADER) Long userId,
                                            @RequestBody BookingRequestDto bookingRequestDto) {
        return bookingService.createBooking(userId, bookingRequestDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto changeBookingStatus(@RequestHeader(name = USER_ID_HEADER) Long userId,
                                                  @PathVariable(name = "bookingId") Long bookingId,
                                                  @RequestParam(name = "approved") Boolean approved) {
        return bookingService.updateStatusById(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBookingInfo(@RequestHeader(name = USER_ID_HEADER) Long userId,
                                             @PathVariable(name = "bookingId") Long bookingId) {
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingResponseDto> getAllBookingsBooker(@RequestHeader(name = USER_ID_HEADER) Long userId,
                                                         @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                         @RequestParam(name = "size", defaultValue = "10") Integer size,
                                                         @RequestParam(name = "state", defaultValue = "ALL") String state) {
        return bookingService.getAllByBookerId(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getAllBookingsOwner(@RequestHeader(name = USER_ID_HEADER) Long userId,
                                                        @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                        @RequestParam(name = "size", defaultValue = "10") Integer size,
                                                        @RequestParam(name = "state", defaultValue = "ALL") String state) {
        return bookingService.getAllByOwnerId(userId, state, from, size);
    }
}

package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @PostMapping
    public BookingResponse addBooking(@RequestHeader(name = USER_ID_HEADER) Long userId,
                                      @RequestBody @Valid BookingRequest bookingRequest) {
        return bookingService.createBooking(userId, bookingRequest);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponse changeBookingStatus(@RequestHeader(name = USER_ID_HEADER) Long userId,
                                               @PathVariable Long bookingId,
                                               @RequestParam(name = "approved") Boolean approved) {
        return bookingService.updateStatusById(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingResponse getBookingInfo(@RequestHeader(name = USER_ID_HEADER) Long userId,
                                          @PathVariable Long bookingId) {
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingResponse> getAllBookingsBooker(@RequestHeader(name = USER_ID_HEADER) Long userId,
                                                      @RequestParam(name = "state", defaultValue = "ALL") String state) {
        return bookingService.getAllByBookerId(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingResponse> getAllBookingsOwner(@RequestHeader(name = USER_ID_HEADER) Long userId,
                                                     @RequestParam(name = "state", defaultValue = "ALL") String state) {
        return bookingService.getAllByOwnerId(userId, state);
    }

}

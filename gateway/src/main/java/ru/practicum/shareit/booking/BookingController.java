package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.exception.NoCorrectRequestException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader(name = USER_ID_HEADER) Long userId,
                                              @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingStatus state = BookingStatus.from(stateParam)
                .orElseThrow(() -> new NoCorrectRequestException("Unknown state: " + stateParam));

        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);

        return bookingClient.getBookingsByBookerId(userId, state, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createBooking(@RequestHeader(name = USER_ID_HEADER) Long userId,
                                                @RequestBody @Valid BookingRequestDto request) {
        log.info("Creating booking {}, userId={}", request, userId);

        if (request.getEnd().isBefore(request.getStart()) ||
                request.getEnd().equals(request.getStart())) {
            throw new NoCorrectRequestException("Exception of start date or end date");
        }

        return bookingClient.createBooking(userId, request);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(name = USER_ID_HEADER) Long userId,
                                             @PathVariable(name = "bookingId") Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);

        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findOwnerBookings(@RequestHeader(name = USER_ID_HEADER) Long userId,
                                                    @RequestParam(name = "state",
                                                            defaultValue = "all") String stateParam,
                                                    @PositiveOrZero @RequestParam(name = "from",
                                                            defaultValue = "0") Integer from,
                                                    @Positive @RequestParam(name = "size",
                                                            defaultValue = "10") Integer size) {
        BookingStatus state = BookingStatus.from(stateParam)
                .orElseThrow(() -> new NoCorrectRequestException("Unknown state: " + stateParam));

        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);

        return bookingClient.getBookingsByOwnerId(userId, state, from, size);
    }


    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateStatusBooking(@RequestHeader(name = USER_ID_HEADER) Long ownerId,
                                                      @PathVariable(name = "bookingId") Long bookingId,
                                                      @RequestParam(name = "approved") Boolean approved) {
        log.info("Update status booking {}, userId={}", bookingId, ownerId);

        return bookingClient.updateStatusBooking(ownerId, bookingId, approved);
    }
}

package ru.practicum.shareit.booking.dto;

import lombok.*;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {

    @NotNull(message = "ItemId cannot be empty or null")
    private Long itemId;

    @FutureOrPresent(message = "Start time can be only in present or future")
    @NotNull(message = "Start date cannot be empty or null")
    private LocalDateTime start;

    @Future(message = "End time can be only in future")
    @NotNull(message = "End date cannot be empty or null")
    private LocalDateTime end;
}

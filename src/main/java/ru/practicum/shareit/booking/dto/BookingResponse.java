package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.Status;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class BookingResponse {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Booker booker;
    private Item item;
    private Status status;

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    public static class Item {
        private Long id;
        private String name;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    public static class Booker {
        private Long id;
        private String name;
    }

}

package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.comment.CommentResponse;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ItemResponse {
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private BookingDto lastBooking;

    private BookingDto nextBooking;

    private List<CommentResponse> comments;

}

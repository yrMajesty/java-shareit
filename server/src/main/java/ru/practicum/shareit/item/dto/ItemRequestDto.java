package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.user.User;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ItemRequestDto {
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private User owner;

    private Long requestId;
}

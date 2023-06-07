package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ItemRequest {
    private Long id;

    @NotBlank(message = "Name cannot be empty or null")
    private String name;

    @NotBlank(message = "Description cannot be empty or null")
    private String description;

    @NotNull(message = "Available cannot be null")
    private Boolean available;

    private User owner;

    private Long request;
}

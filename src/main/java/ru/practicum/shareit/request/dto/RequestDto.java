package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestDto {
    private Long id;

    @NotBlank(message = "Description cannot be empty or null")
    private String description;

    private Long requestorId;

    private LocalDateTime created;

    private List<ItemDto> items;
}

package ru.practicum.shareit.user.dto;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class UserResponseDto {
    private Long id;

    private String name;

    private String email;
}

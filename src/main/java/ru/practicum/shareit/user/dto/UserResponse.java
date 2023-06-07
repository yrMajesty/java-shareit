package ru.practicum.shareit.user.dto;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class UserResponse {
    private Long id;

    private String name;

    private String email;
}

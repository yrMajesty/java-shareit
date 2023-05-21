package ru.practicum.shareit.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class UserDto {
    private Long id;

    @NotBlank(message = "Name cannot be empty or null")
    private String name;

    @NotBlank(message = "Email cannot empty or null")
    @Email(regexp = "[\\w._]{1,10}@[\\w]{2,}.[\\w]{2,}", message = "Email is not format as email (email@email.com)")
    private String email;
}

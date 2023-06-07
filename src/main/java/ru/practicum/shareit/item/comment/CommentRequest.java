package ru.practicum.shareit.item.comment;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CommentRequest {
    @NotBlank(message = "Text cannot be null")
    private String text;
}

package ru.practicum.shareit.item.comment;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@ToString
public class CommentRequestDto {
    @NotBlank(message = "Text cannot be null or empty")
    private String text;
}

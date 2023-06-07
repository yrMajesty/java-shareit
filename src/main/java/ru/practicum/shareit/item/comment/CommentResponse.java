package ru.practicum.shareit.item.comment;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CommentResponse {
    private Long id;
    private String text;
    private String authorName;
    private LocalDateTime created;
}


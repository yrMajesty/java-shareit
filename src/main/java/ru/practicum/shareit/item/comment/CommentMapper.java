package ru.practicum.shareit.item.comment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class CommentMapper {
    public static Comment dtoToObject(CommentRequestDto commentDto) {
        return Comment.builder()
                .text(commentDto.getText())
                .created(LocalDateTime.now())
                .build();
    }

    public static CommentResponseDto dtoToObject(Comment comment) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    public static List<CommentResponseDto> objectsToDto(List<Comment> comments) {
        return comments.stream()
                .map(CommentMapper::dtoToObject)
                .collect(Collectors.toList());
    }
}

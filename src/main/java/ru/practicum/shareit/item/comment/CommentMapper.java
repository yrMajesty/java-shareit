package ru.practicum.shareit.item.comment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class CommentMapper {
    public static Comment dtoToObject(CommentRequest commentDto) {
        return Comment.builder()
                .text(commentDto.getText())
                .created(LocalDateTime.now())
                .build();
    }

    public static CommentResponse dtoToObject(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    public static List<CommentResponse> objectsToDto(List<Comment> comments) {
        return comments.stream()
                .map(CommentMapper::dtoToObject)
                .collect(Collectors.toList());
    }
}

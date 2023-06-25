package ru.practicum.shareit.item.comment;

import java.util.List;

public interface CommentService {
    Comment createComment(Comment comment);

    List<CommentResponseDto> getAllCommentsByItemId(Long id);
}

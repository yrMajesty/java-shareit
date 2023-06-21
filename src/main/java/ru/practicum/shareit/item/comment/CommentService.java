package ru.practicum.shareit.item.comment;

import java.util.List;

public interface CommentService {
    Comment save(Comment comment);

    List<CommentResponse> findAllByItemId(Long id);
}

package ru.practicum.shareit.item.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public Comment createComment(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public List<CommentResponseDto> getAllCommentsByItemId(Long id) {
        List<Comment> comments = commentRepository.findAllByItemId(id);

        return CommentMapper.objectsToDto(comments);
    }
}

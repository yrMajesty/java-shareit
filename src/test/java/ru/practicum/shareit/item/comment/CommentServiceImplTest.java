package ru.practicum.shareit.item.comment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
class CommentServiceImplTest {
    @Autowired
    CommentServiceImpl underTest;

    @MockBean
    CommentRepository commentRepository;

    User user1;
    User user2;
    ItemRequest itemRequest;

    Item item;

    Comment comment;

    @BeforeEach
    void prepare() {
        user1 = User.builder().id(1L).name("Nikita").email("nikita@mail.ru").build();
        user2 = User.builder().id(2L).name("Mike").email("mike@mail.ru").build();
        itemRequest = ItemRequest.builder().id(10L)
                .created(LocalDateTime.of(2023, 2, 10, 17, 10, 5))
                .description("I need interesting book")
                .requestor(user2)
                .build();

        item = Item.builder().id(1L)
                .name("Book")
                .description("Good old book")
                .owner(user1)
                .available(true)
                .request(itemRequest)
                .build();

        comment = Comment.builder()
                .id(1L)
                .text("good book")
                .author(user2)
                .item(item)
                .created(LocalDateTime.of(2023, 2, 10, 17, 10, 5).plusDays(10))
                .build();
    }

    @Test
    void createRequest_successfulCreated_userIdExistAndRequestIsCorrect() {
        underTest.createComment(comment);

        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void getAllByItemId_notEmptyList_commentsExist() {
        when(commentRepository.findAllByItemId(anyLong()))
                .thenReturn(List.of(comment));

        List<CommentResponseDto> result = underTest.getAllCommentsByItemId(1L);

        verify(commentRepository, times(1)).findAllByItemId(anyLong());
        assertFalse(result.isEmpty());
        assertEquals(comment.getText(), result.get(0).getText());
        assertEquals(comment.getAuthor().getName(), result.get(0).getAuthorName());
    }

    @Test
    void getAllByItemId_emptyList_commentsExist() {
        when(commentRepository.findAllByItemId(anyLong()))
                .thenReturn(List.of());

        List<CommentResponseDto> result = underTest.getAllCommentsByItemId(1L);

        verify(commentRepository, times(1)).findAllByItemId(anyLong());
        assertTrue(result.isEmpty());
    }
}
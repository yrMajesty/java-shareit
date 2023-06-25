package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.NoFoundObjectException;
import ru.practicum.shareit.item.comment.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceIntegrationTest {
    private final ItemService itemService;
    private final UserService userService;
    private final EntityManager entityManager;

    @BeforeEach
    void prepare() {
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY = 0; " +
                "TRUNCATE TABLE comments, items, users RESTART IDENTITY;" +
                "SET REFERENTIAL_INTEGRITY = 1;");
    }

    @Test
    void createItemsAndGetByUserId_createItemAndReturnNotEmptyList() {
        ItemRequestDto itemRequest = createItemDto(true, null);

        UserRequestDto userRequest1 = UserRequestDto.builder().name("test name").email("test1@test.test").build();
        UserRequestDto userRequest2 = UserRequestDto.builder().name("test name2").email("test2@test.test").build();

        UserResponseDto user1 = userService.createUser(userRequest1);
        UserResponseDto user2 = userService.createUser(userRequest2);

        itemService.createItem(itemRequest, user1.getId());
        itemService.createItem(itemRequest, user1.getId());

        itemService.createItem(itemRequest, user2.getId());

        List<ItemResponseDto> results = itemService.getAllItemsByUserId(user1.getId());

        assertThat(results).hasSize(2);
    }

    @Test
    void createItemsAndGetByUserId_noFoundObjectException_userDoesNotExist() {
        ItemRequestDto itemRequest = createItemDto(true, null);

        NoFoundObjectException exception = assertThrows(NoFoundObjectException.class,
                () -> itemService.createItem(itemRequest, 22L));

        assertEquals("User with id='22' not found", exception.getMessage());
    }

    @Test
    void createComment_noFoundObjectException_userExistAndItemDoNotExist() {
        UserRequestDto userRequest1 = UserRequestDto.builder().name("test name").email("test@test.test").build();
        UserResponseDto user = userService.createUser(userRequest1);
        CommentRequestDto commentRequestDto = createCommentRequest();

        NoFoundObjectException exception = assertThrows(NoFoundObjectException.class,
                () -> itemService.createComment(commentRequestDto, user.getId(), 100L));

        assertEquals("Item with id='100' not found", exception.getMessage());
    }


    private static ItemRequestDto createItemDto(boolean available, Long requestId) {
        return ItemRequestDto.builder()
                .name("Test name")
                .description("Test description")
                .available(available)
                .requestId(requestId)
                .build();
    }

    private static CommentRequestDto createCommentRequest() {
        return CommentRequestDto.builder()
                .text("some text of comment")
                .build();
    }
}
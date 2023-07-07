package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.NoFoundObjectException;
import ru.practicum.shareit.item.comment.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceIntegrationTest {
    private final ItemService itemService;
    private final UserService userService;
    private final ItemRequestService itemRequestService;
    private final EntityManager entityManager;

    @BeforeEach
    void prepare() {
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY = 0; " +
                "TRUNCATE TABLE comments, items, users RESTART IDENTITY;" +
                "SET REFERENTIAL_INTEGRITY = 1;");
    }

    @Test
    void createItem_returnCreatedItem() {
        ItemRequestDto item = createItemRequestDto("Test name", true, null);
        UserRequestDto user = createUserRequestDto();

        UserResponseDto savedUser = userService.createUser(user);
        ItemResponseDto result = itemService.createItem(item, savedUser.getId());

        assertThat(result.getId()).isEqualTo(result.getId());
        assertThat(result.getName()).isEqualTo(item.getName());
    }

    @Test
    void getItemById_noFoundObjectException_itemDoesNotExist() {
        Long itemId = 1_000L;
        Long userId = 1_000L;

        assertThrows(NoFoundObjectException.class, () -> itemService.getItemById(itemId, userId));
    }

    @Test
    void getItemById_item_itemExist() {
        ItemRequestDto item = createItemRequestDto("Test name", true, null);
        UserRequestDto user = createUserRequestDto();

        UserResponseDto savedUser = userService.createUser(user);
        ItemResponseDto savedItem = itemService.createItem(item, savedUser.getId());

        ItemResponseDto result = itemService.getItemById(savedItem.getId(), savedUser.getId());

        assertThat(result.getId()).isEqualTo(savedItem.getId());
        assertThat(result.getName()).isEqualTo(item.getName());
        assertThat(result.getDescription()).isEqualTo(item.getDescription());
    }

    @Test
    void getAllItemsByUserId_emptyListItems_itemsDoNotExist() {
        UserRequestDto user = createUserRequestDto();

        UserResponseDto savedUser = userService.createUser(user);

        Long userId = savedUser.getId();
        Integer from = 0;
        Integer size = 10;

        List<ItemResponseDto> result = itemService.getAllItemsByUserId(userId, from, size);

        assertThat(result).isEmpty();
    }

    @Test
    void getAllItemsByUserId_notEmptyListItems_itemsExist() {
        ItemRequestDto item = createItemRequestDto("Test name", true, null);
        UserRequestDto user = createUserRequestDto();

        UserResponseDto savedUser = userService.createUser(user);
        ItemResponseDto savedItem = itemService.createItem(item, savedUser.getId());

        Long userId = savedUser.getId();
        Integer from = 0;
        Integer size = 10;

        List<ItemResponseDto> result = itemService.getAllItemsByUserId(userId, from, size);

        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getDescription()).isEqualTo(item.getDescription());
    }

    @Test
    void getAllItemsByUserId_noFoundObjectException_userDoesNotExist() {
        UserRequestDto user = createUserRequestDto();

        UserResponseDto savedUser = userService.createUser(user);

        Long userId = savedUser.getId();
        Integer from = 0;
        Integer size = 10;

        List<ItemResponseDto> result = itemService.getAllItemsByUserId(userId, from, size);

        assertThat(result).isEmpty();
    }

    @Test
    void searchItemByText_emptyListItems_itemsDoNotExist() {
        String text = "unknown";
        Integer from = 0;
        Integer size = 10;

        List<ItemResponseDto> result = itemService.searchItemByText(text, from, size);

        assertThat(result).isEmpty();
    }

    @Test
    void searchItemByText_emptyListItems_itemsExist() {
        ItemRequestDto item = createItemRequestDto("Test name", true, null);
        UserRequestDto user = createUserRequestDto();

        UserResponseDto savedUser = userService.createUser(user);
        ItemResponseDto savedItem = itemService.createItem(item, savedUser.getId());

        String text = "unknown";
        Integer from = 0;
        Integer size = 10;

        List<ItemResponseDto> result = itemService.searchItemByText(text, from, size);

        assertThat(result).isEmpty();
    }

    @Test
    void searchItemByText_notEmptyListItems_itemsExist() {
        ItemRequestDto item = createItemRequestDto("Test name", true, null);
        ItemRequestDto item1 = createItemRequestDto("Second item", true, null);
        UserRequestDto user = createUserRequestDto();

        UserResponseDto savedUser = userService.createUser(user);
        ItemResponseDto savedItem = itemService.createItem(item, savedUser.getId());
        ItemResponseDto savedItem1 = itemService.createItem(item1, savedUser.getId());

        String text = "test name";
        Integer from = 0;
        Integer size = 10;

        List<ItemResponseDto> result = itemService.searchItemByText(text, from, size);

        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getName()).isEqualTo("Test name");
    }

    @Test
    void createComment_noFoundObjectException_userExistAndItemDoNotExist() {
        UserRequestDto userRequest1 = createUserRequestDto();
        UserResponseDto user = userService.createUser(userRequest1);
        CommentRequestDto commentRequestDto = createCommentRequestDto();

        NoFoundObjectException exception = assertThrows(NoFoundObjectException.class,
                () -> itemService.createComment(commentRequestDto, user.getId(), 100L));

        assertEquals("Item with id='100' not found", exception.getMessage());
    }

    @Test
    void getAllByRequestIds_emptyListItems_itemsExist() {
        Long requestId = 1L;

        ItemRequestDto item = createItemRequestDto("Test name", true, null);
        UserRequestDto user = createUserRequestDto();

        UserResponseDto savedUser = userService.createUser(user);
        ItemResponseDto savedItem = itemService.createItem(item, savedUser.getId());

        List<Item> result = itemService.getAllByRequestIds(Set.of(requestId));

        assertThat(result).isEmpty();
    }

    @Test
    void getAllByRequestIds_notEmptyListItems_itemsExist() {
        UserRequestDto user = createUserRequestDto();
        UserResponseDto savedUser = userService.createUser(user);

        RequestDto request = RequestDto.builder()
                .created(LocalDateTime.now())
                .requestorId(1L)
                .description("need some item")
                .build();
        RequestDto savedRequest = itemRequestService.createRequest(request, savedUser.getId());

        ItemRequestDto item = createItemRequestDto("Test name", true, savedRequest.getId());
        ItemResponseDto savedItem = itemService.createItem(item, savedUser.getId());

        List<Item> result = itemService.getAllByRequestIds(Set.of(savedRequest.getId()));

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getRequest().getId()).isEqualTo(savedItem.getRequestId());
    }

    @Test
    void getItemByRequestId_emptyListItems_itemsExist() {
        Long requestId = 1L;

        ItemRequestDto item = createItemRequestDto("Test name", true, null);
        UserRequestDto user = createUserRequestDto();

        UserResponseDto savedUser = userService.createUser(user);
        ItemResponseDto savedItem = itemService.createItem(item, savedUser.getId());

        Item result = itemService.getItemByRequestId(requestId);

        assertThat(result).isNull();
    }

    @Test
    void getItemByRequestId_notEmptyListItems_itemsExist() {
        UserRequestDto user = createUserRequestDto();
        UserResponseDto savedUser = userService.createUser(user);

        RequestDto request = RequestDto.builder()
                .created(LocalDateTime.now())
                .requestorId(1L)
                .description("need some item")
                .build();
        RequestDto savedRequest = itemRequestService.createRequest(request, savedUser.getId());

        ItemRequestDto item = createItemRequestDto("Test name", true, savedRequest.getId());
        ItemResponseDto savedItem = itemService.createItem(item, savedUser.getId());

        Item result = itemService.getItemByRequestId(savedRequest.getId());

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(savedItem.getName());
        assertThat(result.getRequest().getId()).isEqualTo(savedRequest.getId());
    }

    @Test
    void updateItemById_updatedItem_itemExist() {
        ItemRequestDto item = createItemRequestDto("Test name", true, null);
        UserRequestDto user = createUserRequestDto();

        UserResponseDto savedUser = userService.createUser(user);
        ItemResponseDto savedItem = itemService.createItem(item, savedUser.getId());

        ItemRequestDto updateRequest = ItemRequestDto.builder()
                .name("NewName")
                .build();

        ItemResponseDto result = itemService.updateItemById(updateRequest, savedItem.getId(), savedUser.getId());
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(updateRequest.getName());
    }

    @Test
    void updateItemById_noFoundObjectException_itemDoesNotExist() {
        ItemRequestDto updateRequest = ItemRequestDto.builder()
                .name("NewName")
                .build();
        assertThrows(NoFoundObjectException.class, () -> itemService.updateItemById(updateRequest, 100L, 1L));
    }

    private static ItemRequestDto createItemRequestDto(String name, Boolean available, Long requestId) {
        return ItemRequestDto.builder()
                .name(name)
                .description("Test description")
                .available(available)
                .requestId(requestId)
                .build();
    }

    private static UserRequestDto createUserRequestDto() {
        return UserRequestDto.builder().name("test name").email("test1@test.test").build();
    }

    private static CommentRequestDto createCommentRequestDto() {
        return CommentRequestDto.builder()
                .text("some text of comment")
                .build();
    }
}
package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.CommentRequest;
import ru.practicum.shareit.item.comment.CommentResponse;
import ru.practicum.shareit.item.dto.ItemRequest;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;

    @GetMapping
    public List<ItemResponse> getAllByUserId(@RequestHeader(name = USER_ID_HEADER) Long userId) {
        return itemService.getAllByUserId(userId);
    }

    @PostMapping
    public ItemResponse createItem(@RequestHeader(name = USER_ID_HEADER) Long userId,
                                   @Valid @RequestBody ItemRequest itemRequest) {
        return itemService.createItem(itemRequest, userId);
    }

    @GetMapping("/{itemId}")
    public ItemResponse getItem(@RequestHeader(name = USER_ID_HEADER) Long userId, @PathVariable Long itemId) {
        return itemService.getItemById(itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemResponse updateItem(@RequestHeader(name = USER_ID_HEADER) Long userId, @PathVariable Long itemId,
                                   @RequestBody ItemRequest itemRequest) {
        return itemService.updateItemById(itemRequest, itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemResponse> searchByText(@RequestParam(name = "text") String text) {
        return itemService.searchItemByText(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponse createComment(@RequestHeader(name = USER_ID_HEADER) Long userId, @PathVariable Long itemId,
                                         @Valid @RequestBody CommentRequest request) {
        return itemService.createComment(request, userId, itemId);
    }
}

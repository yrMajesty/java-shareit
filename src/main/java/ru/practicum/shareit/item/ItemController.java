package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.CommentRequestDto;
import ru.practicum.shareit.item.comment.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;

    @GetMapping
    public List<ItemResponseDto> getAllItemsByUserId(@RequestHeader(name = USER_ID_HEADER) Long userId) {
        return itemService.getAllItemsByUserId(userId);
    }

    @PostMapping
    public ItemResponseDto createItem(@RequestHeader(name = USER_ID_HEADER) Long userId,
                                      @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return itemService.createItem(itemRequestDto, userId);
    }

    @GetMapping("/{itemId}")
    public ItemResponseDto getItem(@RequestHeader(name = USER_ID_HEADER) Long userId,
                                   @PathVariable(name = "itemId") Long itemId) {
        return itemService.getItemById(itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemResponseDto updateItem(@RequestHeader(name = USER_ID_HEADER) Long userId,
                                      @PathVariable(name = "itemId") Long itemId,
                                      @RequestBody ItemRequestDto itemRequestDto) {
        return itemService.updateItemById(itemRequestDto, itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemResponseDto> searchByText(@RequestParam(name = "text") String text) {
        return itemService.searchItemByText(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponseDto createComment(@RequestHeader(name = USER_ID_HEADER) Long userId,
                                            @PathVariable(name = "itemId") Long itemId,
                                            @Valid @RequestBody CommentRequestDto request) {
        return itemService.createComment(request, userId, itemId);
    }
}

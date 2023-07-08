package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.CommentRequestDto;
import ru.practicum.shareit.item.comment.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;

    @GetMapping
    public List<ItemResponseDto> getAllItemsByUserId(@RequestHeader(name = USER_ID_HEADER) Long userId,
                                                     @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                     @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemService.getAllItemsByUserId(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemResponseDto createItem(@RequestHeader(name = USER_ID_HEADER) Long userId,
                                      @RequestBody ItemRequestDto itemRequestDto) {
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
    public List<ItemResponseDto> searchByText(@RequestParam(name = "text") String text,
                                              @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemService.searchItemByText(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponseDto createComment(@RequestHeader(name = USER_ID_HEADER) Long userId,
                                            @PathVariable(name = "itemId") Long itemId,
                                            @RequestBody CommentRequestDto request) {
        return itemService.createComment(request, userId, itemId);
    }
}

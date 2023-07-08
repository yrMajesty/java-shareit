package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @GetMapping
    public ResponseEntity<Object> getUserItems(@RequestHeader(name = USER_ID_HEADER) Long userId,
                                               @RequestParam(name = "from", defaultValue = "0") @Min(0) Integer from,
                                               @RequestParam(name = "size", defaultValue = "10") @Min(1) Integer size) {
        log.info("Get users items, userId={}", userId);

        return itemClient.getAllItemsByUserId(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createItem(@RequestHeader(name = USER_ID_HEADER) Long userId,
                                             @RequestBody @Valid ItemRequestDto request) {
        log.info("Creating item {}, userId={}", request, userId);

        return itemClient.createItem(userId, request);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader(name = USER_ID_HEADER) Long userId,
                                          @PathVariable(name = "itemId") Long itemId) {
        log.info("Get item by id={}, userId={}", itemId, userId);

        return itemClient.getItemById(userId, itemId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(USER_ID_HEADER) Long userId,
                                             @PathVariable(name = "itemId") Long itemId,
                                             @RequestBody ItemRequestDto request) {
        log.info("Update item {} with id={},  userId={}", request, itemId, userId);

        return itemClient.updateItemById(userId, itemId, request);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItems(@RequestHeader(name = USER_ID_HEADER) Long userId,
                                           @RequestParam(name = "text") String text,
                                           @RequestParam(name = "from", defaultValue = "0") @Min(0) Integer from,
                                           @RequestParam(name = "size", defaultValue = "10") @Min(1) Integer size) {
        log.info("Search items, with text={}, userId={}", text, userId);

        return itemClient.searchItemByText(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(name = USER_ID_HEADER) Long userId,
                                             @PathVariable(name = "itemId") Long itemId,
                                             @RequestBody @Valid CommentRequestDto request) {
        log.info("Create comment {} for item with id={}, userId={}", request, itemId, userId);

        return itemClient.createComment(userId, itemId, request);
    }

}

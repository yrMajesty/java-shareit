package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.CommentRequest;
import ru.practicum.shareit.item.comment.CommentResponse;
import ru.practicum.shareit.item.dto.ItemRequest;
import ru.practicum.shareit.item.dto.ItemResponse;

import java.util.List;

public interface ItemService {
    ItemResponse createItem(ItemRequest itemRequest, Long userId);

    ItemResponse getItemById(Long id, Long userId);

    ItemResponse updateItemById(ItemRequest itemRequest, Long id, Long userId);

    List<ItemResponse> getAllByUserId(Long userId);

    List<ItemResponse> searchItemByText(String text);

    CommentResponse createComment(CommentRequest request, Long userId, Long itemId);
}

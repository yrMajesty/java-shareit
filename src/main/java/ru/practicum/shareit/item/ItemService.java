package ru.practicum.shareit.item;

import ru.practicum.shareit.item.comment.CommentRequest;
import ru.practicum.shareit.item.comment.CommentResponse;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.util.List;

public interface ItemService {
    ItemResponseDto createItem(ItemRequestDto itemRequestDto, Long userId);

    ItemResponseDto getItemById(Long id, Long userId);

    ItemResponseDto updateItemById(ItemRequestDto itemRequestDto, Long id, Long userId);

    List<ItemResponseDto> getAllByUserId(Long userId);

    List<ItemResponseDto> searchItemByText(String text);

    CommentResponse createComment(CommentRequest request, Long userId, Long itemId);
}

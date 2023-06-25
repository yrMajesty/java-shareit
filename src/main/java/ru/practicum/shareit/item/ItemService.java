package ru.practicum.shareit.item;

import ru.practicum.shareit.item.comment.CommentRequestDto;
import ru.practicum.shareit.item.comment.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.util.List;
import java.util.Set;

public interface ItemService {
    ItemResponseDto createItem(ItemRequestDto itemRequestDto, Long userId);

    ItemResponseDto getItemById(Long id, Long userId);

    ItemResponseDto updateItemById(ItemRequestDto itemRequestDto, Long id, Long userId);

    List<ItemResponseDto> getAllItemsByUserId(Long userId);

    List<ItemResponseDto> searchItemByText(String text);

    CommentResponseDto createComment(CommentRequestDto request, Long userId, Long itemId);

    List<Item> getAllByRequestIds(Set<Long> collect);

    Item getItemByRequestId(Long requestId);
}

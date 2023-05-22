package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto itemDto, Long userId);

    ItemDto getItemById(Long id);

    ItemDto updateById(ItemDto itemDto, Long id, Long userId);

    List<ItemDto> getAllByUserId(Long userId);

    List<ItemDto> searchByText(String text);
}

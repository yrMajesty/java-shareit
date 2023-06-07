package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemRequest;
import ru.practicum.shareit.item.dto.ItemResponse;

import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {

    public static ItemResponse objectToDto(Item item) {
        return ItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public static Item dtoToObject(ItemRequest itemDto) {
        return Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }

    public static List<ItemResponse> objectToDto(List<Item> items) {
        return items.stream()
                .map(ItemMapper::objectToDto)
                .collect(Collectors.toList());
    }
}

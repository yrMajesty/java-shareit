package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {

    public static ItemResponseDto objectToDto(Item item) {
        return ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public static Item dtoToObject(ItemRequestDto itemDto) {
        return Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }

    public static List<ItemResponseDto> objectToDto(List<Item> items) {
        return items.stream()
                .map(ItemMapper::objectToDto)
                .collect(Collectors.toList());
    }
}

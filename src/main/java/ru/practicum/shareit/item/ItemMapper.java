package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {

    public static ItemResponseDto objectToItemResponseDto(Item item) {
        return ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : 0)
                .build();
    }

    public static ItemDto objectToDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : 0)
                .build();
    }

    public static Item dtoToObject(ItemRequestDto itemDto) {
        return Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())

                .build();
    }

    public static List<ItemResponseDto> objectToItemResponseDto(List<Item> items) {
        return items.stream()
                .map(ItemMapper::objectToItemResponseDto)
                .collect(Collectors.toList());
    }
}

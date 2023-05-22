package ru.practicum.shareit.item;

import ru.practicum.shareit.request.ItemRequest;

import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {

    public static ItemDto objectToDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .owner(item.getOwner())
                .available(item.getAvailable())
                .request(item.getRequest() != null ? item.getRequest().getId() : null)
                .build();
    }

    public static Item dtoToObject(ItemDto itemDto) {
        ItemRequest itemRequest = ItemRequest.builder()
                .id(itemDto.getRequest())
                .build();

        return Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .owner(itemDto.getOwner())
                .available(itemDto.getAvailable())
                .request(itemRequest)
                .build();
    }

    public static List<ItemDto> objectToDto(List<Item> items) {
        return items.stream()
                .map(ItemMapper::objectToDto)
                .collect(Collectors.toList());
    }
}

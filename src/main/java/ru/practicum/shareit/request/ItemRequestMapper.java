package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.RequestDto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ItemRequestMapper {

    public static RequestDto objectToDto(ItemRequest object) {
        return RequestDto.builder()
                .id(object.getId())
                .requestorId(object.getRequestor().getId())
                .description(object.getDescription())
                .created(object.getCreated())
                .items(new ArrayList<>())
                .build();
    }


    public static ItemRequest dtoToObject(RequestDto dto) {
        return ItemRequest.builder()
                .description(dto.getDescription())
                .build();
    }

    public static List<RequestDto> objectToDto(List<ItemRequest> objects) {
        return objects.stream()
                .map(ItemRequestMapper::objectToDto)
                .collect(Collectors.toList());
    }
}

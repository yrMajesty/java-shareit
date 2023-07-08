package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.RequestDto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RequestMapper {

    public static RequestDto objectToDto(Request object) {
        return RequestDto.builder()
                .id(object.getId())
                .requestorId(object.getRequestor().getId())
                .description(object.getDescription())
                .created(object.getCreated())
                .items(new ArrayList<>())
                .build();
    }


    public static Request dtoToObject(RequestDto dto) {
        return Request.builder()
                .description(dto.getDescription())
                .build();
    }

    public static List<RequestDto> objectToDto(List<Request> objects) {
        return objects.stream()
                .map(RequestMapper::objectToDto)
                .collect(Collectors.toList());
    }
}

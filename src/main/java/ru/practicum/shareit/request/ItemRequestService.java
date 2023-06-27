package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.RequestDto;

import java.util.List;

public interface ItemRequestService {

    RequestDto createRequest(RequestDto request, Long userId);

    List<RequestDto> getOwnerRequestByUserId(Long userId);

    RequestDto getRequestById(Long userId, Long requestId);

    List<RequestDto> getRequestsOtherUsers(Long userId, Integer from, Integer size);
}

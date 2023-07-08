package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NoFoundObjectException;
import ru.practicum.shareit.exception.NoValidArgumentException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    public RequestDto createRequest(RequestDto request, Long userId) {
        User user = userService.findUserById(userId);

        Request itemRequest = RequestMapper.dtoToObject(request);
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());

        return RequestMapper.objectToDto(requestRepository.save(itemRequest));
    }

    @Override
    public List<RequestDto> getOwnerRequestByUserId(Long userId) {
        userService.checkExistUserById(userId);

        List<Request> requests = requestRepository.findAllByRequestorId(userId);

        List<Item> items = itemService.getAllByRequestIds(requests
                .stream()
                .map(Request::getId)
                .collect(Collectors.toSet()));

        Map<Long, RequestDto> requestDtos = RequestMapper.objectToDto(requests).stream()
                .collect(Collectors.toMap(RequestDto::getId, request -> request));

        items.forEach(item -> {
            Long id = item.getRequest().getId();
            requestDtos.get(id).getItems().add(ItemMapper.objectToDto(item));
        });

        return new ArrayList<>(requestDtos.values());
    }

    @Override
    public RequestDto getRequestById(Long userId, Long requestId) {
        userService.checkExistUserById(userId);

        Request request = requestRepository.findById(requestId).orElseThrow(
                () -> new NoFoundObjectException(String.format("ItemRequest with id='%s' not found", requestId)));

        Item item = itemService.getItemByRequestId(requestId);

        RequestDto requestDto = RequestMapper.objectToDto(request);
        requestDto.setItems(List.of(ItemMapper.objectToDto(item)));

        return requestDto;
    }

    @Override
    public List<RequestDto> getRequestsOtherUsers(Long userId, Integer from, Integer size) {
        userService.checkExistUserById(userId);

        if (from < 0 || size <= 0) {
            throw new NoValidArgumentException("The request parameters from b size are invalid and cannot be negative");
        }

        int page = from == 0 ? 0 : (from / size);
        Pageable pageable = PageRequest.of(page, size, Sort.by("created").descending());

        List<Request> requests = requestRepository.findAllByOwnerId(userId, pageable);

        Map<Long, RequestDto> requestDtos = RequestMapper.objectToDto(requests).stream()
                .collect(Collectors.toMap(RequestDto::getId, requestDto -> requestDto));

        List<Item> items = itemService.getAllByRequestIds(requests
                .stream()
                .map(Request::getId)
                .collect(Collectors.toSet()));

        items.forEach(item -> {
            Long id = item.getRequest().getId();
            requestDtos.get(id).getItems().add(ItemMapper.objectToDto(item));
        });

        return new ArrayList<>(requestDtos.values());
    }

}

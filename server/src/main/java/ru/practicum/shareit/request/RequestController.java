package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class RequestController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto createRequest(@RequestHeader(name = USER_ID_HEADER) Long userId,
                                    @RequestBody RequestDto request) {
        return requestService.createRequest(request, userId);
    }

    @GetMapping
    public List<RequestDto> getOwnerRequestsByUser(@RequestHeader(name = USER_ID_HEADER) Long userId) {
        return requestService.getOwnerRequestByUserId(userId);
    }

    @GetMapping("/{requestId}")
    public RequestDto getRequest(@RequestHeader(name = USER_ID_HEADER) Long userId,
                                 @PathVariable(name = "requestId") Long requestId) {
        return requestService.getRequestById(userId, requestId);
    }

    @GetMapping("/all")
    public List<RequestDto> getAllRequestOtherUsers(@RequestHeader(name = USER_ID_HEADER) Long userId,
                                                    @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                    @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return requestService.getRequestsOtherUsers(userId, from, size);
    }

}

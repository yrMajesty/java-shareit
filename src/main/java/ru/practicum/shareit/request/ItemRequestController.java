package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;

    @PostMapping
    public RequestDto createRequest(@RequestHeader(name = USER_ID_HEADER) Long userId,
                                    @RequestBody @Valid RequestDto request) {
        return itemRequestService.createRequest(request, userId);
    }

    @GetMapping
    public List<RequestDto> getOwnerRequestsByUser(@RequestHeader(name = USER_ID_HEADER) Long userId) {
        return itemRequestService.getOwnerRequestByUserId(userId);
    }

    @GetMapping("/{requestId}")
    public RequestDto getRequest(@RequestHeader(name = USER_ID_HEADER) Long userId,
                                       @PathVariable(name = "requestId") Long requestId) {
        return itemRequestService.getRequestById(userId, requestId);
    }

    @GetMapping("/all")
    public List<RequestDto> getAllRequestOtherUsers(@RequestHeader(name = USER_ID_HEADER) Long userId,
                                                    @PositiveOrZero @RequestParam(name = "from",
                                                            defaultValue = "0") Integer from,
                                                    @Positive @RequestParam(name = "size",
                                                            defaultValue = "10") Integer size) {
        return itemRequestService.getRequestsOtherUsers(userId, from, size);
    }

}

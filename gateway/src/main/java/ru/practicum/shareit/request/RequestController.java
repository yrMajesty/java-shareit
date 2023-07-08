package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {
    private final RequestClient requestClient;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createRequest(@RequestHeader(name = USER_ID_HEADER) Long userId,
                                                @RequestBody @Valid RequestDto request) {
        log.info("Creating request {}, userId={}", request, userId);

        return requestClient.createRequest(userId, request);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnerRequestsByUser(@RequestHeader(name = USER_ID_HEADER) Long userId) {
        log.info("Get requests by owner, userId={}", userId);

        return requestClient.getOwnerRequestsByUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequestOtherUsers(@RequestHeader(name = USER_ID_HEADER) Long userId,
                                                          @RequestParam(name = "from",
                                                                  defaultValue = "0") @Min(0) Integer from,
                                                          @RequestParam(name = "size",
                                                                  defaultValue = "10") @Min(1) Integer size) {
        log.info("Get other users requests by user with id={}, userId={}", userId, userId);

        return requestClient.getRequestsOtherUsers(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@RequestHeader(name = USER_ID_HEADER) Long userId,
                                             @PathVariable(name = "requestId") Long requestId) {
        log.info("Get request with id={}, userId={}", requestId, userId);

        return requestClient.getRequestById(userId, requestId);
    }
}

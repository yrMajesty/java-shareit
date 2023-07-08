package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NoCorrectRequestException extends RuntimeException {
    public NoCorrectRequestException(String message) {
        super(message);
    }
}

package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ErrorHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request) {

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        response.put("status", HttpStatus.BAD_REQUEST.name());

        Map<String, Object> errors = new LinkedHashMap<>();
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        response.put("errors", errors);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(value = NoFoundObjectException.class)
    public ResponseEntity<Object> handleNoFoundObjectException(final NoFoundObjectException ex) {
        Map<String, Object> response = new LinkedHashMap<>();

        response.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        response.put("status", HttpStatus.NOT_FOUND.name());
        response.put("message", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(value = AccessException.class)
    public ResponseEntity<Object> handleAccessException(final AccessException ex) {
        Map<String, Object> response = new LinkedHashMap<>();

        response.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        response.put("status", HttpStatus.FORBIDDEN.name());
        response.put("message", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(response);
    }


    @ExceptionHandler(value = ExistEmailException.class)
    public ResponseEntity<Object> handleExistEmailException(final ExistEmailException ex) {
        Map<String, Object> response = new LinkedHashMap<>();

        response.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        response.put("status", HttpStatus.CONFLICT.name());
        response.put("message", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }

    @ExceptionHandler(value = {NoCorrectRequestException.class})
    public ResponseEntity<Object> handleNoCorrectRequestException(final RuntimeException ex) {
        Map<String, Object> response = new LinkedHashMap<>();

        response.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        response.put("status", HttpStatus.BAD_REQUEST.name());
        response.put("error", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Object> handleException(final Exception ex) {
        Map<String, Object> response = new LinkedHashMap<>();

        response.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.name());
        response.put("message", "Непредвиденная ошибка: " + ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

}

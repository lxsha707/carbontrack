package com.example.carbontrack.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(
            RuntimeException exception) {

        String message = exception.getMessage();

        HttpStatus status = determineStatus(message);

        Map<String, Object> response =
                new LinkedHashMap<>();

        response.put("timestamp", LocalDateTime.now());
        response.put("status", status.value());
        response.put("error", status.getReasonPhrase());
        response.put("message", message);

        return new ResponseEntity<>(
                response,
                status
        );
    }

    private HttpStatus determineStatus(String message) {

        if (message == null) {
            return HttpStatus.BAD_REQUEST;
        }

        String lowerMessage =
                message.toLowerCase();

        if (lowerMessage.contains("not found")) {
            return HttpStatus.NOT_FOUND;
        }

        if (lowerMessage.contains("already participating")) {
            return HttpStatus.CONFLICT;
        }

        return HttpStatus.BAD_REQUEST;
    }
}
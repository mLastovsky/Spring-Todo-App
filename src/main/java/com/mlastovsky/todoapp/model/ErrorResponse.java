package com.mlastovsky.todoapp.model;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(

        LocalDateTime timestamp,
        HttpStatus status,
        String error,
        String message,
        List<String> details
) {
}

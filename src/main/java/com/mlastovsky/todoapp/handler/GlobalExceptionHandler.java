package com.mlastovsky.todoapp.handler;

import com.mlastovsky.todoapp.exception.TodoNotFoundException;
import com.mlastovsky.todoapp.exception.UserNotFoundException;
import com.mlastovsky.todoapp.model.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

import static java.lang.String.format;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(TodoNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(
            TodoNotFoundException ex,
            HttpServletRequest request
    ) {
        var pathInfo = getPathInfo(request);
        log.error("Todo not found: {} | Path: {}", ex.getMsg(), pathInfo, ex);

        var response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND,
                "Resource Not Found",
                ex.getMsg(),
                List.of(pathInfo)
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(
            UserNotFoundException ex,
            HttpServletRequest request
    ) {
        var pathInfo = getPathInfo(request);
        log.error("User not found: {} | Path: {}", ex.getMsg(), pathInfo, ex);

        var response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND,
                "Resource Not Found",
                ex.getMsg(),
                List.of(pathInfo)
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        var pathInfo = getPathInfo(request);
        var errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(this::formatValidationError)
                .toList();

        log.warn("Validation failed: {} | Path: {} | Errors: {}",
                "Request contains invalid fields",
                pathInfo,
                errors,
                ex);

        var response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST,
                "Validation Failed",
                "Request contains invalid fields",
                errors
        );

        return ResponseEntity
                .badRequest()
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(
            Exception ex,
            HttpServletRequest request
    ) {
        var pathInfo = getPathInfo(request);
        log.error("Unexpected error occurred | Path: {} | Error: {}",
                pathInfo,
                ex.getMessage(),
                ex);

        var response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                "An unexpected error occurred",
                List.of(pathInfo, ex.getMessage())
        );

        return ResponseEntity
                .internalServerError()
                .body(response);
    }

    private String formatValidationError(FieldError error) {
        return format("%s: %s",
                error.getField(),
                error.getDefaultMessage());
    }

    private String getPathInfo(HttpServletRequest request) {
        return format("%s %s",
                request.getMethod(),
                request.getRequestURI());
    }

}

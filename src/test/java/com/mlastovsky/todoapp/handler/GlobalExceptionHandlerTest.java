package com.mlastovsky.todoapp.handler;

import com.mlastovsky.todoapp.exception.TodoNotFoundException;
import com.mlastovsky.todoapp.exception.UserNotFoundException;
import com.mlastovsky.todoapp.model.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private static final int TIMESTAMP_TOLERANCE_SECONDS = 2;

    @InjectMocks
    private GlobalExceptionHandler handler;

    @Mock
    private HttpServletRequest request;

    @Mock
    private MethodArgumentNotValidException validationException;

    @Mock
    private BindingResult bindingResult;

    @Test
    void handleTodoNotFound_shouldReturnNotFoundResponse() {
        // Given
        var exception = new TodoNotFoundException("Todo 123 not found");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/test");

        // When
        var response = handler.handleTodoNotFound(exception, request);

        // Then
        assertThat(response)
                .extracting(ResponseEntity::getStatusCode)
                .isEqualTo(HttpStatus.NOT_FOUND);

        assertThat(response.getBody())
                .satisfies(body -> {
                    assertThat(body.status()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(body.error()).isEqualTo("Resource Not Found");
                    assertThat(body.message()).isEqualTo(exception.getMsg());
                    assertThat(body.details()).containsExactly("Path: GET /api/test");
                    assertThat(body.timestamp())
                            .isCloseTo(LocalDateTime.now(), within(TIMESTAMP_TOLERANCE_SECONDS, SECONDS));
                });
    }

    @Test
    void handleUserNotFound_shouldReturnNotFoundResponse() {
        // Given
        var exception = new UserNotFoundException("User 456 not found");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/test");

        // When
        var response = handler.handleTodoNotFound(exception, request);

        // Then
        assertThat(response.getBody())
                .usingRecursiveComparison()
                .ignoringFields("timestamp", "message")
                .isEqualTo(new ErrorResponse(
                        null,
                        HttpStatus.NOT_FOUND,
                        "Resource Not Found",
                        null,
                        List.of("Path: GET /api/test")
                ));
    }

    @Test
    void handleValidationErrors_shouldReturnBadRequestWithFormattedErrors() {
        // Given
        var fieldErrors = List.of(
                new FieldError("task", "title", "Title required"),
                new FieldError("task", "dueDate", "Invalid date format")
        );
        when(validationException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

        // When
        var response = handler.handleValidation(validationException, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        assertThat(response.getBody())
                .satisfies(body -> {
                    assertThat(body.error()).isEqualTo("Validation Failed");
                    assertThat(body.message()).isEqualTo("Request contains invalid fields");
                    assertThat(body.details())
                            .containsExactlyInAnyOrder(
                                    "title: Title required",
                                    "dueDate: Invalid date format"
                            );
                });
    }

    @Test
    void handleEmptyValidationErrors_shouldReturnBadRequestWithEmptyDetails() {
        // Given
        when(validationException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(Collections.emptyList());

        // When
        var response = handler.handleValidation(validationException, request);

        // Then
        assertThat(response.getBody().details())
                .isEmpty();
    }

    @Test
    void handleGenericException_shouldReturnInternalServerError() {
        // Given
        var exception = new RuntimeException("Unexpected error");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/test");

        // When
        var response = handler.handleAll(exception, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().details())
                .containsExactly("Path: GET /api/test", "Unexpected error");
    }

}

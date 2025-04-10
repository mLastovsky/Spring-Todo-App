package com.mlastovsky.todoapp.dto;

import com.mlastovsky.todoapp.model.TodoStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TodoRequestDto(

        @NotNull(message = "description is required")
        @NotBlank(message = "description should not be blank")
        String description,

        @NotNull(message = "status is required")
        @NotBlank(message = "status should be present")
        TodoStatus status,

        @NotNull(message = "ownerId is mandatory")
        @NotBlank(message = "ownerId should not be blank")
        Long ownerId
) {
}

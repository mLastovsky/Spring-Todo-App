package com.mlastovsky.todoapp.dto;

import com.mlastovsky.todoapp.model.TodoStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TodoUpdateRequestDto(

        @NotNull(message = "id is required")
        @NotBlank(message = "id should not be blank")
        Long id,

        String description,

        TodoStatus status,

        @NotNull(message = "ownerId is mandatory")
        @NotBlank(message = "ownerId should not be blank")
        Long ownerId
) {
}

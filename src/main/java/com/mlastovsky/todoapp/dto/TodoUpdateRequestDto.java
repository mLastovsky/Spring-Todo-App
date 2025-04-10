package com.mlastovsky.todoapp.dto;

import com.mlastovsky.todoapp.model.TodoStatus;
import jakarta.validation.constraints.NotNull;

public record TodoUpdateRequestDto(

        String description,

        TodoStatus status,

        @NotNull(message = "ownerId is mandatory")
        Long ownerId
) {
}

package com.mlastovsky.todoapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TodoRequestDto(

        @NotBlank(message = "description should not be blank")
        String description,

        @NotNull(message = "ownerId is mandatory")
        Long ownerId
) {
}

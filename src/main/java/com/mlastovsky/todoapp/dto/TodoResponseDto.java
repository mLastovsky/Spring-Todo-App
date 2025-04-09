package com.mlastovsky.todoapp.dto;

import com.mlastovsky.todoapp.model.TodoStatus;

import java.time.LocalDateTime;

public record TodoResponseDto(

        Long id,
        String description,
        TodoStatus status,
        Long ownerId,
        LocalDateTime createdAt,
        LocalDateTime lastModifiedAt
) {
}

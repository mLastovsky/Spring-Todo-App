package com.mlastovsky.todoapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserUpdateRequestDto(

        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        String username,

        @Email
        String email,

        @Size(min = 8, message = "Password must be at least 8 characters long")
        String password
) {
}

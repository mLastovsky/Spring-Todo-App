package com.mlastovsky.todoapp.mapper;

import com.mlastovsky.todoapp.dto.UserRequestDto;
import com.mlastovsky.todoapp.dto.UserResponseDto;
import com.mlastovsky.todoapp.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponseDto fromUser(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
    }

    public User toUser(UserRequestDto dto) {
        return User.builder()
                .username(dto.username())
                .email(dto.email())
                .password(dto.password())
                .build();
    }

}

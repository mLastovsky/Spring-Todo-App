package com.mlastovsky.todoapp.service;

import com.mlastovsky.todoapp.dto.UserRequestDto;
import com.mlastovsky.todoapp.dto.UserResponseDto;

import java.util.List;

public interface UserService {

    UserResponseDto findById(Long id);

    List<UserResponseDto> findAllUsers();

    UserResponseDto createUser(UserRequestDto request);

    UserResponseDto fullyUpdateUser(Long id, UserRequestDto request);

    UserResponseDto partiallyUpdateUser(Long id, UserRequestDto request);

    void deleteUser(Long id);

}

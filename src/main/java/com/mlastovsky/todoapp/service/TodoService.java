package com.mlastovsky.todoapp.service;

import com.mlastovsky.todoapp.dto.TodoRequestDto;
import com.mlastovsky.todoapp.dto.TodoResponseDto;

import java.util.List;

public interface TodoService {

    TodoResponseDto createTodo(TodoRequestDto todoRequestDto);

    List<TodoResponseDto> findAllTodos();

    TodoResponseDto findById(Long id);

    List<TodoResponseDto> findByUserId(Long userId);

    TodoResponseDto fullyUpdateTodo(Long id, TodoRequestDto request);

    TodoResponseDto partiallyUpdateTodo(Long id, TodoRequestDto todoRequestDto);

    void deleteTodo(Long id);

}

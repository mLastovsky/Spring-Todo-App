package com.mlastovsky.todoapp.mapper;

import com.mlastovsky.todoapp.dto.TodoRequestDto;
import com.mlastovsky.todoapp.dto.TodoResponseDto;
import com.mlastovsky.todoapp.model.Todo;
import com.mlastovsky.todoapp.model.User;
import org.springframework.stereotype.Component;

@Component
public class TodoMapper {

    public Todo toTodo(TodoRequestDto todoRequestDto, User owner) {
        return Todo.builder()
                .description(todoRequestDto.description())
                .status(todoRequestDto.status())
                .owner(owner)
                .build();
    }

    public TodoResponseDto fromTodo(Todo todo) {
        return new TodoResponseDto(
                todo.getId(),
                todo.getDescription(),
                todo.getStatus(),
                todo.getOwner().getId(),
                todo.getCreatedAt(),
                todo.getLastModifiedAt()
        );
    }

}

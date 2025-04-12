package com.mlastovsky.todoapp.service;

import com.mlastovsky.todoapp.dto.TodoRequestDto;
import com.mlastovsky.todoapp.dto.TodoResponseDto;
import com.mlastovsky.todoapp.dto.TodoUpdateRequestDto;
import com.mlastovsky.todoapp.exception.TodoNotFoundException;
import com.mlastovsky.todoapp.exception.UserNotFoundException;
import com.mlastovsky.todoapp.mapper.TodoMapper;
import com.mlastovsky.todoapp.model.TodoStatus;
import com.mlastovsky.todoapp.repository.TodoRepository;
import com.mlastovsky.todoapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.lang.String.format;

@Service
@Transactional
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;
    private final TodoMapper todoMapper;

    public TodoResponseDto createTodo(@Valid TodoRequestDto todoRequestDto) {
        var owner = userRepository.findById(todoRequestDto.ownerId())
                .orElseThrow(() -> new UserNotFoundException(
                                format("User with ID:: %d not found", todoRequestDto.ownerId())
                        )
                );

        var todo = todoMapper.toTodo(todoRequestDto, owner);
        return todoMapper.fromTodo(todoRepository.save(todo));
    }

    public List<TodoResponseDto> findAllTodos() {
        return todoRepository.findAllTodos().stream()
                .map(todoMapper::fromTodo)
                .toList();
    }

    public TodoResponseDto findById(Long id) {
        return todoRepository.findById(id)
                .map(todoMapper::fromTodo)
                .orElseThrow(
                        () -> new TodoNotFoundException(
                               format("Todo with ID:: %d not found", id)
                        )
                );
    }

    public List<TodoResponseDto> findByUserId(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        format("User with ID:: %d not found", userId)
                ));

        return todoRepository.findAllByUserId(userId).stream()
                .map(todoMapper::fromTodo)
                .toList();
    }

    public TodoResponseDto fullyUpdateTodo(Long id, @Valid TodoUpdateRequestDto todoRequestDto) {
        var todo = todoRepository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException(
                        format("Todo with ID:: %d not found", id)
                ));

        todo.setDescription(todoRequestDto.description());
        todo.setStatus(todoRequestDto.status());

        var updatedTodo = todoRepository.save(todo);
        return todoMapper.fromTodo(updatedTodo);
    }

    public TodoResponseDto partiallyUpdateTodo(Long id, @Valid TodoUpdateRequestDto todoRequestDto) {
        var todo = todoRepository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException(
                        format("Todo with ID:: %d not found", id)
                ));

        if (todoRequestDto.description() != null) {
            todo.setDescription(todoRequestDto.description());
        }
        if (todoRequestDto.status() != null) {
            todo.setStatus(todoRequestDto.status());
        }

        var updatedTodo = todoRepository.save(todo);
        return todoMapper.fromTodo(updatedTodo);
    }

    public void deleteTodo(Long id) {
        if(!todoRepository.existsById(id)) {
            throw new TodoNotFoundException(
                    format("Todo with ID:: %d not found", id)
            );
        }
        todoRepository.deleteById(id);
    }

}

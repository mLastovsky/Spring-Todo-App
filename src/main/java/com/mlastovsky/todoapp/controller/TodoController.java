package com.mlastovsky.todoapp.controller;

import com.mlastovsky.todoapp.dto.TodoRequestDto;
import com.mlastovsky.todoapp.dto.TodoResponseDto;
import com.mlastovsky.todoapp.service.TodoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @PostMapping
    public ResponseEntity<TodoResponseDto> createTodo(
            @RequestBody @Valid TodoRequestDto todoRequestDto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(todoService.createTodo(todoRequestDto));
    }

    @GetMapping
    public ResponseEntity<List<TodoResponseDto>> getAllTodos() {
        return ResponseEntity.ok(todoService.findAllTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TodoResponseDto> getTodoById(
            @PathVariable(name = "id") Long id
    ) {
        return ResponseEntity.ok(todoService.findById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TodoResponseDto>> getTodosByUser(
            @PathVariable(name = "userId") Long userId
    ) {
        return ResponseEntity.ok(todoService.findByUserId(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TodoResponseDto> fullyUpdateTodo(
            @PathVariable(name = "id") Long id,
            @RequestBody @Valid TodoRequestDto todoRequestDto
    ) {
        return ResponseEntity.ok(todoService.fullyUpdateTodo(id, todoRequestDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TodoResponseDto> partiallyUpdateTodo(
            @PathVariable(name = "id") Long id,
            @RequestBody @Valid TodoRequestDto todoRequestDto
    ) {
        return ResponseEntity.ok(todoService.partiallyUpdateTodo(id, todoRequestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(
            @PathVariable(name = "id") Long id
    ) {
        todoService.deleteTodo(id);
        return ResponseEntity.noContent().build();
    }

}

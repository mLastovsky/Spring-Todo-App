package com.mlastovsky.todoapp.controller;

import com.mlastovsky.todoapp.dto.UserRequestDto;
import com.mlastovsky.todoapp.dto.UserResponseDto;
import com.mlastovsky.todoapp.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(
            @RequestBody @Valid UserRequestDto userRequestDto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.createUser(userRequestDto));
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        return ResponseEntity.ok(userService.findAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(
            @PathVariable(name = "id") Long id
    ) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> fullyUpdateUser(
            @PathVariable(name = "id") Long id,
            @RequestBody @Valid UserRequestDto userRequestDto
    ) {
        return ResponseEntity.ok(userService.fullyUpdateUser(id, userRequestDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserResponseDto> partiallyUpdateUser(
            @PathVariable(name = "id") Long id,
            @RequestBody @Valid UserRequestDto userRequestDto
    ) {
        return ResponseEntity.ok(userService.partiallyUpdateUser(id, userRequestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable(name = "id") Long id
    ) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

}

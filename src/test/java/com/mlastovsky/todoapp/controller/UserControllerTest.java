package com.mlastovsky.todoapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mlastovsky.todoapp.dto.UserRequestDto;
import com.mlastovsky.todoapp.dto.UserResponseDto;
import com.mlastovsky.todoapp.dto.UserUpdateRequestDto;
import com.mlastovsky.todoapp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.empty;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private static final String BASE_URL = "/api/v1/users";
    private static final Long USER_ID = 1L;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void createUser_shouldReturnCreatedStatus() throws Exception {
        // Given
        var requestDto = new UserRequestDto(
                "Test User",
                "test@example.com",
                "12345678"
        );

        var responseDto = new UserResponseDto(
                USER_ID,
                "Test User",
                "test@example.com"
        );

        when(userService.createUser(any(UserRequestDto.class))).thenReturn(responseDto);

        // When & Then
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(USER_ID))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.username").value("Test User"));

        verify(userService).createUser(any(UserRequestDto.class));
    }

    @Test
    void createUser_shouldThrowValidationExceptionWhenNameIsBlank() throws Exception {
        // Given
        var requestDto = new UserRequestDto(
                "",
                "test@example.com",
                "12345678"
        );

        // When & Then
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any(UserRequestDto.class));
    }

    @Test
    void createUser_shouldThrowValidationExceptionWhenEmailIsInvalid() throws Exception {
        // Given
        var requestDto = new UserRequestDto(
                "Test User",
                "invalid-email",
                "12345678"
        );

        // When & Then
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any(UserRequestDto.class));
    }

    @Test
    void createUser_shouldThrowValidationExceptionWhenPasswordIsTooShort() throws Exception {
        // Given
        var requestDto = new UserRequestDto(
                "Test User",
                "test@example.com",
                "1234567"
        );

        // When & Then
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any(UserRequestDto.class));
    }

    @Test
    void createUser_shouldThrowValidationExceptionWhenEmailIsNull() throws Exception {
        // Given
        var requestDto = new UserRequestDto(
                "Test User",
                null,  // email не указан
                "12345678"
        );

        // When & Then
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any(UserRequestDto.class));
    }

    @Test
    void createUser_shouldThrowValidationExceptionWhenAllFieldsAreMissing() throws Exception {
        // Given
        var requestDto = new UserRequestDto(
                null,
                null,
                null
        );

        // When & Then
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any(UserRequestDto.class));
    }

    @Test
    void getAllUsers_shouldReturnOkStatusWithUsersList() throws Exception {
        // Given
        var users = List.of(
                new UserResponseDto(
                        1L,
                        "User One",
                        "user1@test.com"
                ),

                new UserResponseDto(
                        2L,
                        "User Two",
                        "user2@test.com"
                )
        );

        when(userService.findAllUsers()).thenReturn(users);

        // When & Then
        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].email").value("user1@test.com"))
                .andExpect(jsonPath("$[0].username").value("User One"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].email").value("user2@test.com"))
                .andExpect(jsonPath("$[1].username").value("User Two"));

        verify(userService).findAllUsers();
    }

    @Test
    void getAllUsers_shouldReturnOkStatusWhenNoUsersExist() throws Exception {
        // Given
        when(userService.findAllUsers()).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", empty()));

        verify(userService).findAllUsers();
    }

    @Test
    void getUserById_shouldReturnUser() throws Exception {
        // Given
        var userDto = new UserResponseDto(
                USER_ID,
                "Test User",
                "test@example.com");

        when(userService.findById(USER_ID)).thenReturn(userDto);

        // When & Then
        mockMvc.perform(get(BASE_URL + "/{id}", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(USER_ID))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.username").value("Test User"));

        verify(userService).findById(USER_ID);
    }

    @Test
    void fullyUpdateUser_shouldReturnUpdatedUser() throws Exception {
        // Given
        var updateDto = new UserUpdateRequestDto(
                "Updated User",
                "updated@example.com",
                "12345678"
        );

        var updatedUser = new UserResponseDto(
                USER_ID,
                "Updated User",
                "updated@example.com"
        );

        when(userService.fullyUpdateUser(eq(USER_ID), any(UserUpdateRequestDto.class))).thenReturn(updatedUser);

        // When & Then
        mockMvc.perform(put(BASE_URL + "/{id}", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(USER_ID))
                .andExpect(jsonPath("$.email").value("updated@example.com"))
                .andExpect(jsonPath("$.username").value("Updated User"));

        verify(userService).fullyUpdateUser(eq(USER_ID), any(UserUpdateRequestDto.class));
    }

    @Test
    void partiallyUpdateUser_shouldReturnPartiallyUpdatedUser() throws Exception {
        // Given
        var updateDto = new UserUpdateRequestDto(
                "Updated Name",
                null,
                null
        );

        var updatedUser = new UserResponseDto(
                USER_ID,
                "Updated Name",
                "test@example.com"
        );

        when(userService.partiallyUpdateUser(eq(USER_ID), any(UserUpdateRequestDto.class))).thenReturn(updatedUser);

        // When & Then
        mockMvc.perform(patch(BASE_URL + "/{id}", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(USER_ID))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.username").value("Updated Name"));

        verify(userService).partiallyUpdateUser(eq(USER_ID), any(UserUpdateRequestDto.class));
    }

    @Test
    void deleteUser_shouldReturnNoContentStatus() throws Exception {
        // Given
        doNothing().when(userService).deleteUser(USER_ID);

        // When & Then
        mockMvc.perform(delete(BASE_URL + "/{id}", USER_ID))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(USER_ID);
    }

}

package com.mlastovsky.todoapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mlastovsky.todoapp.dto.TodoRequestDto;
import com.mlastovsky.todoapp.dto.TodoResponseDto;
import com.mlastovsky.todoapp.dto.TodoUpdateRequestDto;
import com.mlastovsky.todoapp.service.TodoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static com.mlastovsky.todoapp.model.TodoStatus.*;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TodoControllerTest {

    private static final String BASE_URL = "/api/v1/todos";
    private static final Long TEST_ID = 1L;
    private static final Long OWNER_ID = 1L;

    @Mock
    private TodoService todoService;

    @InjectMocks
    private TodoController todoController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private TodoResponseDto todoResponseDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(todoController).build();
        objectMapper = new ObjectMapper();

        todoResponseDto = new TodoResponseDto(
                TEST_ID,
                "test description",
                IN_PROGRESS,
                OWNER_ID,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    void createTodo_ShouldReturnCreatedStatus() throws Exception {
        // Given
        var todoRequestDto = new TodoRequestDto(
                "test description",
                OWNER_ID
        );

        when(todoService.createTodo(any(TodoRequestDto.class))).thenReturn(todoResponseDto);

        // When & Then
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todoRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(TEST_ID))
                .andExpect(jsonPath("$.description").value("test description"))
                .andExpect(jsonPath("$.status").value(IN_PROGRESS.name()))
                .andExpect(jsonPath("$.ownerId").value(OWNER_ID))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.lastModifiedAt").exists());

        verify(todoService).createTodo(any(TodoRequestDto.class));
    }

    @Test
    void createTodo_ShouldThrowsValidationException_WhenOwnerIdNotPresent() throws Exception {
        // Given
        var todoRequestDto = new TodoRequestDto(
                "test description",
                null
        );

        // When & Then
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todoRequestDto)))
                .andExpect(status().isBadRequest());

        verify(todoService, never()).createTodo(any());
    }

    @Test
    void createTodo_ShouldThrowsValidationException_WhenDescriptionNotPresent() throws Exception {
        // Given
        var todoRequestDto = new TodoRequestDto(
                null,
                OWNER_ID
        );

        // When & Then
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todoRequestDto)))
                .andExpect(status().isBadRequest());

        verify(todoService, never()).createTodo(any());
    }

    @Test
    void createTodo_ShouldThrowValidationException_WhenRequestDtoNotValid() throws Exception {
        // Given
        var todoRequestDto = new TodoRequestDto(
                null,
                null
        );

        // When & Then
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todoRequestDto)))
                .andExpect(status().isBadRequest());

        verify(todoService, never()).createTodo(any());
    }

    @Test
    void getTodoById_ShouldReturnTodo() throws Exception {
        // Given
        when(todoService.findById(TEST_ID)).thenReturn(todoResponseDto);

        // When & Then
        mockMvc.perform(get(BASE_URL + "/{id}", TEST_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_ID))
                .andExpect(jsonPath("$.description").value(todoResponseDto.description()));

        verify(todoService).findById(TEST_ID);
    }

    @Test
    void getAllTodos_ShouldReturnList() throws Exception {
        // Given
        when(todoService.findAllTodos()).thenReturn(List.of(todoResponseDto));

        // When & Then
        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(TEST_ID))
                .andExpect(jsonPath("$[0].description").value("test description"));

        verify(todoService).findAllTodos();
    }

    @Test
    void fullyUpdateTodo_ShouldReturnUpdatedTodo() throws Exception {
        // Given
        var updateRequest = new TodoUpdateRequestDto(
                "updated description",
                COMPLETED,
                OWNER_ID
        );

        var updatedResponse = new TodoResponseDto(
                TEST_ID,
                "updated description",
                COMPLETED,
                OWNER_ID,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(todoService.fullyUpdateTodo(eq(TEST_ID), any(TodoUpdateRequestDto.class)))
                .thenReturn(updatedResponse);

        // When & Then
        mockMvc.perform(put(BASE_URL + "/{id}", TEST_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("updated description"))
                .andExpect(jsonPath("$.status").value(COMPLETED.name()));

        verify(todoService).fullyUpdateTodo(eq(TEST_ID), any(TodoUpdateRequestDto.class));
    }

    @Test
    void deleteTodo_ShouldReturnNoContent() throws Exception {
        // Given
        doNothing().when(todoService).deleteTodo(TEST_ID);

        // When & Then
        mockMvc.perform(delete(BASE_URL + "/{id}", TEST_ID))
                .andExpect(status().isNoContent());

        verify(todoService).deleteTodo(TEST_ID);
    }

    @Test
    void getTodosByUser_ShouldReturnUserTodos() throws Exception {
        // Given
        var userId = 1L;
        var todo1 = new TodoResponseDto(
                1L,
                "Task 1",
                IN_PROGRESS,
                userId,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        var todo2 = new TodoResponseDto(
                2L,
                "Task 2",
                COMPLETED,
                userId,
                LocalDateTime.now(),
                LocalDateTime.now());

        when(todoService.findByUserId(userId)).thenReturn(List.of(todo1, todo2));

        // When & Then
        mockMvc.perform(get(BASE_URL + "/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(todo1.id()))
                .andExpect(jsonPath("$[0].description").value(todo1.description()))
                .andExpect(jsonPath("$[0].status").value(todo1.status().name()))
                .andExpect(jsonPath("$[1].id").value(todo2.id()))
                .andExpect(jsonPath("$[1].status").value(todo2.status().name()));

        verify(todoService).findByUserId(userId);
    }

    @Test
    void getTodosByUser_ShouldReturnEmptyList_WhenNoTodosFound() throws Exception {
        // Given
        Long userId = 2L;
        when(todoService.findByUserId(userId)).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get(BASE_URL + "/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", empty()));

        verify(todoService).findByUserId(userId);
    }

    @Test
    void partiallyUpdateTodo_ShouldUpdateFields() throws Exception {
        // Given
        var todoId = 1L;
        var updateRequest = new TodoUpdateRequestDto(
                "Updated description",
                COMPLETED,
                OWNER_ID
        );

        var updatedTodo = new TodoResponseDto(
                todoId,
                "Updated description",
                COMPLETED,
                1L,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(todoService.partiallyUpdateTodo(eq(todoId), any(TodoUpdateRequestDto.class)))
                .thenReturn(updatedTodo);

        // When & Then
        mockMvc.perform(patch(BASE_URL + "/{id}", todoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(todoId))
                .andExpect(jsonPath("$.description").value(updateRequest.description()))
                .andExpect(jsonPath("$.status").value(updateRequest.status().name()));

        verify(todoService).partiallyUpdateTodo(eq(todoId), any(TodoUpdateRequestDto.class));
    }

}

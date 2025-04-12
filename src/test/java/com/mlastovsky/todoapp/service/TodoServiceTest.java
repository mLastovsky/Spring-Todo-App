package com.mlastovsky.todoapp.service;

import com.mlastovsky.todoapp.dto.TodoRequestDto;
import com.mlastovsky.todoapp.dto.TodoResponseDto;
import com.mlastovsky.todoapp.dto.TodoUpdateRequestDto;
import com.mlastovsky.todoapp.exception.TodoNotFoundException;
import com.mlastovsky.todoapp.exception.UserNotFoundException;
import com.mlastovsky.todoapp.mapper.TodoMapper;
import com.mlastovsky.todoapp.model.Todo;
import com.mlastovsky.todoapp.model.TodoStatus;
import com.mlastovsky.todoapp.model.User;
import com.mlastovsky.todoapp.repository.TodoRepository;
import com.mlastovsky.todoapp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TodoMapper todoMapper;

    @InjectMocks
    private TodoService todoService;

    @Test
    void createTodo_ShouldCreateTodo_WhenUserExists() {
        var userId = 1L;
        var requestDto = new TodoRequestDto(
                "Test todo",
                userId);

        var user = User.builder()
                .id(userId)
                .build();

        var todo = Todo.builder()
                .id(1L)
                .status(TodoStatus.IN_PROGRESS)
                .build();

        var expectedResponse = new TodoResponseDto(
                1L,
                "Test todo",
                TodoStatus.IN_PROGRESS,
                userId,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(todoMapper.toTodo(requestDto, user)).thenReturn(todo);
        when(todoRepository.save(todo)).thenReturn(todo);
        when(todoMapper.fromTodo(todo)).thenReturn(expectedResponse);

        var result = todoService.createTodo(requestDto);

        assertThat(result)
                .isNotNull()
                .isEqualTo(expectedResponse);

        assertThat(result.status()).isEqualTo(expectedResponse.status());

        verify(userRepository).findById(userId);
        verify(todoMapper).toTodo(requestDto, user);
        verify(todoRepository).save(todo);
        verify(todoMapper).fromTodo(todo);
    }

    @Test
    void createTodo_ShouldThrowException_WhenUserNotExists() {
        var userId = 99L;

        var todoRequestDto = new TodoRequestDto(
                "test",
                userId
        );

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> todoService.createTodo(todoRequestDto));
        verify(userRepository).findById(userId);
        verify(todoRepository, never()).save(any());
        verifyNoInteractions(todoMapper);
    }

    @Test
    void findAllTodos_ShouldReturnAllTodos() {
        var userId = 1L;
        var todo1 = Todo.builder()
                .id(1L)
                .status(TodoStatus.IN_PROGRESS)
                .build();

        var todo2 = Todo.builder()
                .id(2L)
                .status(TodoStatus.COMPLETED)
                .build();

        var response1 = new TodoResponseDto(
                1L,
                "Todo 1",
                TodoStatus.IN_PROGRESS,
                userId,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        var response2 = new TodoResponseDto(
                2L,
                "Todo 2",
                TodoStatus.COMPLETED,
                userId,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(todoRepository.findAllTodos()).thenReturn(List.of(todo1, todo2));
        when(todoMapper.fromTodo(todo1)).thenReturn(response1);
        when(todoMapper.fromTodo(todo2)).thenReturn(response2);

        var result = todoService.findAllTodos();

        assertThat(result)
                .hasSize(2)
                .containsExactly(response1, response2)
                .extracting(TodoResponseDto::status)
                .containsExactly(TodoStatus.IN_PROGRESS, TodoStatus.COMPLETED);

        verify(todoRepository).findAllTodos();
        verify(todoMapper, times(2)).fromTodo(any(Todo.class));
    }

    @Test
    void findAllTodos_ShouldReturnEmptyList_WhenNoTodos() {
        when(todoRepository.findAllTodos()).thenReturn(List.of());

        var result = todoService.findAllTodos();

        assertThat(result).isEmpty();
        verify(todoRepository).findAllTodos();
        verifyNoInteractions(todoMapper);
    }

    @Test
    void findById_ShouldReturnTodo_WhenExists() {
        var todoId = 1L;
        var todo = Todo.builder()
                .id(todoId)
                .status(TodoStatus.IN_PROGRESS)
                .build();

        var expectedResponse = new TodoResponseDto(
                todoId,
                "Test todo",
                TodoStatus.IN_PROGRESS,
                1L,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(todoRepository.findById(todoId)).thenReturn(Optional.of(todo));
        when(todoMapper.fromTodo(todo)).thenReturn(expectedResponse);

        var result = todoService.findById(todoId);

        assertThat(result)
                .isNotNull()
                .isEqualTo(expectedResponse);
        assertThat(result.id()).isEqualTo(todoId);

        verify(todoRepository).findById(todoId);
        verify(todoMapper).fromTodo(todo);
    }

    @Test
    void findById_ShouldThrowException_WhenTodoNotFound() {
        var todoId = 99L;
        when(todoRepository.findById(todoId)).thenReturn(Optional.empty());

        assertThrows(TodoNotFoundException.class, () -> todoService.findById(todoId));
        verify(todoRepository).findById(todoId);
        verifyNoInteractions(todoMapper);
    }

    @Test
    void findByUserId_ShouldReturnUserTodos() {
        var userId = 1L;
        var todo1 = Todo.builder()
                .id(1L)
                .status(TodoStatus.IN_PROGRESS)
                .build();

        var todo2 = Todo.builder()
                .id(2L)
                .status(TodoStatus.COMPLETED)
                .build();

        var response1 = new TodoResponseDto(
                1L,
                "Todo 1",
                TodoStatus.IN_PROGRESS,
                userId,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        var response2 = new TodoResponseDto(
                2L,
                "Todo 2",
                TodoStatus.COMPLETED,
                userId,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(User.builder().build()));
        when(todoRepository.findAllByUserId(userId)).thenReturn(List.of(todo1, todo2));
        when(todoMapper.fromTodo(todo1)).thenReturn(response1);
        when(todoMapper.fromTodo(todo2)).thenReturn(response2);

        var result = todoService.findByUserId(userId);

        assertThat(result)
                .hasSize(2)
                .containsExactly(response1, response2)
                .extracting(TodoResponseDto::ownerId)
                .containsOnly(userId);

        verify(todoRepository).findAllByUserId(userId);
        verify(todoMapper, times(2)).fromTodo(any(Todo.class));
    }

    @Test
    void findByUserId_ShouldReturnEmptyList_WhenNoTodos() {
        var userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(User.builder().build()));
        when(todoRepository.findAllByUserId(userId)).thenReturn(List.of());

        var result = todoService.findByUserId(userId);

        assertThat(result).isEmpty();
        verify(todoRepository).findAllByUserId(userId);
        verifyNoInteractions(todoMapper);
    }

    @Test
    void findByUserId_ShouldThrowException_WhenUserNotFound() {
        var nonExistUserId = 99L;

        when(userRepository.findById(nonExistUserId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> todoService.findByUserId(nonExistUserId));
        verify(userRepository).findById(nonExistUserId);
    }

    @Test
    void deleteTodo_ShouldDelete_WhenTodoExists() {
        var todoId = 1L;

        when(todoRepository.existsById(todoId)).thenReturn(true);

        todoService.deleteTodo(todoId);

        verify(todoRepository).existsById(todoId);
        verify(todoRepository).deleteById(todoId);
        verifyNoMoreInteractions(todoRepository);
    }

    @Test
    void deleteTodo_ShouldThrowException_WhenTodoNotFound() {
        var todoId = 999L;

        when(todoRepository.existsById(todoId)).thenReturn(false);

        assertThrows(TodoNotFoundException.class, () -> todoService.deleteTodo(todoId));
        verify(todoRepository).existsById(todoId);
        verify(todoRepository, never()).deleteById(anyLong());
    }

    @Test
    void createTodo_ShouldSetInProgressStatusByDefault() {
        var userId = 1L;
        var requestDto = new TodoRequestDto(
                "Test todo",
                userId);

        var user = User.builder()
                .id(userId)
                .build();

        var todo = Todo.builder()
                .id(1L)
                .status(TodoStatus.IN_PROGRESS)
                .build();

        var expectedResponse = new TodoResponseDto(
                1L,
                "Test todo",
                TodoStatus.IN_PROGRESS,
                userId,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(todoMapper.toTodo(requestDto, user)).thenReturn(todo);
        when(todoRepository.save(todo)).thenReturn(todo);
        when(todoMapper.fromTodo(todo)).thenReturn(expectedResponse);

        var result = todoService.createTodo(requestDto);

        assertThat(result.status()).isEqualTo(expectedResponse.status());
        verify(todoRepository).save(todo);
    }

    @Test
    void completedTodo_ShouldHaveCompletedStatus() {
        var todoId = 1L;
        var todo = Todo.builder()
                .id(todoId)
                .status(TodoStatus.COMPLETED)
                .build();

        var expectedResponse = new TodoResponseDto(
                todoId,
                "Completed todo",
                TodoStatus.COMPLETED,
                1L,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(todoRepository.findById(todoId)).thenReturn(Optional.of(todo));
        when(todoMapper.fromTodo(todo)).thenReturn(expectedResponse);

        var result = todoService.findById(todoId);

        assertThat(result.status()).isEqualTo(expectedResponse.status());
        verify(todoMapper).fromTodo(todo);
    }

    @Test
    void fullyUpdateTodo_shouldUpdateAllFields() {
        var todoId = 1L;
        var userId = 1L;

        var updateDto = new TodoUpdateRequestDto(
                "Updated title",
                TodoStatus.COMPLETED,
                userId
        );

        var existingTodo = Todo.builder()
                .id(todoId)
                .description("Old description")
                .status(TodoStatus.IN_PROGRESS)
                .owner(User.builder()
                        .id(userId)
                        .build())
                .build();

        var updatedTodo = Todo.builder()
                .id(todoId)
                .description("Updated description")
                .status(TodoStatus.COMPLETED)
                .owner(User.builder()
                        .id(userId)
                        .build())
                .build();

        var expectedResponse = new TodoResponseDto(
                todoId,
                "Updated description",
                TodoStatus.COMPLETED,
                userId,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(todoRepository.findById(todoId)).thenReturn(Optional.of(existingTodo));
        when(todoRepository.save(any(Todo.class))).thenReturn(updatedTodo);
        when(todoMapper.fromTodo(updatedTodo)).thenReturn(expectedResponse);

        var result = todoService.fullyUpdateTodo(todoId, updateDto);

        assertThat(result)
                .isNotNull()
                .isEqualTo(expectedResponse);
        assertThat(result.description()).isEqualTo(expectedResponse.description());
        assertThat(result.status()).isEqualTo(expectedResponse.status());

        verify(todoRepository).findById(todoId);
        verify(todoRepository).save(existingTodo);
        verify(todoMapper).fromTodo(updatedTodo);
    }

    @Test
    void fullyUpdateTodo_shouldThrowException_WhenTodoNotFound() {
        var todoId = 999L;
        var userId = 1L;

        var updateDto = new TodoUpdateRequestDto(
                "Updated title",
                TodoStatus.COMPLETED,
                userId
        );

        when(todoRepository.findById(todoId)).thenReturn(Optional.empty());

        assertThrows(TodoNotFoundException.class, () -> todoService.fullyUpdateTodo(todoId, updateDto));

        verify(todoRepository).findById(todoId);
        verifyNoMoreInteractions(todoRepository, todoMapper);
    }

    @Test
    void partiallyUpdateTodo_shouldUpdateOnlyTitle_WhenProvided() {
        var todoId = 1L;
        var userId = 1L;

        var updateDto = new TodoUpdateRequestDto(
                "Updated title",
                null,
                userId
        );

        var existingTodo = Todo.builder()
                .id(todoId)
                .description("Old description")
                .status(TodoStatus.IN_PROGRESS)
                .owner(User.builder()
                        .id(userId)
                        .build())
                .build();

        var updatedTodo = Todo.builder()
                .id(todoId)
                .description("Updated description")
                .status(TodoStatus.IN_PROGRESS)
                .owner(User.builder()
                        .id(userId)
                        .build())
                .build();

        var expectedResponse = new TodoResponseDto(
                todoId,
                "Updated title",
                TodoStatus.IN_PROGRESS,
                userId,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(todoRepository.findById(todoId)).thenReturn(Optional.of(existingTodo));
        when(todoRepository.save(any(Todo.class))).thenReturn(updatedTodo);
        when(todoMapper.fromTodo(updatedTodo)).thenReturn(expectedResponse);

        var result = todoService.partiallyUpdateTodo(todoId, updateDto);

        assertThat(result)
                .isNotNull()
                .isEqualTo(expectedResponse);
        assertThat(result.description()).isEqualTo(expectedResponse.description());
        assertThat(result.status()).isEqualTo(expectedResponse.status());

        verify(todoRepository).findById(todoId);
        verify(todoRepository).save(existingTodo);
        verify(todoMapper).fromTodo(updatedTodo);
    }

    @Test
    void partiallyUpdateTodo_shouldUpdateOnlyStatus_WhenProvided() {
        var todoId = 1L;
        var userId = 1L;
        var updateDto = new TodoUpdateRequestDto(
                null,
                TodoStatus.COMPLETED,
                userId
        );

        var existingTodo = Todo.builder()
                .id(todoId)
                .description("Existing description")
                .status(TodoStatus.IN_PROGRESS)
                .owner(User.builder()
                        .id(userId)
                        .build())
                .build();

        var updatedTodo = Todo.builder()
                .id(todoId)
                .description("Existing description")
                .status(TodoStatus.COMPLETED)
                .owner(User.builder()
                        .id(userId)
                        .build())
                .build();

        var expectedResponse = new TodoResponseDto(
                todoId,
                "Existing description",
                TodoStatus.COMPLETED,
                userId,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(todoRepository.findById(todoId)).thenReturn(Optional.of(existingTodo));
        when(todoRepository.save(any(Todo.class))).thenReturn(updatedTodo);
        when(todoMapper.fromTodo(updatedTodo)).thenReturn(expectedResponse);

        var result = todoService.partiallyUpdateTodo(todoId, updateDto);

        assertThat(result)
                .isNotNull()
                .isEqualTo(expectedResponse);
        assertThat(result.description()).isEqualTo(expectedResponse.description());
        assertThat(result.status()).isEqualTo(expectedResponse.status());

        verify(todoRepository).findById(todoId);
        verify(todoRepository).save(existingTodo);
        verify(todoMapper).fromTodo(updatedTodo);
    }

    @Test
    void partiallyUpdateTodo_shouldThrowException_WhenTodoNotFound() {
        var todoId = 999L;
        var userId = 1L;
        var updateDto = new TodoUpdateRequestDto(
                null,
                TodoStatus.COMPLETED,
                userId
        );

        when(todoRepository.findById(todoId)).thenReturn(Optional.empty());

        assertThrows(TodoNotFoundException.class, ()-> todoService.partiallyUpdateTodo(todoId, updateDto));

        verify(todoRepository).findById(todoId);
        verifyNoMoreInteractions(todoRepository, todoMapper);
    }

}

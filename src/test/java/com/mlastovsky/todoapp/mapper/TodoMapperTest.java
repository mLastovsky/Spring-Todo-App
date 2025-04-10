package com.mlastovsky.todoapp.mapper;

import com.mlastovsky.todoapp.dto.TodoRequestDto;
import com.mlastovsky.todoapp.model.Todo;
import com.mlastovsky.todoapp.model.TodoStatus;
import com.mlastovsky.todoapp.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class TodoMapperTest {

    @InjectMocks
    private TodoMapper todoMapper;

    @Test
    void toTodo_ShouldMapAllFieldsCorrectly() {
        var owner = User.builder()
                .id(1L)
                .username("testUser")
                .build();

        var requestDto = new TodoRequestDto(
                "Test description",
                1L
        );

        var result = todoMapper.toTodo(requestDto, owner);

        assertThat(result).isNotNull();
        assertThat(result.getDescription()).isEqualTo(requestDto.description());
        assertThat(result.getStatus()).isEqualTo(TodoStatus.IN_PROGRESS);
        assertThat(result.getOwner()).isEqualTo(owner);
        assertThat(result.getId()).isNull();
    }


    @Test
    void fromTodo_ShouldMapAllFieldsCorrectly() {
        var owner = User.builder()
                .id(1L)
                .username("testUser")
                .build();

        var createdAt = LocalDateTime.now();
        var modifiedAt = createdAt.plusHours(1);

        var todo = Todo.builder()
                .id(1L)
                .description("Test description")
                .status(TodoStatus.COMPLETED)
                .owner(owner)
                .createdAt(createdAt)
                .lastModifiedAt(modifiedAt)
                .build();

        var result = todoMapper.fromTodo(todo);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(todo.getId());
        assertThat(result.description()).isEqualTo(todo.getDescription());
        assertThat(result.status()).isEqualTo(todo.getStatus());
        assertThat(result.ownerId()).isEqualTo(owner.getId());
        assertThat(result.createdAt()).isEqualTo(createdAt);
        assertThat(result.lastModifiedAt()).isEqualTo(modifiedAt);
    }

}

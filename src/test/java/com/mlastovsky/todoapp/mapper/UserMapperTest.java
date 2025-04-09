package com.mlastovsky.todoapp.mapper;

import com.mlastovsky.todoapp.dto.UserRequestDto;
import com.mlastovsky.todoapp.model.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    static UserMapper userMapper;

    @BeforeAll
    static void setUp() {
        userMapper = new UserMapper();
    }

    @Test
    void fromUser_ShouldMapAllFieldsCorrectly (){
        var user = User.builder()
                .id(1L)
                .username("testUser")
                .email("test@example.com")
                .password("securePassword")
                .build();

        var result = userMapper.fromUser(user);

        assertNotNull(result);
        assertEquals(user.getId(), result.id());
        assertEquals(user.getUsername(), result.username());
        assertEquals(user.getEmail(), result.email());
    }

    @Test
    void toUser_ShouldMapAllFieldsCorrectly() {
        var dto = new UserRequestDto(
                "newUser",
                "new@example.com",
                "password123"
        );

        var result = userMapper.toUser(dto);

        assertNotNull(result);
        assertEquals(dto.username(), result.getUsername());
        assertEquals(dto.email(), result.getEmail());
        assertEquals(dto.password(), result.getPassword());
        assertNull(result.getId());
    }

    @Test
    void toUser_ShouldHandlePartialNullFields() {
        var dto = new UserRequestDto(
                null,
                "partial@example.com",
                null
        );

        var result = userMapper.toUser(dto);

        assertNotNull(result);
        assertNull(result.getUsername());
        assertEquals("partial@example.com", result.getEmail());
        assertNull(result.getPassword());
    }

}

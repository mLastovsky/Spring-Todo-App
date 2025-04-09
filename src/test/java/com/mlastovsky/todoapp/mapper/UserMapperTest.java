package com.mlastovsky.todoapp.mapper;

import com.mlastovsky.todoapp.dto.UserRequestDto;
import com.mlastovsky.todoapp.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {

    @InjectMocks
    UserMapper userMapper;

    @Test
    void fromUser_ShouldMapAllFieldsCorrectly (){
        var user = User.builder()
                .id(1L)
                .username("testUser")
                .email("test@example.com")
                .password("securePassword")
                .build();

        var result = userMapper.fromUser(user);

        assertThat(result).isNotNull();
        assertThat(user.getEmail()).isEqualTo(result.email());
        assertThat(user.getUsername()).isEqualTo(result.username());
        assertThat(user.getId()).isEqualTo(result.id());
    }

    @Test
    void toUser_ShouldMapAllFieldsCorrectly() {
        var dto = new UserRequestDto(
                "newUser",
                "new@example.com",
                "password123"
        );

        var result = userMapper.toUser(dto);

        assertThat(result).isNotNull();
        assertThat(dto.username()).isEqualTo(result.getUsername());
        assertThat(dto.email()).isEqualTo(result.getEmail());
        assertThat(dto.password()).isEqualTo(result.getPassword());
        assertThat(result.getId()).isNull();
    }

    @Test
    void toUser_ShouldHandlePartialNullFields() {
        var dto = new UserRequestDto(
                null,
                "partial@example.com",
                null
        );

        var result = userMapper.toUser(dto);

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isNull();
        assertThat("partial@example.com").isEqualTo(result.getEmail());
        assertThat(result.getPassword()).isNull();
    }

}

package com.mlastovsky.todoapp.service;

import com.mlastovsky.todoapp.dto.UserRequestDto;
import com.mlastovsky.todoapp.dto.UserResponseDto;
import com.mlastovsky.todoapp.exception.UserAlreadyExistException;
import com.mlastovsky.todoapp.exception.UserNotFoundException;
import com.mlastovsky.todoapp.mapper.UserMapper;
import com.mlastovsky.todoapp.model.User;
import com.mlastovsky.todoapp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void findById_ShouldReturnUserWhenExists() {
        var userId = 1L;
        var user = User.builder()
                .id(userId)
                .username("testUser")
                .email("test@example.com")
                .password("password")
                .build();

        var expectedDto = new UserResponseDto(
                userId,
                "testUser",
                "test@example.com"
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.fromUser(user)).thenReturn(expectedDto);

        var result = userService.findById(userId);

        assertThat(result).isEqualTo(expectedDto);
        verify(userRepository).findById(userId);
        verify(userMapper).fromUser(user);
    }

    @Test
    void findById_ShouldThrowExceptionWhenUserNotFound() {
        var userId = 99L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findById(userId));
        verify(userRepository).findById(userId);
        verifyNoInteractions(userMapper);
    }

    @Test
    void findAllUsers_ShouldReturnAllUsers() {
        var user1 = User.builder()
                .id(1L)
                .username("user1")
                .email("user1@example.com")
                .password("pass1")
                .build();

        var user2 = User.builder()
                .id(2L)
                .username("user2")
                .email("user2@example.com")
                .password("pass2")
                .build();

        var dto1 = new UserResponseDto(
                1L,
                "user1",
                "user1@example.com"
        );

        var dto2 = new UserResponseDto(
                2L,
                "user2",
                "user2@example.com"
        );

        when(userRepository.findAllUsers()).thenReturn(List.of(user1, user2));
        when(userMapper.fromUser(user1)).thenReturn(dto1);
        when(userMapper.fromUser(user2)).thenReturn(dto2);

        var result = userService.findAllUsers();

        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(dto1, dto2);
        verify(userRepository).findAllUsers();
        verify(userMapper, times(2)).fromUser(any());
    }

    @Test
    void findAllUsers_ShouldReturnEmptyListWhenNoUsers() {
        when(userRepository.findAllUsers()).thenReturn(List.of());

        var result = userService.findAllUsers();

        assertThat(result).isEmpty();
        verify(userRepository).findAllUsers();
        verifyNoInteractions(userMapper);
    }

    @Test
    void createUser_ShouldSaveNewUser() {
        var request = new UserRequestDto(
                "newUser",
                "new@example.com",
                "password"
        );

        var user = User.builder()
                .id(null)
                .username("newUser")
                .email("new@example.com")
                .password("password")
                .build();

        var savedUser = User.builder()
                .id(1L)
                .username("newUser")
                .email("new@example.com")
                .password("password")
                .build();

        var expected = new UserResponseDto(
                1L,
                "newUser",
                "new@example.com"
        );

        when(userRepository.existsByUsername("newUser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userMapper.toUser(request)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(savedUser);
        when(userMapper.fromUser(savedUser)).thenReturn(expected);

        UserResponseDto result = userService.createUser(request);

        assertThat(result).isEqualTo(expected);
        verify(userRepository).existsByUsername("newUser");
        verify(userRepository).existsByEmail("new@example.com");
        verify(userMapper).toUser(request);
        verify(userRepository).save(user);
        verify(userMapper).fromUser(savedUser);
    }

    @Test
    void createUser_ShouldThrowExceptionWhenUsernameExists() {
        var request = new UserRequestDto(
                "existing",
                "new@example.com",
                "password"
        );
        when(userRepository.existsByUsername("existing")).thenReturn(true);

        assertThrows(UserAlreadyExistException.class, () -> userService.createUser(request));
        verify(userRepository).existsByUsername("existing");
        verify(userRepository, never()).existsByEmail(any());
        verifyNoInteractions(userMapper);
    }

    @Test
    void createUser_ShouldThrowExceptionWhenEmailExists() {
        var request = new UserRequestDto(
                "newUser",
                "existing@example.com",
                "password"
        );
        when(userRepository.existsByUsername("newUser")).thenReturn(false);
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThrows(UserAlreadyExistException.class, () -> userService.createUser(request));
        verify(userRepository).existsByUsername("newUser");
        verify(userRepository).existsByEmail("existing@example.com");
        verifyNoInteractions(userMapper);
    }

    @Test
    void fullyUpdateUser_ShouldUpdateAllFields() {
        var userId = 1L;
        var request = new UserRequestDto(
                "updated",
                "updated@example.com",
                "newPass"
        );

        var existingUser = User.builder()
                .id(userId)
                .username("old")
                .email("old@example.com")
                .password("oldPass")
                .build();

        var updatedUser = User.builder()
                .id(userId)
                .username("updated")
                .email("updated@example.com")
                .password("newPass")
                .build();

        var expected = new UserResponseDto(
                userId,
                "updated",
                "updated@example.com"
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any())).thenReturn(updatedUser);
        when(userMapper.fromUser(updatedUser)).thenReturn(expected);

        var result = userService.fullyUpdateUser(userId, request);

        assertThat(result).isEqualTo(expected);
        assertThat(updatedUser.getUsername()).isEqualTo(request.username());
        assertThat(updatedUser.getEmail()).isEqualTo(request.email());
        assertThat(updatedUser.getPassword()).isEqualTo(request.password());
    }

    @Test
    void fullyUpdateUser_ShouldThrowExceptionWhenUserNotFound() {
        var userId = 99L;
        var request = new UserRequestDto(
                "test",
                "test@example.com",
                "pass"
        );

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.fullyUpdateUser(userId, request));
        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any());
        verifyNoInteractions(userMapper);
    }

    @Test
    void partiallyUpdateUser_ShouldUpdateOnlyProvidedFields() {
        var userId = 1L;
        var request = new UserRequestDto(
                null,
                "updated@example.com",
                null
        );

        var existingUser = User.builder()
                .id(userId)
                .username("old")
                .email("old@example.com")
                .password("oldPass")
                .build();

        var updatedUser = User.builder()
                .id(userId)
                .username("old")
                .email("updated@example.com")
                .password("oldPass")
                .build();

        var expected = new UserResponseDto(
                userId,
                "old",
                "updated@example.com"
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any())).thenReturn(updatedUser);
        when(userMapper.fromUser(updatedUser)).thenReturn(expected);

        var result = userService.partiallyUpdateUser(userId, request);

        assertThat(result).isEqualTo(expected);
        assertThat(updatedUser.getUsername()).isEqualTo("old");
        assertThat(updatedUser.getEmail()).isEqualTo("updated@example.com");
        assertThat(updatedUser.getPassword()).isEqualTo("oldPass");
    }

    @Test
    void deleteUser_ShouldDeleteWhenUserExists() {
        var userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);

        userService.deleteUser(userId);

        verify(userRepository).existsById(userId);
        verify(userRepository).deleteById(userId);
    }

    @Test
    void deleteUser_ShouldThrowExceptionWhenUserNotFound() {
        var userId = 99L;
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(userId));
        verify(userRepository).existsById(userId);
        verify(userRepository, never()).deleteById(any());
    }

}

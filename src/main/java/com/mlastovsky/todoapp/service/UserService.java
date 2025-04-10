package com.mlastovsky.todoapp.service;

import com.mlastovsky.todoapp.dto.UserRequestDto;
import com.mlastovsky.todoapp.dto.UserResponseDto;
import com.mlastovsky.todoapp.exception.UserAlreadyExistException;
import com.mlastovsky.todoapp.exception.UserNotFoundException;
import com.mlastovsky.todoapp.mapper.UserMapper;
import com.mlastovsky.todoapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.lang.String.format;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserResponseDto findById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::fromUser)
                .orElseThrow(
                        () -> new UserNotFoundException(
                                format("User with ID:: %d not found", id)
                        )
                );
    }

    public List<UserResponseDto> findAllUsers() {
        return userRepository.findAllUsers().stream()
                .map(userMapper::fromUser)
                .toList();
    }

    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        if (userRepository.existsByUsername(userRequestDto.username()) || userRepository.existsByEmail(userRequestDto.email())) {
            throw new UserAlreadyExistException("Username or email address already in use");
        }
        var savedUser = userRepository.save(userMapper.toUser(userRequestDto));
        return userMapper.fromUser(savedUser);
    }

    public UserResponseDto fullyUpdateUser(Long id, UserRequestDto request) {
        var existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(
                        format("User with ID:: %d not found", id)
                ));

        existingUser.setUsername(request.username());
        existingUser.setEmail(request.email());
        existingUser.setPassword(request.password());

        var updatedUser = userRepository.save(existingUser);
        return userMapper.fromUser(updatedUser);
    }

    public UserResponseDto partiallyUpdateUser(Long id, UserRequestDto request) {
        var existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(format("User with ID:: %d not found", id)));

        if (request.username() != null) {
            existingUser.setUsername(request.username());
        }
        if (request.email() != null) {
            existingUser.setEmail(request.email());
        }
        if (request.password() != null) {
            existingUser.setPassword(request.password());
        }

        var updatedUser = userRepository.save(existingUser);
        return userMapper.fromUser(updatedUser);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(
                    format("User with ID:: %d not found", id)
            );
        }
        userRepository.deleteById(id);
    }

}


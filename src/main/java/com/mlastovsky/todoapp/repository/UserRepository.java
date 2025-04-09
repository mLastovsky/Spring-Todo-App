package com.mlastovsky.todoapp.repository;

import com.mlastovsky.todoapp.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
}

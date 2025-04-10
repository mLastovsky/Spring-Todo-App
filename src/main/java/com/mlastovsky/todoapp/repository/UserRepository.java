package com.mlastovsky.todoapp.repository;

import com.mlastovsky.todoapp.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Long> {

    @Query("FROM User u")
    List<User> findAllUsers();

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

}

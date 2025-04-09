package com.mlastovsky.todoapp.repository;

import com.mlastovsky.todoapp.model.Todo;
import org.springframework.data.repository.CrudRepository;

public interface TodoRepository extends CrudRepository<Todo, Long> {
}

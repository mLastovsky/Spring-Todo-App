package com.mlastovsky.todoapp.repository;

import com.mlastovsky.todoapp.model.Todo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TodoRepository extends CrudRepository<Todo, Long> {

    @Query("FROM Todo t WHERE t.owner.id = :userId")
    List<Todo> findAllByUserId(Long userId);

}

package com.isys3001.todo_backend.repositories;

import com.isys3001.todo_backend.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findByOwnerIdOrderByCreatedAtDesc(Long ownerId);
    Optional<Todo> findByIdAndOwnerId(Long id, Long ownerId);
}
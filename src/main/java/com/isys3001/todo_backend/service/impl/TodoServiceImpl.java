package com.isys3001.todo_backend.service.impl;

import com.isys3001.todo_backend.dto.request.CreateTodoRequest;
import com.isys3001.todo_backend.dto.request.UpdateTodoRequest;
import com.isys3001.todo_backend.dto.response.TodoResponse;
import com.isys3001.todo_backend.entity.Todo;
import com.isys3001.todo_backend.entity.User;
import com.isys3001.todo_backend.repositories.TodoRepository;
import com.isys3001.todo_backend.repositories.UserRepository;
import com.isys3001.todo_backend.service.TodoService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepo;
    private final UserRepository userRepo;

    public TodoServiceImpl(TodoRepository todoRepo, UserRepository userRepo) {
        this.todoRepo = todoRepo;
        this.userRepo = userRepo;
    }

    private User currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName(); // email/username used at login
        return userRepo.findByEmail(username) // change to findByUsername(...) if needed
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found: " + username));
    }

    private static TodoResponse map(Todo t) {
        return new TodoResponse(
                t.getId(), t.getTitle(), t.getDescription(),
                t.isCompleted(), t.getCreatedAt(), t.getUpdatedAt()
        );
    }

    @Override
    public TodoResponse create(CreateTodoRequest req) {
        User owner = currentUser();
        Todo t = new Todo();
        t.setTitle(req.title());
        t.setDescription(req.description());
        t.setOwner(owner);
        return map(todoRepo.save(t));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TodoResponse> listMine() {
        User owner = currentUser();
        return todoRepo.findByOwnerIdOrderByCreatedAtDesc(owner.getId())
                .stream().map(TodoServiceImpl::map).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TodoResponse get(Long id) {
        User owner = currentUser();
        Todo t = todoRepo.findByIdAndOwnerId(id, owner.getId())
                .orElseThrow(() -> new IllegalArgumentException("Todo not found"));
        return map(t);
    }

    @Override
    public TodoResponse update(Long id, UpdateTodoRequest req) {
        User owner = currentUser();
        Todo t = todoRepo.findByIdAndOwnerId(id, owner.getId())
                .orElseThrow(() -> new IllegalArgumentException("Todo not found"));

        if (req.title() != null && !req.title().isBlank()) t.setTitle(req.title());
        if (req.description() != null) t.setDescription(req.description());
        if (req.completed() != null) t.setCompleted(req.completed());

        return map(todoRepo.save(t));
    }

    @Override
    public void delete(Long id) {
        User owner = currentUser();
        Todo t = todoRepo.findByIdAndOwnerId(id, owner.getId())
                .orElseThrow(() -> new IllegalArgumentException("Todo not found"));
        todoRepo.delete(t);
    }
}
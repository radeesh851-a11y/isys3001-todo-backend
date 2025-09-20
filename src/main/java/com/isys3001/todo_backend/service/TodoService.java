package com.isys3001.todo_backend.service;


import com.isys3001.todo_backend.dto.request.CreateTodoRequest;
import com.isys3001.todo_backend.dto.request.UpdateTodoRequest;
import com.isys3001.todo_backend.dto.response.TodoResponse;

import java.util.List;

public interface TodoService {
    TodoResponse create(CreateTodoRequest req);
    List<TodoResponse> listMine();
    TodoResponse get(Long id);
    TodoResponse update(Long id, UpdateTodoRequest req);
    void delete(Long id);
}

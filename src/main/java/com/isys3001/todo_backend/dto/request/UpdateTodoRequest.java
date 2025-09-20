package com.isys3001.todo_backend.dto.request;

import jakarta.validation.constraints.Size;

public record UpdateTodoRequest(
        @Size(max = 160) String title,
        @Size(max = 10000) String description,
        Boolean completed
) {}
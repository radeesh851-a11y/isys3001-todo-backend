package com.isys3001.todo_backend.dto.response;

import java.time.Instant;

public record TodoResponse(
        Long id,
        String title,
        String description,
        boolean completed,
        Instant createdAt,
        Instant updatedAt
) {}
package com.isys3001.todo_backend.service;

import com.isys3001.todo_backend.dto.request.LoginRequest;
import com.isys3001.todo_backend.dto.request.RegisterRequest;

public interface AuthenticationService {
    String register(RegisterRequest req);
    String login(LoginRequest req);
}
package com.isys3001.todo_backend.controller;

import com.isys3001.todo_backend.dto.request.LoginRequest;
import com.isys3001.todo_backend.dto.request.RegisterRequest;
import com.isys3001.todo_backend.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public String register(@Valid @RequestBody RegisterRequest req) {
        return authenticationService.register(req);
    }

    @PostMapping("/login")
    public String login(@Valid @RequestBody LoginRequest req) {
        return authenticationService.login(req);
    }
}

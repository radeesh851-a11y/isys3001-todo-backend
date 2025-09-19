package com.isys3001.todo_backend.service.impl;

import com.isys3001.todo_backend.dto.request.LoginRequest;
import com.isys3001.todo_backend.dto.request.RegisterRequest;
import com.isys3001.todo_backend.entity.User;
import com.isys3001.todo_backend.enums.Role;
import com.isys3001.todo_backend.repositories.UserRepository;
import com.isys3001.todo_backend.service.AuthenticationService;
import com.isys3001.todo_backend.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public String register(RegisterRequest req) {
        userRepository.findByEmail(req.getEmail()).ifPresent(u -> {
            throw new RuntimeException("Email already registered");
        });

        User user = User.builder()
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .role(Role.USER)
                .build();

        userRepository.save(user);
        return "User registered successfully";
    }

    @Override
    public String login(LoginRequest req) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));
        return jwtUtil.generateToken(req.getEmail());
    }
}

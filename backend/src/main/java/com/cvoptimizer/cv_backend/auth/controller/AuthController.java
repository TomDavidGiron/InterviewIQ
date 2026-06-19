package com.cvoptimizer.cv_backend.auth.controller;

import com.cvoptimizer.cv_backend.auth.dto.AuthResponse;
import com.cvoptimizer.cv_backend.auth.dto.LoginRequest;
import com.cvoptimizer.cv_backend.auth.dto.RegisterRequest;
import com.cvoptimizer.cv_backend.auth.entity.UserEntity;
import com.cvoptimizer.cv_backend.auth.jwt.JwtTokenProvider;
import com.cvoptimizer.cv_backend.auth.repository.UserRepository;
import com.cvoptimizer.cv_backend.auth.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    public AuthController(UserService userService,
                          JwtTokenProvider jwtTokenProvider,
                          AuthenticationManager authenticationManager,
                          UserRepository userRepository) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        UserEntity user = userService.register(request.getUsername(), request.getPassword());
        String token = jwtTokenProvider.generateToken(user.getId(), user.getUsername());
        return ResponseEntity.ok(new AuthResponse(token, user.getId(), user.getUsername()));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        // auth.getName() = userId (set in UserService.loadUserByUsername)
        String userId = auth.getName();
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));
        String token = jwtTokenProvider.generateToken(userId, user.getUsername());
        return ResponseEntity.ok(new AuthResponse(token, userId, user.getUsername()));
    }
}

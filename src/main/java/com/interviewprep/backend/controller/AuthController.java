package com.interviewprep.backend.controller;

import java.security.Principal;
import java.util.Map;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.interviewprep.backend.dto.ApiResponse;
import com.interviewprep.backend.dto.AuthRequest;
import com.interviewprep.backend.dto.AuthResponse;
import com.interviewprep.backend.dto.RegisterRequest;
import com.interviewprep.backend.dto.UserResponse;
import com.interviewprep.backend.service.AuthService;
import com.interviewprep.backend.util.ApiResponses;

import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public org.springframework.http.ResponseEntity<ApiResponse<AuthResponse>> register(
        @Valid @RequestBody RegisterRequest request
    ) {
        return ApiResponses.created("User registered successfully.", authService.register(request));
    }

    @PostMapping("/login")
    public org.springframework.http.ResponseEntity<ApiResponse<AuthResponse>> login(
        @Valid @RequestBody AuthRequest request
    ) {
        return ApiResponses.ok("Login successful.", authService.login(request));
    }

    @PostMapping("/logout")
    public org.springframework.http.ResponseEntity<ApiResponse<Map<String, String>>> logout() {
        return ApiResponses.ok(
            "Logout successful.",
            Map.of("message", "Logout is handled by deleting the stored JWT on the client.")
        );
    }

    @GetMapping("/me")
    public org.springframework.http.ResponseEntity<ApiResponse<UserResponse>> me(Principal principal) {
        return ApiResponses.ok("Current user loaded successfully.", authService.getCurrentUser(principal.getName()));
    }
}

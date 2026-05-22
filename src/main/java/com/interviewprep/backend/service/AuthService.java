package com.interviewprep.backend.service;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.interviewprep.backend.dto.AuthRequest;
import com.interviewprep.backend.dto.AuthResponse;
import com.interviewprep.backend.dto.RegisterRequest;
import com.interviewprep.backend.dto.UserResponse;
import com.interviewprep.backend.exception.ApiException;
import com.interviewprep.backend.model.User;
import com.interviewprep.backend.repository.UserRepository;
import com.interviewprep.backend.security.JwtService;
import com.interviewprep.backend.util.TextSanitizer;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public AuthService(
        AuthenticationManager authenticationManager,
        JwtService jwtService,
        PasswordEncoder passwordEncoder,
        UserRepository userRepository
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public AuthResponse register(RegisterRequest request) {
        String normalizedEmail = TextSanitizer.sanitizePrompt(request.getEmail(), 150).toLowerCase();
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new ApiException(HttpStatus.CONFLICT, "Email is already registered.");
        }

        User user = new User();
        user.setName(TextSanitizer.sanitizePrompt(request.getName(), 80));
        user.setEmail(normalizedEmail);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());

        User savedUser = userRepository.save(user);
        return buildAuthResponse(savedUser);
    }

    public AuthResponse login(AuthRequest request) {
        String normalizedEmail = TextSanitizer.sanitizePrompt(request.getEmail(), 150).toLowerCase();
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(normalizedEmail, request.getPassword())
        );

        User user = userRepository.findByEmail(authentication.getName())
            .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials."));

        return buildAuthResponse(user);
    }

    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found."));
        return toUserResponse(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        String token = jwtService.generateToken(user.getId(), user.getEmail(), user.getRole());
        return new AuthResponse(token, toUserResponse(user));
    }

    private UserResponse toUserResponse(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }
}

package com.interviewprep.backend.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.interviewprep.backend.dto.AuthRequest;
import com.interviewprep.backend.exception.ApiException;
import com.interviewprep.backend.repository.UserRepository;
import com.interviewprep.backend.security.JwtService;

class AuthServiceTest {

    private AuthenticationManager authenticationManager;
    private JwtService jwtService;
    private PasswordEncoder passwordEncoder;
    private UserRepository userRepository;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        authenticationManager = mock(AuthenticationManager.class);
        jwtService = new JwtService("test-secret-key-for-jwt-1234567890", 3_600_000L);
        passwordEncoder = mock(PasswordEncoder.class);
        userRepository = mock(UserRepository.class);

        authService = new AuthService(authenticationManager, jwtService, passwordEncoder, userRepository);
    }

    @Test
    void loginWhenAuthenticationFailsThrowsUnauthorizedApiException() {
        AuthRequest request = new AuthRequest();
        request.setEmail("user@example.com");
        request.setPassword("wrong-password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("invalid"));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(ApiException.class)
                .hasMessage("Invalid credentials.");
    }
}

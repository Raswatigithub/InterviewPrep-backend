package com.interviewprep.backend.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.interviewprep.backend.dto.ApiResponse;
import com.interviewprep.backend.util.ApiResponses;

@RestController
@RequestMapping({"/api/health", "/health"})
public class HealthController {

    @GetMapping
    public org.springframework.http.ResponseEntity<ApiResponse<Map<String, String>>> health() {
        return ApiResponses.ok("Backend is healthy.", Map.of("status", "ok"));
    }
}

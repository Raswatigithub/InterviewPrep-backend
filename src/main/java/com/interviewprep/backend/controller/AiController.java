package com.interviewprep.backend.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.interviewprep.backend.dto.ApiResponse;
import com.interviewprep.backend.dto.AiGenerateRequest;
import com.interviewprep.backend.dto.AiGenerateResponse;
import com.interviewprep.backend.service.GeminiService;
import com.interviewprep.backend.util.ApiResponses;

import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final GeminiService geminiService;

    public AiController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @PostMapping("/generate")
    public org.springframework.http.ResponseEntity<ApiResponse<AiGenerateResponse>> generate(
        @Valid @RequestBody AiGenerateRequest request
    ) {
        return ApiResponses.ok(
            "AI response generated successfully.",
            new AiGenerateResponse(geminiService.generateContent(request.getPrompt(), request.getSystemPrompt()))
        );
    }
}

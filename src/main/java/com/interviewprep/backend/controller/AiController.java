package com.interviewprep.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.interviewprep.backend.dto.ApiResponse;
import com.interviewprep.backend.dto.AiGenerateRequest;
import com.interviewprep.backend.dto.AiGenerateResponse;
import com.interviewprep.backend.exception.ApiException;
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
    public ResponseEntity<ApiResponse<AiGenerateResponse>> generate(
        @Valid @RequestBody AiGenerateRequest request
    ) {
        try {
            String text = geminiService.generateContent(request.getPrompt(), request.getSystemPrompt());
            return ApiResponses.ok("AI response generated successfully.", new AiGenerateResponse(text));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Failed to generate AI response: " + e.getMessage(),
                "AI_GENERATION_FAILED"
            );
        }
    }
}

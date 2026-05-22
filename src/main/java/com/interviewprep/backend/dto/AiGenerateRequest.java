package com.interviewprep.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AiGenerateRequest {

    @NotBlank(message = "Prompt is required.")
    @Size(max = 12000, message = "Prompt is too long.")
    private String prompt;

    @NotBlank(message = "System prompt is required.")
    @Size(max = 4000, message = "System prompt is too long.")
    private String systemPrompt;

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getSystemPrompt() {
        return systemPrompt;
    }

    public void setSystemPrompt(String systemPrompt) {
        this.systemPrompt = systemPrompt;
    }
}

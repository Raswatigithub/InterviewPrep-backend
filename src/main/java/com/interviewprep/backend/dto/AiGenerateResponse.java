package com.interviewprep.backend.dto;

public class AiGenerateResponse {

    private final String text;

    public AiGenerateResponse(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}

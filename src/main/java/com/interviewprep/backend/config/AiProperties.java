package com.interviewprep.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Component
@Validated
@ConfigurationProperties(prefix = "app.ai")
public class AiProperties {

    private String apiKey;
    private String model = "gemini-2.5-flash";
    @Min(1000)
    private int connectTimeoutMs = 15000;
    @Min(1000)
    private int requestTimeoutMs = 30000;
    @Min(0)
    @Max(5)
    private int maxRetries = 2;
    @Min(100)
    private int maxPromptLength = 12000;
    @Min(100)
    private int maxSystemPromptLength = 4000;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getConnectTimeoutMs() {
        return connectTimeoutMs;
    }

    public void setConnectTimeoutMs(int connectTimeoutMs) {
        this.connectTimeoutMs = connectTimeoutMs;
    }

    public int getRequestTimeoutMs() {
        return requestTimeoutMs;
    }

    public void setRequestTimeoutMs(int requestTimeoutMs) {
        this.requestTimeoutMs = requestTimeoutMs;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public int getMaxPromptLength() {
        return maxPromptLength;
    }

    public void setMaxPromptLength(int maxPromptLength) {
        this.maxPromptLength = maxPromptLength;
    }

    public int getMaxSystemPromptLength() {
        return maxSystemPromptLength;
    }

    public void setMaxSystemPromptLength(int maxSystemPromptLength) {
        this.maxSystemPromptLength = maxSystemPromptLength;
    }
}

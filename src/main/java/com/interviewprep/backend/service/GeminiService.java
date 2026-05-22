package com.interviewprep.backend.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewprep.backend.config.AiProperties;
import com.interviewprep.backend.exception.ApiException;
import com.interviewprep.backend.util.TextSanitizer;

@Service
public class GeminiService {

    private static final Logger logger = LoggerFactory.getLogger(GeminiService.class);
    private static final String GEMINI_ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models";

    private final AiProperties aiProperties;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public GeminiService(AiProperties aiProperties, ObjectMapper objectMapper) {
        this.aiProperties = aiProperties;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofMillis(aiProperties.getConnectTimeoutMs()))
            .build();
    }

    public String generateContent(String prompt, String systemPrompt) {
        if (aiProperties.getApiKey() == null || aiProperties.getApiKey().isBlank()) {
            throw new ApiException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "AI service is not configured for this environment.",
                "AI_NOT_CONFIGURED"
            );
        }

        String sanitizedPrompt = TextSanitizer.sanitizePrompt(prompt, aiProperties.getMaxPromptLength());
        String sanitizedSystemPrompt = TextSanitizer.sanitizePrompt(
            systemPrompt,
            aiProperties.getMaxSystemPromptLength()
        );
        String requestBody = buildRequestBody(sanitizedPrompt, sanitizedSystemPrompt);
        String endpoint = String.format(
            "%s/%s:generateContent?key=%s",
            GEMINI_ENDPOINT,
            aiProperties.getModel(),
            aiProperties.getApiKey()
        );

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(endpoint))
            .timeout(Duration.ofMillis(aiProperties.getRequestTimeoutMs()))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build();

        RuntimeException lastException = null;

        for (int attempt = 0; attempt <= aiProperties.getMaxRetries(); attempt += 1) {
            try {
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                JsonNode json = objectMapper.readTree(response.body());

                if (response.statusCode() < 200 || response.statusCode() >= 300) {
                    String providerMessage = json.path("error").path("message").asText("Gemini request failed.");
                    logger.warn(
                        "gemini_request_failed status={} attempt={} providerMessage={}",
                        response.statusCode(),
                        attempt + 1,
                        providerMessage
                    );

                    if (response.statusCode() >= 500 && attempt < aiProperties.getMaxRetries()) {
                        continue;
                    }

                    throw new ApiException(
                        HttpStatus.BAD_GATEWAY,
                        "AI provider is temporarily unavailable. Please try again shortly.",
                        "AI_PROVIDER_ERROR"
                    );
                }

                logTokenUsage(json);

                String text = json.path("candidates")
                    .path(0)
                    .path("content")
                    .path("parts")
                    .path(0)
                    .path("text")
                    .asText("")
                    .trim();

                if (text.isBlank()) {
                    throw new ApiException(
                        HttpStatus.BAD_GATEWAY,
                        "AI provider returned an empty response.",
                        "AI_EMPTY_RESPONSE"
                    );
                }

                return text;
            } catch (HttpTimeoutException exception) {
                logger.warn("gemini_request_timeout attempt={}", attempt + 1);
                lastException = new ApiException(
                    HttpStatus.GATEWAY_TIMEOUT,
                    "AI request timed out. Please try again.",
                    "AI_TIMEOUT"
                );
            } catch (IOException exception) {
                logger.warn("gemini_request_io_error attempt={} message={}", attempt + 1, exception.getMessage());
                lastException = new ApiException(
                    HttpStatus.BAD_GATEWAY,
                    "Unable to process AI response right now.",
                    "AI_RESPONSE_ERROR"
                );
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                throw new ApiException(
                    HttpStatus.GATEWAY_TIMEOUT,
                    "AI request was interrupted.",
                    "AI_INTERRUPTED"
                );
            }
        }

        throw lastException != null
            ? lastException
            : new ApiException(HttpStatus.BAD_GATEWAY, "AI service is unavailable.", "AI_UNAVAILABLE");
    }

    private String buildRequestBody(String prompt, String systemPrompt) {
        try {
            JsonNode root = objectMapper.createObjectNode()
                .set(
                    "contents",
                    objectMapper.createArrayNode().add(
                        objectMapper.createObjectNode().set(
                            "parts",
                            objectMapper.createArrayNode().add(
                                objectMapper.createObjectNode().put("text", prompt)
                            )
                        )
                    )
                );

            ((com.fasterxml.jackson.databind.node.ObjectNode) root).set(
                "systemInstruction",
                objectMapper.createObjectNode().set(
                    "parts",
                    objectMapper.createArrayNode().add(
                        objectMapper.createObjectNode().put("text", systemPrompt)
                    )
                )
            );

            return objectMapper.writeValueAsString(root);
        } catch (IOException exception) {
            throw new ApiException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Unable to prepare AI request.",
                "AI_REQUEST_BUILD_ERROR"
            );
        }
    }

    private void logTokenUsage(JsonNode json) {
        JsonNode usage = json.path("usageMetadata");

        if (usage.isMissingNode()) {
            return;
        }

        logger.info(
            "gemini_usage promptTokens={} candidatesTokens={} totalTokens={}",
            usage.path("promptTokenCount").asInt(-1),
            usage.path("candidatesTokenCount").asInt(-1),
            usage.path("totalTokenCount").asInt(-1)
        );
    }
}

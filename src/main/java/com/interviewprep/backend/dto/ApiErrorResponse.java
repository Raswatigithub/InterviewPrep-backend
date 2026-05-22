package com.interviewprep.backend.dto;

import java.time.Instant;
import java.util.Map;

public class ApiErrorResponse {

    private final boolean success;
    private final String code;
    private final String error;
    private final String message;
    private final int status;
    private final String path;
    private final String requestId;
    private final Instant timestamp;
    private final Map<String, String> errors;

    public ApiErrorResponse(
        boolean success,
        String code,
        String error,
        String message,
        int status,
        String path,
        String requestId,
        Instant timestamp,
        Map<String, String> errors
    ) {
        this.success = success;
        this.code = code;
        this.error = error;
        this.message = message;
        this.status = status;
        this.path = path;
        this.requestId = requestId;
        this.timestamp = timestamp;
        this.errors = errors;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getCode() {
        return code;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public String getPath() {
        return path;
    }

    public String getRequestId() {
        return requestId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}

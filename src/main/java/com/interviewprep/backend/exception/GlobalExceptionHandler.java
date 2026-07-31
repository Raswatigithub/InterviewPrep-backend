package com.interviewprep.backend.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.interviewprep.backend.dto.ApiErrorResponse;
import com.interviewprep.backend.filter.RequestLoggingFilter;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiErrorResponse> handleApiException(ApiException exception, HttpServletRequest request) {
        return ResponseEntity.status(exception.getStatus())
            .body(errorBody(exception.getStatus(), exception.getMessage(), exception.getCode(), null, request));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
        MethodArgumentNotValidException exception,
        HttpServletRequest request
    ) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return ResponseEntity.badRequest()
            .body(errorBody(HttpStatus.BAD_REQUEST, "Validation failed.", "VALIDATION_ERROR", fieldErrors, request));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthenticationException(
        AuthenticationException exception, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(errorBody(HttpStatus.UNAUTHORIZED, "Invalid credentials.", "UNAUTHORIZED", null, request));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(Exception exception, HttpServletRequest request) {
        logger.error("Unhandled exception on {} {}", request.getMethod(), request.getRequestURI(), exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(errorBody(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Unexpected server error.",
                "INTERNAL_SERVER_ERROR",
                null,
                request
            ));
    }

    private ApiErrorResponse errorBody(
        HttpStatus status,
        String message,
        String code,
        Map<String, String> errors,
        HttpServletRequest request
    ) {
        return new ApiErrorResponse(
            false,
            code,
            status.getReasonPhrase(),
            message,
            status.value(),
            request.getRequestURI(),
            (String) request.getAttribute(RequestLoggingFilter.REQUEST_ID_ATTRIBUTE),
            Instant.now(),
            errors
        );
    }
}

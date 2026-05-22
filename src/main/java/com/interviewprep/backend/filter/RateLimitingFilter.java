package com.interviewprep.backend.filter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewprep.backend.config.RateLimitProperties;
import com.interviewprep.backend.dto.ApiErrorResponse;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final long WINDOW_MS = 60_000L;

    private final RateLimitProperties rateLimitProperties;
    private final ObjectMapper objectMapper;
    private final Map<String, WindowCounter> counters = new ConcurrentHashMap<>();

    public RateLimitingFilter(RateLimitProperties rateLimitProperties, ObjectMapper objectMapper) {
        this.rateLimitProperties = rateLimitProperties;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        String path = request.getRequestURI();
        int limit = resolveLimit(path);

        if (limit <= 0) {
          filterChain.doFilter(request, response);
          return;
        }

        String clientKey = request.getRemoteAddr() + ":" + resolveBucket(path);
        long now = System.currentTimeMillis();
        WindowCounter counter = counters.compute(clientKey, (_key, existing) -> refreshCounter(existing, now));
        int currentCount = counter.count.incrementAndGet();

        if (currentCount > limit) {
            ApiErrorResponse body = new ApiErrorResponse(
                false,
                "RATE_LIMIT_EXCEEDED",
                HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase(),
                "Too many requests. Please slow down and try again shortly.",
                HttpStatus.TOO_MANY_REQUESTS.value(),
                request.getRequestURI(),
                (String) request.getAttribute(RequestLoggingFilter.REQUEST_ID_ATTRIBUTE),
                Instant.now(),
                null
            );

            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            objectMapper.writeValue(response.getWriter(), body);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String resolveBucket(String path) {
        if (path.startsWith("/api/ai")) {
            return "ai";
        }

        if (path.startsWith("/api/auth")) {
            return "auth";
        }

        return "general";
    }

    private int resolveLimit(String path) {
        if (path.startsWith("/api/ai")) {
            return rateLimitProperties.getAiRequestsPerMinute();
        }

        if (path.startsWith("/api/auth")) {
            return rateLimitProperties.getAuthRequestsPerMinute();
        }

        if (path.startsWith("/api/")) {
            return rateLimitProperties.getGeneralRequestsPerMinute();
        }

        return 0;
    }

    private WindowCounter refreshCounter(WindowCounter existing, long now) {
        if (existing == null || now - existing.windowStartedAt > WINDOW_MS) {
            return new WindowCounter(now);
        }

        return existing;
    }

    private static final class WindowCounter {
        private final long windowStartedAt;
        private final AtomicInteger count = new AtomicInteger(0);

        private WindowCounter(long windowStartedAt) {
            this.windowStartedAt = windowStartedAt;
        }
    }
}

package com.interviewprep.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.rate-limit")
public class RateLimitProperties {

    private int authRequestsPerMinute = 20;
    private int aiRequestsPerMinute = 12;
    private int generalRequestsPerMinute = 120;

    public int getAuthRequestsPerMinute() {
        return authRequestsPerMinute;
    }

    public void setAuthRequestsPerMinute(int authRequestsPerMinute) {
        this.authRequestsPerMinute = authRequestsPerMinute;
    }

    public int getAiRequestsPerMinute() {
        return aiRequestsPerMinute;
    }

    public void setAiRequestsPerMinute(int aiRequestsPerMinute) {
        this.aiRequestsPerMinute = aiRequestsPerMinute;
    }

    public int getGeneralRequestsPerMinute() {
        return generalRequestsPerMinute;
    }

    public void setGeneralRequestsPerMinute(int generalRequestsPerMinute) {
        this.generalRequestsPerMinute = generalRequestsPerMinute;
    }
}

package com.interviewprep.backend.util;

public final class TextSanitizer {

    private TextSanitizer() {
    }

    public static String sanitizePrompt(String value, int maxLength) {
        if (value == null) {
            return "";
        }

        String sanitized = value
            .replaceAll("\\p{Cntrl}&&[^\r\n\t]", " ")
            .trim();

        if (sanitized.length() > maxLength) {
            return sanitized.substring(0, maxLength);
        }

        return sanitized;
    }
}

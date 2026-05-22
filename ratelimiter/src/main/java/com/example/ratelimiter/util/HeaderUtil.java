package com.example.ratelimiter.util;

import com.example.ratelimiter.model.RateLimitDecision;
import jakarta.servlet.http.HttpServletResponse;

public class HeaderUtil {

    private HeaderUtil() {
    }

    public static void addRateLimitHeaders(
            HttpServletResponse response,
            RateLimitDecision decision
    ) {

        response.setHeader(
                "X-RateLimit-Limit",
                String.valueOf(decision.getLimit())
        );

        response.setHeader(
                "X-RateLimit-Remaining",
                String.valueOf(decision.getRemaining())
        );

        response.setHeader(
                "X-RateLimit-Reset",
                String.valueOf(decision.getResetAfterSeconds())
        );
    }
}
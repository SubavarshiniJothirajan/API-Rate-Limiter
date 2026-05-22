package com.example.ratelimiter.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimiterMetrics {

    private final Counter totalRequests;
    private final Counter allowedRequests;
    private final Counter blockedRequests;

    private final AtomicInteger currentTokens =
            new AtomicInteger(0);

    public RateLimiterMetrics(MeterRegistry registry) {

        totalRequests = registry.counter(
                "rate_limiter_requests_total");

        allowedRequests = registry.counter(
                "rate_limiter_requests_allowed");

        blockedRequests = registry.counter(
                "rate_limiter_requests_blocked");

        Gauge.builder(
                "rate_limiter_tokens_remaining",
                currentTokens,
                AtomicInteger::get
        ).register(registry);
    }

    public void incrementTotal() {
        totalRequests.increment();
    }

    public void incrementAllowed() {
        allowedRequests.increment();
    }

    public void incrementBlocked() {
        blockedRequests.increment();
    }

    public void updateTokens(int tokens) {
        currentTokens.set(tokens);
    }
}
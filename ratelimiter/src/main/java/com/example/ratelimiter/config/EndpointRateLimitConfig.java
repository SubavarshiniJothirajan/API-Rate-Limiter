package com.example.ratelimiter.config;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class EndpointRateLimitConfig {

    private final Map<String, Integer> endpointLimits = Map.of(
            "/login", 5,
            "/search", 100,
            "/test", 20
    );

    public int getLimit(String path) {
        return endpointLimits.getOrDefault(path, 10);
    }
}
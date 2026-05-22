package com.example.ratelimiter.algorithm;

import com.example.ratelimiter.model.RateLimitDecision;

public interface RateLimitAlgorithm {

    RateLimitDecision allowRequest(String identifier, String endpoint, int limit);
}
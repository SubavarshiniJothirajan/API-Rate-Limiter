package com.example.ratelimiter.service;

import com.example.ratelimiter.model.RateLimitDecision;

public interface RateLimitService {

    RateLimitDecision allowRequest(String clientId,
                                   String endpoint,
                                   int limit);
}
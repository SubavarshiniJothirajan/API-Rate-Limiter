package com.example.ratelimiter.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RateLimitDecision {

    private boolean allowed;

    private long limit;

    private long remaining;

    private long resetAfterSeconds;
}
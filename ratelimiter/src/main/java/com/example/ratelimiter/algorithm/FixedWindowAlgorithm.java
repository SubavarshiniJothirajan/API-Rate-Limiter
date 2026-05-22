package com.example.ratelimiter.algorithm;

import com.example.ratelimiter.model.RateLimitDecision;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FixedWindowAlgorithm implements RateLimitAlgorithm {

    private final StringRedisTemplate redisTemplate;

    private final DefaultRedisScript<List> rateLimiterScript;

    @Value("${rate.limit.windowSeconds}")
    private long windowSeconds;

    @Override
    public RateLimitDecision allowRequest(String identifier,
                                          String endpoint,
                                          int limit) {

        // Separate bucket per client + endpoint
        String redisKey =
                "fixed_window:" + identifier + ":" + endpoint;

        List result = redisTemplate.execute(
                rateLimiterScript,
                Collections.singletonList(redisKey),
                String.valueOf(limit),
                String.valueOf(windowSeconds)
        );

        if (result == null || result.size() < 3) {
            throw new RuntimeException("Redis Lua script execution failed");
        }

        boolean allowed =
                Long.parseLong(result.get(0).toString()) == 1;

        long currentCount =
                Long.parseLong(result.get(1).toString());

        long ttl =
                Long.parseLong(result.get(2).toString());

        return RateLimitDecision.builder()
                .allowed(allowed)
                .limit(limit)
                .remaining(Math.max(0, limit - currentCount))
                .resetAfterSeconds(ttl)
                .build();
    }
}
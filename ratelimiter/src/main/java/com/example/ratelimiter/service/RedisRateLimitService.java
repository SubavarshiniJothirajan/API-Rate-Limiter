package com.example.ratelimiter.service;

import com.example.ratelimiter.model.RateLimitDecision;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RedisRateLimitService implements RateLimitService {

    private final StringRedisTemplate redisTemplate;

    private final DefaultRedisScript<List> rateLimiterScript;

    @Value("${rate.limit.windowSeconds}")
    private long windowSeconds;

    @Override
    public RateLimitDecision allowRequest(String clientId,
                                          String endpoint,
                                          int limit) {

        // Fixed window bucket
        long bucket =
                Instant.now().getEpochSecond() / windowSeconds;

        // One client + one endpoint = one bucket
        String redisKey =
                "rl:" + clientId + ":" + endpoint + ":" + bucket;

        List result = redisTemplate.execute(
                rateLimiterScript,
                Collections.singletonList(redisKey),
                String.valueOf(limit),           // max requests for this endpoint
                String.valueOf(windowSeconds)    // TTL
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
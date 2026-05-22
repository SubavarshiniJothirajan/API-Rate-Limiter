package com.example.ratelimiter.algorithm;

import com.example.ratelimiter.model.RateLimitDecision;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class SlidingWindowAlgorithm implements RateLimitAlgorithm {

    private final StringRedisTemplate redisTemplate;

    @Value("${rate.limit.maxRequests}")
    private long maxRequests;

    @Value("${rate.limit.windowSeconds}")
    private long windowSeconds;

    @Override
    public RateLimitDecision allowRequest(String identifier,
                                String endpoint,
                                int limit) {

        String key = "sliding:" + identifier;

        long now = Instant.now().getEpochSecond();

        long windowStart = now - windowSeconds;

        // 1. Remove old timestamps
        redisTemplate.opsForZSet()
                .removeRangeByScore(key, 0, windowStart);

        // 2. Count current requests
        Long currentCount =
                redisTemplate.opsForZSet().zCard(key);

        if (currentCount == null) {
            currentCount = 0L;
        }

        boolean allowed = currentCount < maxRequests;

        if (allowed) {

            // 3. Add current request
            redisTemplate.opsForZSet()
                    .add(
                            key,
                            UUID.randomUUID().toString(),
                            now
                    );

            // 4. Expire key
            redisTemplate.expire(
                    key,
                    windowSeconds,
                    TimeUnit.SECONDS
            );

            currentCount++;
        }

        return RateLimitDecision.builder()
                .allowed(allowed)
                .limit(maxRequests)
                .remaining(
                        Math.max(0,
                                maxRequests - currentCount)
                )
                .resetAfterSeconds(windowSeconds)
                .build();
    }
}
package com.example.ratelimiter.algorithm;

import com.example.ratelimiter.metrics.RateLimiterMetrics;
import com.example.ratelimiter.model.RateLimitDecision;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Primary
@Component
@RequiredArgsConstructor
public class TokenBucketAlgorithm
        implements RateLimitAlgorithm {

    private final StringRedisTemplate redisTemplate;

    private final DefaultRedisScript<List> tokenBucketScript;

    private final RateLimiterMetrics metrics;

    private static final long WINDOW_SECONDS = 60;

    @Override
    public RateLimitDecision allowRequest(
            String identifier,
            String endpoint,
            int limit) {

        String bucketKey =
                "bucket:" + identifier + ":" + endpoint;

        long now =
                Instant.now().getEpochSecond();

        double refillRate =
                (double) limit / WINDOW_SECONDS;

        List result =
                redisTemplate.execute(
                        tokenBucketScript,
                        Collections.singletonList(bucketKey),
                        String.valueOf(limit),
                        String.valueOf(refillRate),
                        String.valueOf(now),
                        String.valueOf(WINDOW_SECONDS)
                );

        Long allowed =
                ((Number) result.get(0)).longValue();

        Long remaining =
                ((Number) result.get(1)).longValue();

        Long retryAfter =
                ((Number) result.get(2)).longValue();
        System.out.println(
                "Client IP = " + identifier
        );
        // Metrics
        metrics.incrementTotal();

        if (allowed == 1) {

            metrics.incrementAllowed();

            metrics.updateTokens(
                    remaining.intValue()
            );

        } else {

            metrics.incrementBlocked();
        }

        return RateLimitDecision.builder()
                .allowed(allowed == 1)
                .limit(limit)
                .remaining(remaining)
                .resetAfterSeconds(retryAfter)
                .build();
    }
}
package com.example.ratelimiter.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisDebugService {

    private final StringRedisTemplate redisTemplate;

    public Map<String, Object> inspectIp(
            String ip) {

        String key =
                "bucket:" + ip + ":/search";

        Map<Object, Object> redisData =
                redisTemplate.opsForHash()
                        .entries(key);

        Long ttl =
                redisTemplate.getExpire(
                        key,
                        TimeUnit.SECONDS);

        Map<String, Object> result =
                new HashMap<>();

        result.put("key", key);
        result.put("ttlSeconds", ttl);
        result.put("state", redisData);

        return result;
    }
}
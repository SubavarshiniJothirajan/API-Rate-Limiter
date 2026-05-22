package com.example.ratelimiter.controller;

import com.example.ratelimiter.service.RedisDebugService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/debug")
@RequiredArgsConstructor
public class DebugController {

    private final RedisDebugService redisDebugService;

    @GetMapping("/{ip}")
    public Map<String, Object> inspect(
            @PathVariable String ip) {

        return redisDebugService.inspectIp(ip);
    }
}
package com.example.ratelimiter.identity;

import com.example.ratelimiter.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClientIdentifier {

    private final JwtUtil jwtUtil;

    public String resolve(HttpServletRequest request) {

        String authHeader =
                request.getHeader("Authorization");

        // JWT present
        if (authHeader != null &&
                authHeader.startsWith("Bearer ")) {

            String token =
                    authHeader.substring(7);

            String userId =
                    jwtUtil.extractUserId(token);

            return "user:" + userId;
        }

        // Fallback → IP
        return "ip:" + request.getRemoteAddr();
    }
}
package com.example.ratelimiter.filter;

import com.example.ratelimiter.algorithm.RateLimitAlgorithm;
import com.example.ratelimiter.config.EndpointRateLimitConfig;
import com.example.ratelimiter.identity.ClientIdentifier;
import com.example.ratelimiter.model.RateLimitDecision;
import com.example.ratelimiter.util.HeaderUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final ClientIdentifier clientIdentifier;

    @Qualifier("tokenBucketAlgorithm")
    private final RateLimitAlgorithm algorithm;

    private final EndpointRateLimitConfig endpointConfig;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Step 1: Identify client (IP/API key later)
        String clientId =
                clientIdentifier.resolve(request);

        // Step 2: Identify endpoint
        String endpoint =
                request.getRequestURI();

        // Step 3: Get endpoint-specific limit
        int limit =
                endpointConfig.getLimit(endpoint);

        // Step 4: Run selected algorithm
        RateLimitDecision decision =
                algorithm.allowRequest(
                        clientId,
                        endpoint,
                        limit
                );

        // Step 5: Add rate-limit headers
        HeaderUtil.addRateLimitHeaders(
                response,
                decision
        );

        // Step 6: Block if exceeded
        if (!decision.isAllowed()) {

            response.setStatus(429);
            response.setContentType("application/json");

            response.getWriter().write(
                    """
                    {
                      "status": 429,
                      "error": "Too Many Requests",
                      "message": "Rate limit exceeded"
                    }
                    """
            );

            return;
        }

        // Step 7: Continue request
        filterChain.doFilter(
                request,
                response
        );
    }
}
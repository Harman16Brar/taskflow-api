package com.taskflow_api.shared.filter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Order(1)
@Slf4j
public class RateLimitFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    private Bucket createAuthenticatedBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(100)
                        .refillGreedy(100, Duration.ofMinutes(1))
                        .build())
                .build();
    }

    private Bucket createUnauthenticatedBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(20)
                        .refillGreedy(20, Duration.ofMinutes(1))
                        .build())
                .build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String key = resolveKey(request);
        boolean isAuthenticated = request.getHeader("Authorization") != null;

        Bucket bucket = buckets.computeIfAbsent(key, k ->
                isAuthenticated ? createAuthenticatedBucket() : createUnauthenticatedBucket()
        );

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            response.addHeader("X-Rate-Limit-Remaining",
                    String.valueOf(probe.getRemainingTokens()));
            filterChain.doFilter(request, response);
        } else {
            long waitSeconds = (probe.getNanosToWaitForRefill() / 1_000_000_000) + 1;
            response.setStatus(429);
            response.setContentType("application/json");
            response.addHeader("X-Rate-Limit-Retry-After-Seconds", String.valueOf(waitSeconds));
            response.getWriter().write("""
                    {"errorCode":"TOO_MANY_REQUESTS","message":"Rate limit exceeded. Try again in %d seconds.","status":429}
                    """.formatted(waitSeconds));
        }

    }

    private String resolveKey(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7); //using token as key.
        }
        return getClientIP(request);//fall back to IP
    }

    private String getClientIP(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isEmpty()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}

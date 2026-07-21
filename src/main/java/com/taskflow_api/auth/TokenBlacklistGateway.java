package com.taskflow_api.auth;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenBlacklistGateway {
    private final RedisTemplate<String, String> redisTemplate;

    @CircuitBreaker(name = "redis", fallbackMethod = "blacklistFallback")
    @Retry(name = "redis")
    public void blacklist(String tokenHash, Duration ttl) {
        redisTemplate.opsForValue().set(tokenHash, "revoked", ttl);
    }

    @CircuitBreaker(name = "redis", fallbackMethod = "isBlacklistedFallback")
    @Retry(name = "redis")
    public boolean isBlacklisted(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    private void blacklistFallback(String key, Duration ttl, Exception ex) {
        log.error("Redis unavailable — could not blacklist token (key={}): {}", key, ex.getMessage());
    }

    private boolean isBlacklistedFallback(String key, Exception ex) {
        // Fail-open: DB delete in rotate()/revoke() is the primary revocation check;
        // this only covers the narrow replay window during an in-flight rotation.
        log.error("Redis unavailable — treating token as NOT blacklisted (fail-open), key={}: {}", key, ex.getMessage());
        return false;
    }
}

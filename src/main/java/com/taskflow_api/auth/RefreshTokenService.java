package com.taskflow_api.auth;


import com.taskflow_api.shared.exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenBlacklistGateway tokenBlacklistGateway;
    private static final String BLACKLIST_PREFIX = "blacklist:";

    @Value("${app.jwt.refresh-expiration-days}")
    private int refreshExpirationDays;

    // ── Generate raw token + hash + save to DB ──────────────
    public String createRefreshToken(UUID userId) {
        String rawToken = UUID.randomUUID().toString();
        String tokenHash = hashToken(rawToken);

        RefreshToken refreshToken = RefreshToken.create(
                tokenHash,
                userId,
                LocalDateTime.now().plusDays(refreshExpirationDays)
        );
        refreshTokenRepository.save(refreshToken);
        return rawToken; // return raw — client stores this
    }

    // ── Validate incoming refresh token ───────────────────────────────────────
    public RefreshToken validate(String rawToken) {
        String tokenHash = hashToken(rawToken);

        // Check Redis blacklist first
        if (isBlacklisted(tokenHash)) {
            throw new AppException(
                    "Refresh token has been revoked",
                    HttpStatus.UNAUTHORIZED,
                    "REFRESH_TOKEN_BLACKLISTED");
        }

        // Check DB
        RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new AppException(
                        "Refresh token not found",
                        HttpStatus.UNAUTHORIZED,
                        "REFRESH_TOKEN_NOT_FOUND"));

        // Check expiry
        if (refreshToken.isExpired(refreshToken.getExpiresAt())) {
            refreshTokenRepository.deleteByTokenHash(tokenHash);
            throw new AppException(
                    "Refresh token has expired",
                    HttpStatus.UNAUTHORIZED,
                    "REFRESH_TOKEN_EXPIRED"
            );
        }
        return refreshToken;
    }
// ── Rotate — blacklist old, create new ────────────────────────────────────

    public String rotate(String oldRawToken, UUID userId) {
        String oldHash = hashToken(oldRawToken);

        // Blacklist old token in Redis
        blacklist(oldHash, refreshExpirationDays);

        // Delete old from DB
        refreshTokenRepository.deleteByTokenHash(oldHash);

        // Create new token
        return createRefreshToken(userId);
    }

    // ── Logout — blacklist + delete ───────────────────────────────────────────

    public void revoke(String rawToken) {
        String tokenHash = hashToken(rawToken);
        blacklist(tokenHash, refreshExpirationDays);
        refreshTokenRepository.deleteByTokenHash(tokenHash);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    public String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    private void blacklist(String tokenHash, int days) {
        tokenBlacklistGateway.blacklist(BLACKLIST_PREFIX + tokenHash, Duration.ofDays(days));
    }

    private boolean isBlacklisted(String tokenHash) {
        return tokenBlacklistGateway.isBlacklisted(BLACKLIST_PREFIX + tokenHash);
    }
//    private void blacklist(String tokenHash, int days) {
//        redisTemplate.opsForValue().set(
//                BLACKLIST_PREFIX + tokenHash,
//                "revoked",
//                Duration.ofDays(days)
//        );
//    }
//
//    private boolean isBlacklisted(String tokenHash) {
//        return redisTemplate.hasKey(BLACKLIST_PREFIX + tokenHash);
//    }
}



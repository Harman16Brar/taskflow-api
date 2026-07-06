package com.taskflow_api.auth;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "refresh_tokens")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "token_hash", nullable = false, unique = true)
    private String tokenHash;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "created_at", updatable = false)  //  never changes
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "expires_at", nullable = false)    // might need updating
    private LocalDateTime expiresAt;

    public static RefreshToken create(String tokenHash, UUID userId, LocalDateTime expires_at) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setTokenHash(tokenHash);
        refreshToken.setUserId(userId);
        refreshToken.setExpiresAt(expires_at);
        return refreshToken;
    }

    public boolean isExpired(LocalDateTime expires_at) {
        return LocalDateTime.now().isAfter(expires_at);
    }
}

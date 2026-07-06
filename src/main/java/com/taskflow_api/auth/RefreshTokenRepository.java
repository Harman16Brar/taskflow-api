package com.taskflow_api.auth;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    List<RefreshToken> findAllByUserId(UUID userId);

    @Modifying
    @Transactional
    void deleteByTokenHash(String tokenHash);


    @Modifying
    @Transactional
    void deleteAllByUserId(UUID userId);
}

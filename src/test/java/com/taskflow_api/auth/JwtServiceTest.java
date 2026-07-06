package com.taskflow_api.auth;

import com.taskflow_api.auth.service.JwtService;
import com.taskflow_api.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.jsonwebtoken.ExpiredJwtException;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private User user;

    @BeforeEach
    void setUp() {
        // secret must be ≥ 32 bytes for HMAC-SHA256
        jwtService = new JwtService("test-secret-key-that-is-long-enough-for-hmacsha256", 3_600_000L);

        user = new User();
        user.setEmail("harman@test.com");
        user.setFirstName("Harman");
        user.setLastName("Brar");
    }

    @Test
    void generateToken_shouldReturnNonNullToken() {
        String token = jwtService.generateToken(user);
        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void extractEmail_shouldReturnCorrectEmail() {
        String token = jwtService.generateToken(user);
        String extracted = jwtService.extractEmail(token);
        assertEquals("harman@test.com", extracted);
    }

    @Test
    void isTokenValid_withValidToken_shouldReturnTrue() {
        String token = jwtService.generateToken(user);
        assertTrue(jwtService.isTokenValid(token, "harman@test.com"));
    }

    @Test
    void isTokenValid_withWrongEmail_shouldReturnFalse() {
        String token = jwtService.generateToken(user);
        assertFalse(jwtService.isTokenValid(token, "other@test.com"));
    }

    @Test
    void isTokenValid_withExpiredToken_shouldThrowExpiredJwtException() throws InterruptedException {
        // JwtService does not swallow ExpiredJwtException — callers receive the raw exception
        JwtService shortLived = new JwtService("test-secret-key-that-is-long-enough-for-hmacsha256", 1L);
        String token = shortLived.generateToken(user);
        Thread.sleep(10);
        assertThrows(ExpiredJwtException.class,
                () -> shortLived.isTokenValid(token, "harman@test.com"));
    }
}

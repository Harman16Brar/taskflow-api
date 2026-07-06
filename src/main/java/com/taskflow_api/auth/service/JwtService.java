package com.taskflow_api.auth.service;

import com.taskflow_api.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    private final String secret;
    private final long expiration;
    private final SecretKey key;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration}") long expiration) {
        this.secret = secret;
        this.expiration = expiration;
        this.key = Keys.hmacShaKeyFor(secret.getBytes()); // build signing key
    }

    // 🔑 Generate JWT for a user
    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(user.getEmail()) // email as subject
                .claim("userId", user.getId()) // custom claim
                .claim("firstName", user.getFirstName())
                .claim("lastName", user.getLastName())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key)
                .compact();
    }

    // 📧 Extract email from token
    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    // 📧 Extract userId from token
    public String extractUserId(String token) {
        return extractAllClaims(token).get("userId", String.class);
    }

    // ✅ Validate token against email
    public boolean isTokenValid(String token, String email) {
        String extractedEmail = extractEmail(token);
        return extractedEmail.equals(email) && !isTokenExpired(token);
    }

    // 🧩 Helpers
    private boolean isTokenExpired(String token) {
        Date expirationDate = extractAllClaims(token).getExpiration();
        return expirationDate.before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

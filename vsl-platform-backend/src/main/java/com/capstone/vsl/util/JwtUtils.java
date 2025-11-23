package com.capstone.vsl.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT Utility Component
 * Handles JWT token generation, validation, and extraction of claims.
 */
@Component
@Slf4j
public class JwtUtils {

    @Value("${jwt.secret:vsl-platform-secret-key-change-this-in-production-minimum-256-bits}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}") // 24 hours in milliseconds
    private long jwtExpirationMs;

    /**
     * Generates a JWT access token for the given username.
     * Token is valid for 24 hours by default.
     *
     * @param username The username to include in the token
     * @return JWT token string
     */
    public String generateToken(String username) {
        var now = new Date();
        var expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .claim("sub", username)
                .claim("iat", now.getTime() / 1000)
                .claim("exp", expiryDate.getTime() / 1000)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extracts the username from a JWT token.
     *
     * @param token JWT token string
     * @return Username extracted from token
     */
    public String getUsernameFromToken(String token) {
        var claims = extractAllClaims(token);
        return claims.get("sub", String.class);
    }

    /**
     * Validates if a JWT token is valid (not expired and signature is correct).
     *
     * @param token JWT token string
     * @return true if token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            var claims = extractAllClaims(token);
            var exp = claims.get("exp", Long.class);
            if (exp == null) {
                return false;
            }
            var expiryDate = new Date(exp * 1000);
            return !expiryDate.before(new Date());
        } catch (Exception e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Extracts all claims from a JWT token.
     *
     * @param token JWT token string
     * @return Claims object containing all token data
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Gets the signing key for JWT operations.
     * Uses HMAC SHA-256 algorithm.
     *
     * @return SecretKey for signing/verifying tokens
     */
    private SecretKey getSigningKey() {
        var keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

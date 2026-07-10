package com.example.rentrella.auth.security;

import com.example.rentrella.auth.domain.User;
import com.example.rentrella.auth.domain.UserRole;
import com.example.rentrella.auth.exception.AuthException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long accessTokenValidityMs;
    private final long refreshTokenValidityMs;

    public JwtTokenProvider(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.access-token-validity-ms}") long accessTokenValidityMs,
            @Value("${app.jwt.refresh-token-validity-ms}") long refreshTokenValidityMs
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenValidityMs = accessTokenValidityMs;
        this.refreshTokenValidityMs = refreshTokenValidityMs;
    }

    public String createAccessToken(User user) {
        return createToken(user, accessTokenValidityMs, "access");
    }

    public String createRefreshToken(User user) {
        return createToken(user, refreshTokenValidityMs, "refresh");
    }

    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isAccessToken(String token) {
        return "access".equals(parseClaims(token).get("type", String.class));
    }

    public boolean isRefreshToken(String token) {
        return "refresh".equals(parseClaims(token).get("type", String.class));
    }

    public long getAccessTokenValidityMs() {
        return accessTokenValidityMs;
    }

    public long getRefreshTokenValidityMs() {
        return refreshTokenValidityMs;
    }

    private String createToken(User user, long validityMs, String type) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusMillis(validityMs);

        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .claim("email", user.getEmail())
                .claim("role", user.getRole().name())
                .claim("type", type)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .signWith(key)
                .compact();
    }

    public AuthenticatedUser toAuthenticatedUser(String token) {
        try {
            Claims claims = parseClaims(token);
            return new AuthenticatedUser(
                    Long.valueOf(claims.getSubject()),
                    claims.get("email", String.class),
                    UserRole.valueOf(claims.get("role", String.class))
            );
        } catch (JwtException | IllegalArgumentException exception) {
            throw new AuthException(HttpStatus.UNAUTHORIZED, "Invalid token.");
        }
    }
}

package com.example.rentrella.auth.dto;

public record TokenResponse(
        String tokenType,
        String accessToken,
        String refreshToken,
        long expiresIn
) {
}

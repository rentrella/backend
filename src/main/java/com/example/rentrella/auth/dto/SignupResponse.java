package com.example.rentrella.auth.dto;

public record SignupResponse(
        Long userId,
        String email,
        String name
) {
}

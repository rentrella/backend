package com.example.rentrella.auth.security;

import com.example.rentrella.auth.domain.UserRole;

public record AuthenticatedUser(
        Long id,
        String email,
        UserRole role
) {
}

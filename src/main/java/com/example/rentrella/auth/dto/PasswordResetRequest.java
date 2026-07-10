package com.example.rentrella.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PasswordResetRequest(
        @Email @NotBlank String email,
        @NotBlank String verificationCode,
        @NotBlank @Size(min = 8, max = 100) String newPassword
) {
}

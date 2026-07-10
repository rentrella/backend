package com.example.rentrella.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignupRequest(
        @Email @NotBlank String email,
        @NotBlank @Size(min = 8, max = 100) String password,
        @NotBlank @Size(max = 50) String name,
        @Size(max = 20) String studentNumber,
        @Size(max = 100) String dataGsmId,
        @NotBlank String verificationCode
) {
}

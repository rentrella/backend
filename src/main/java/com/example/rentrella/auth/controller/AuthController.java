package com.example.rentrella.auth.controller;

import com.example.rentrella.auth.dto.EmailCodeRequest;
import com.example.rentrella.auth.dto.LoginRequest;
import com.example.rentrella.auth.dto.LogoutRequest;
import com.example.rentrella.auth.dto.PasswordResetRequest;
import com.example.rentrella.auth.dto.ReissueRequest;
import com.example.rentrella.auth.dto.SignupRequest;
import com.example.rentrella.auth.dto.SignupResponse;
import com.example.rentrella.auth.dto.TokenResponse;
import com.example.rentrella.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody LogoutRequest request) {
        authService.logout(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reissue")
    public ResponseEntity<TokenResponse> reissue(@Valid @RequestBody ReissueRequest request) {
        return ResponseEntity.ok(authService.reissue(request));
    }

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupRequest request) {
        return ResponseEntity.ok(authService.signup(request));
    }

    @PostMapping("/email/signup-code")
    public ResponseEntity<Void> sendSignupCode(@Valid @RequestBody EmailCodeRequest request) {
        authService.sendSignupCode(request);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/email/password-reset-code")
    public ResponseEntity<Void> sendPasswordResetCode(@Valid @RequestBody EmailCodeRequest request) {
        authService.sendPasswordResetCode(request);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/password/reset")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody PasswordResetRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.noContent().build();
    }
}

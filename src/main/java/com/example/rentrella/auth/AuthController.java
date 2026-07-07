package com.example.rentrella.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AuthController {

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, Object> request) {
        throw new UnsupportedOperationException();
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        throw new UnsupportedOperationException();
    }

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue() {
        throw new UnsupportedOperationException();
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Map<String, Object> request) {
        throw new UnsupportedOperationException();
    }

    @PostMapping("/email/signup-code")
    public ResponseEntity<?> sendSignupCode(@RequestBody Map<String, Object> request) {
        throw new UnsupportedOperationException();
    }

    @PostMapping("/password/reset")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, Object> request) {
        throw new UnsupportedOperationException();
    }
}

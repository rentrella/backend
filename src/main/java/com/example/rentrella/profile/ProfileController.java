package com.example.rentrella.profile;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProfileController {

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        throw new UnsupportedOperationException();
    }
}

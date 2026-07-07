package com.example.rentrella.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/inquiry")
    public ResponseEntity<?> getInquiries() {
        throw new UnsupportedOperationException();
    }

    @PatchMapping("/inquiry/complete")
    public ResponseEntity<?> completeInquiry(@RequestBody Map<String, Object> request) {
        throw new UnsupportedOperationException();
    }

    @GetMapping("/umbrella")
    public ResponseEntity<?> getUmbrellaRentalLogs() {
        throw new UnsupportedOperationException();
    }

    @PatchMapping("/lock/umbrella")
    public ResponseEntity<?> lockUmbrella(@RequestBody Map<String, Object> request) {
        throw new UnsupportedOperationException();
    }

    @PatchMapping("/lock/user")
    public ResponseEntity<?> lockUser(@RequestBody Map<String, Object> request) {
        throw new UnsupportedOperationException();
    }

    @PatchMapping("/open")
    public ResponseEntity<?> openUmbrellaSlot(@RequestBody Map<String, Object> request) {
        throw new UnsupportedOperationException();
    }
}

package com.example.rentrella.admin.controller;

import com.example.rentrella.admin.dto.request.LockUmbrellaRequest;
import com.example.rentrella.admin.dto.request.LockUserRequest;
import com.example.rentrella.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

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
        return ResponseEntity.ok(adminService.getRentalLogs());
    }

    @PatchMapping("/lock/umbrella")
    public ResponseEntity<?> lockUmbrella(@RequestBody LockUmbrellaRequest request) {
        return ResponseEntity.ok(adminService.lockUmbrella(request.deviceId()));
    }

    @PatchMapping("/lock/user")
    public ResponseEntity<?> lockUser(@RequestBody LockUserRequest request) {
        return ResponseEntity.ok(adminService.lockUser(request.userId()));
    }

    @PatchMapping("/open")
    public ResponseEntity<?> openUmbrellaSlot(@RequestParam Long deviceId) {
        return ResponseEntity.ok(adminService.openUmbrellaSlot(deviceId));
    }
}

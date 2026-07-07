package com.example.rentrella.device;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/commands")
public class DeviceController {

    @GetMapping("/latest")
    public ResponseEntity<?> pollLatestCommand(@RequestParam Long deviceId) {
        throw new UnsupportedOperationException();
    }

    @PostMapping("/{commandId}/complete")
    public ResponseEntity<?> reportCommandResult(@PathVariable Long commandId, @RequestBody Map<String, Object> request) {
        throw new UnsupportedOperationException();
    }
}

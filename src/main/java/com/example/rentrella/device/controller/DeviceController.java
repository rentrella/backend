package com.example.rentrella.device.controller;

import com.example.rentrella.device.dto.CompleteCommandRequest;
import com.example.rentrella.device.service.DeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/commands")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    @GetMapping("/latest")
    public ResponseEntity<?> pollLatestCommand(@RequestParam Long deviceId) {
        return ResponseEntity.ok(deviceService.pollLatestCommand(deviceId));
    }

    @PostMapping("/{commandId}/complete")
    public ResponseEntity<?> reportCommandResult(@PathVariable Long commandId, @RequestBody CompleteCommandRequest request) {
        return ResponseEntity.ok(deviceService.completeCommand(commandId));
    }
}

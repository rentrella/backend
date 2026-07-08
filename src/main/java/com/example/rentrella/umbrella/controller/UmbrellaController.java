package com.example.rentrella.umbrella.controller;

import com.example.rentrella.umbrella.dto.RentRequest;
import com.example.rentrella.umbrella.service.UmbrellaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/umbrella")
@RequiredArgsConstructor
public class UmbrellaController {

    private final UmbrellaService umbrellaService;

    @GetMapping
    public ResponseEntity<?> getAvailableUmbrellas() {
        return ResponseEntity.ok(umbrellaService.getAvailableCount());
    }

    @PostMapping("/Rental")
    public ResponseEntity<?> rentUmbrella(@RequestBody RentRequest request) {
        return ResponseEntity.ok(umbrellaService.rentUmbrella(request.deviceId()));
    }

    @PostMapping("/return")
    public ResponseEntity<?> returnUmbrella(@RequestBody RentRequest request) {
        return ResponseEntity.ok(umbrellaService.returnUmbrella(request.deviceId()));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyRentedUmbrella() {
        throw new UnsupportedOperationException();
    }
}

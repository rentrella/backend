package com.example.rentrella.umbrella;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/umbrella")
public class UmbrellaController {

    @GetMapping
    public ResponseEntity<?> getAvailableUmbrellas() {
        throw new UnsupportedOperationException();
    }

    @PostMapping("/Rental")
    public ResponseEntity<?> rentUmbrella(@RequestBody Map<String, Object> request) {
        throw new UnsupportedOperationException();
    }

    @PostMapping("/return")
    public ResponseEntity<?> returnUmbrella(@RequestBody Map<String, Object> request) {
        throw new UnsupportedOperationException();
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyRentedUmbrella() {
        throw new UnsupportedOperationException();
    }
}

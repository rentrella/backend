package com.example.rentrella.alarm;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AlarmController {

    @GetMapping("/alarm")
    public ResponseEntity<?> getAlarms() {
        throw new UnsupportedOperationException();
    }
}

package com.example.rentrella.inquiry;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/inquiry")
public class InquiryController {

    @PostMapping
    public ResponseEntity<?> createInquiry(@RequestBody Map<String, Object> request) {
        throw new UnsupportedOperationException();
    }
}

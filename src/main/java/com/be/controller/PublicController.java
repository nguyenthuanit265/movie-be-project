package com.be.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/resources")
public class PublicController {
    @GetMapping("/public")
    public ResponseEntity<String> getUser() {
        return ResponseEntity.ok("Hello World");
    }
}

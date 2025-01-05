package com.security.controller;

import com.security.model.base.AppResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admins")
public class AdminController {
    @GetMapping("/profiles")
    public ResponseEntity<?> adminProfile(HttpServletRequest request) {
        return new ResponseEntity<>(AppResponse.buildResponse(HttpStatus.OK, "Admin request"), HttpStatus.OK);
    }

    @GetMapping("/board")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> adminAccess() {
        return new ResponseEntity<>(AppResponse.buildResponse(HttpStatus.OK, "Admin Board."), HttpStatus.OK);
    }
}

package com.be.controller;

import com.be.config.JwtTokenProvider;
import com.be.model.base.AppResponse;
import com.be.model.dto.auth.AuthRequest;
import com.be.model.dto.auth.AuthResponse;
import com.be.model.dto.auth.SignUpRequest;
import com.be.model.dto.auth.UserDto;
import com.be.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final UserService userService;
    private final AuthenticationManager authManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public AuthController(UserService userService, AuthenticationManager authManager) {
        this.userService = userService;
        this.authManager = authManager;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody @Valid SignUpRequest req, HttpServletRequest request) {
        return userService.signUp(req, request);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthRequest request, HttpServletRequest req) {

        Optional<UserDto> optUser = userService.findByEmail(request.getEmail());
        if (optUser.isEmpty()) {
            return new ResponseEntity<>(AppResponse.buildResponse("", "", "User is not exist in system, please sign up", HttpStatus.UNAUTHORIZED.value(), request), HttpStatus.UNAUTHORIZED);
        }

        // Spring Security use authenticate function -> call functions loadUserByUsername and get username and password -> using PasswordEncoder Bean authenticate user login
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword())
        );

        // Get user login info
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtTokenProvider.generateAccessToken(authentication);

        AuthResponse authResponse = new AuthResponse();
        optUser.ifPresent(userDto -> authResponse.setId(userDto.getId()));
        authResponse.setAccessToken(jwt);
        authResponse.setEmail(request.getEmail());
        return new ResponseEntity<>(AppResponse.buildResponse(HttpStatus.OK, authResponse), HttpStatus.OK);
    }
}

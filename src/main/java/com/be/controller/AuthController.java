package com.be.controller;

import com.be.appexception.InvalidTokenException;
import com.be.appexception.ResourceNotFoundException;
import com.be.appexception.TokenExpiredException;
import com.be.config.JwtTokenProvider;
import com.be.model.base.AppResponse;
import com.be.model.dto.auth.*;
import com.be.model.entity.User;
import com.be.repository.UserRepository;
import com.be.service.AuthService;
import com.be.service.PasswordResetService;
import com.be.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final UserService userService;
    private final AuthenticationManager authManager;
    private final PasswordResetService passwordResetService;
    private final HttpServletRequest request;
    private final UserRepository userRepository;
    private final AuthService authService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public AuthController(UserService userService,
                          AuthenticationManager authManager,
                          PasswordResetService passwordResetService,
                          HttpServletRequest request,
                          UserRepository userRepository,
                          AuthService authService) {
        this.userService = userService;
        this.authManager = authManager;
        this.passwordResetService = passwordResetService;
        this.request = request;
        this.userRepository = userRepository;
        this.authService = authService;
    }

    @ExceptionHandler({InvalidTokenException.class, TokenExpiredException.class})
    public ResponseEntity<AppResponse<Void>> handleTokenExceptions(Exception e) {
        return ResponseEntity.badRequest()
                .body(AppResponse.buildResponse(
                        e.getMessage(),
                        request.getRequestURI(),
                        "Password reset failed",
                        HttpStatus.BAD_REQUEST.value(),
                        null
                ));
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

        if (Boolean.FALSE.equals(optUser.get().getIsActive())) {
            return new ResponseEntity<>(AppResponse.buildResponse("", "", "User is not active in system, please verify account", HttpStatus.UNAUTHORIZED.value(), request), HttpStatus.UNAUTHORIZED);
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

    @PostMapping("/forgot-password")
    public ResponseEntity<AppResponse<Void>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest forgotRequest) {

        User user = userRepository.findByEmail(forgotRequest.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found", "", "", ""));

        passwordResetService.createPasswordResetTokenForUser(user);

        return ResponseEntity.ok(AppResponse.buildResponse(
                null,
                request.getRequestURI(),
                "Password reset email sent successfully",
                HttpStatus.OK.value(),
                null
        ));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<AppResponse<Void>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest resetRequest, @RequestParam("token") String token) {

        resetRequest.setToken(token);
        passwordResetService.resetPassword(resetRequest);

        return ResponseEntity.ok(AppResponse.buildResponse(
                null,
                request.getRequestURI(),
                "Password has been reset successfully",
                HttpStatus.OK.value(),
                null
        ));
    }

    @GetMapping("/verify")
    public ResponseEntity<AppResponse<String>> verifyAccount(@RequestParam String token) {
        String result = authService.verifyAccount(token);

        return ResponseEntity.ok(AppResponse.buildResponse(
                null,
                request.getRequestURI(),
                result,
                HttpStatus.OK.value(),
                null
        ));
    }

    @PostMapping("/verify/resend")
    public ResponseEntity<AppResponse<Void>> resendVerificationToken(@RequestParam String email) {
        authService.resendVerificationToken(email);

        return ResponseEntity.ok(AppResponse.buildResponse(
                null,
                request.getRequestURI(),
                "Verification email sent",
                HttpStatus.OK.value(),
                null
        ));
    }
}

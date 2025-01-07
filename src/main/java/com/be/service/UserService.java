package com.be.service;
import com.be.model.dto.auth.SignUpRequest;
import com.be.model.dto.auth.SpringSecurityUserDetailsDto;
import com.be.model.dto.auth.UserDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface UserService {
    Optional<UserDto> findByEmail(String email);

    Optional<SpringSecurityUserDetailsDto> findByUsername(String username);

    ResponseEntity<?> signUp(SignUpRequest request, HttpServletRequest servletRequest);

    ResponseEntity<?> getUserById(Long id);
}

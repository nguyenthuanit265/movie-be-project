package com.security.service;
import com.security.model.dto.SignUpRequest;
import com.security.model.dto.SpringSecurityUserDetailsDto;
import com.security.model.dto.UserDto;
import com.security.model.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface UserService {
    Optional<UserDto> findByEmail(String email);

    Optional<SpringSecurityUserDetailsDto> findByUsername(String username);

    ResponseEntity<?> signUp(SignUpRequest request, HttpServletRequest servletRequest);

    ResponseEntity<?> getUserById(Long id);
}

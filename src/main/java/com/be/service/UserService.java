package com.be.service;

import com.be.model.dto.MovieDTO;
import com.be.model.dto.UserProfileDTO;
import com.be.model.dto.UserRatingDTO;
import com.be.model.dto.auth.SignUpRequest;
import com.be.model.dto.auth.SpringSecurityUserDetailsDto;
import com.be.model.dto.auth.UserDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface UserService {
    Optional<UserDto> findByEmail(String email);

    Optional<SpringSecurityUserDetailsDto> findByUsername(String username);

    ResponseEntity<?> signUp(SignUpRequest request, HttpServletRequest servletRequest);

    ResponseEntity<?> getUserById(Long id);

    UserProfileDTO getUserProfile(Long userId);

    Page<MovieDTO> getUserFavorites(Long userId, Pageable pageable);

    Page<MovieDTO> getUserWatchlist(Long userId, Pageable pageable);

    Page<UserRatingDTO> getUserRatings(Long userId, Pageable pageable);
}

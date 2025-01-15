package com.be.controller;

import com.be.model.base.AppResponse;
import com.be.model.dto.MovieDTO;
import com.be.model.dto.UserProfileDTO;
import com.be.model.dto.UserRatingDTO;
import com.be.service.UserService;
import com.be.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@Slf4j
public class UserProfileController {

    private final UserService userService;

    public UserProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<AppResponse<UserProfileDTO>> getProfile(HttpServletRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        UserProfileDTO profile = userService.getUserProfile(userId);

        return ResponseEntity.ok(AppResponse.buildResponse(
                null,
                request.getRequestURI(),
                "Profile retrieved successfully",
                HttpStatus.OK.value(),
                profile
        ));
    }

    @GetMapping("/profile/watchlist")
    public ResponseEntity<AppResponse<Page<MovieDTO>>> getWatchlist(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        Page<MovieDTO> watchlist = userService.getUserWatchlist(userId, PageRequest.of(page, size));

        return ResponseEntity.ok(AppResponse.buildResponse(
                null,
                request.getRequestURI(),
                "Watchlist retrieved successfully",
                HttpStatus.OK.value(),
                watchlist
        ));
    }

    @GetMapping("/profile/favorites")
    public ResponseEntity<AppResponse<Page<MovieDTO>>> getFavorites(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        Page<MovieDTO> favorites = userService.getUserFavorites(userId, PageRequest.of(page, size));

        return ResponseEntity.ok(AppResponse.buildResponse(
                null,
                request.getRequestURI(),
                "Favorites retrieved successfully",
                HttpStatus.OK.value(),
                favorites
        ));
    }

    @GetMapping("/profile/ratings")
    public ResponseEntity<AppResponse<Page<UserRatingDTO>>> getRatings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        Page<UserRatingDTO> ratings = userService.getUserRatings(userId, PageRequest.of(page, size));

        return ResponseEntity.ok(AppResponse.buildResponse(
                null,
                request.getRequestURI(),
                "Ratings retrieved successfully",
                HttpStatus.OK.value(),
                ratings
        ));
    }
}

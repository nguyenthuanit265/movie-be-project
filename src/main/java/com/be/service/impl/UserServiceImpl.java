package com.be.service.impl;


import com.be.appexception.ResourceNotFoundException;
import com.be.model.base.AppResponse;
import com.be.model.dto.MovieDTO;
import com.be.model.dto.UserProfileDTO;
import com.be.model.dto.UserRatingDTO;
import com.be.model.dto.auth.SignUpRequest;
import com.be.model.dto.auth.SpringSecurityUserDetailsDto;
import com.be.model.dto.auth.UserDto;
import com.be.model.entity.MovieRating;
import com.be.model.entity.User;
import com.be.model.entity.UserRole;
import com.be.repository.MovieRatingRepository;
import com.be.repository.MovieRepository;
import com.be.repository.UserRepository;
import com.be.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final MovieRepository movieRepository;
    private final MovieRatingRepository ratingRepository;

    public UserServiceImpl(UserRepository userRepository,
                           ModelMapper modelMapper,
                           MovieRepository movieRepository,
                           MovieRatingRepository ratingRepository) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.movieRepository = movieRepository;
        this.ratingRepository = ratingRepository;
    }

    @Override
    public Optional<UserDto> findByEmail(String email) {
        if (ObjectUtils.isEmpty(email)) {
            return Optional.empty();
        }
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            return Optional.ofNullable(UserDto.builder().id(user.get().getId()).email(user.get().getEmail()).name(user.get().getFullName()).build());
        }
        return Optional.empty();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResponseEntity<?> signUp(SignUpRequest request, HttpServletRequest servletRequest) {
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());
        if (optionalUser.isPresent()) {
            return new ResponseEntity<>(AppResponse.buildResponse("", "", "User is existed in system, please sign up with the other email", HttpStatus.BAD_REQUEST.value(), request), HttpStatus.BAD_REQUEST);
        }
        User user = new User();
        user.setEmail(request.getEmail());
        user.setFullName(request.getName());
        String hashPassword = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt());
        user.setPasswordHash(hashPassword);
        User saved = userRepository.save(user);
        return new ResponseEntity<>(modelMapper.map(saved, UserDto.class), HttpStatus.CREATED);
    }

    @Transactional
    @Override
    public Optional<SpringSecurityUserDetailsDto> findByUsername(String username) {
        if (ObjectUtils.isEmpty(username)) {
            return Optional.empty();
        }
        Optional<User> user = userRepository.findByEmail(username);
        if (user.isPresent()) {
            return Optional.ofNullable(SpringSecurityUserDetailsDto.builder().id(user.get().getId()).email(user.get().getEmail()).name(user.get().getFullName()).build());
        }
        return Optional.empty();
    }

    @Transactional
    @Override
    public ResponseEntity<?> getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return new ResponseEntity<>(AppResponse.buildResponse(HttpStatus.OK, modelMapper.map(user, UserDto.class)), HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    @Override
    public UserProfileDTO getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Get stats
        int totalWatchlist = user.getWatchlist().size();
        int totalFavorites = user.getFavorites().size();
        int totalRatings = user.getRatings().size();

        Double averageRating = user.getRatings().stream()
                .map(MovieRating::getValue)
                .filter(Objects::nonNull)
                .mapToDouble(Float::doubleValue)
                .average()
                .orElse(0.0);

        // Get recent items (last 5)
        List<MovieDTO> recentWatchlist = user.getWatchlist().stream()
                .sorted(Comparator.comparing(m -> m.getUpdatedAt(), Comparator.reverseOrder()))
                .limit(5)
                .map(item -> MovieDTO.fromEntity(item, user))
                .collect(Collectors.toList());

        List<MovieDTO> recentFavorites = user.getFavorites().stream()
                .sorted(Comparator.comparing(m -> m.getUpdatedAt(), Comparator.reverseOrder()))
                .limit(5)
                .map(item -> MovieDTO.fromEntity(item, user))
                .collect(Collectors.toList());

        List<UserRatingDTO> recentRatings = user.getRatings().stream()
                .sorted(Comparator.comparing(MovieRating::getCreatedAt).reversed())
                .limit(5)
                .map(rating -> UserRatingDTO.builder()
                        .id(rating.getId())
                        .movie(MovieDTO.fromEntity(rating.getMovie(), user))
                        .rating(rating.getValue())
                        .createdAt(rating.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return UserProfileDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .provider(user.getProvider())
                .providerId(user.getProviderId())
                .imageUrl(user.getImageUrl())
                .role(user.getRole())
                .totalWatchlist(totalWatchlist)
                .totalFavorites(totalFavorites)
                .totalRatings(totalRatings)
                .averageRating(Float.valueOf(String.valueOf(averageRating)))
                .recentWatchlist(recentWatchlist)
                .recentFavorites(recentFavorites)
                .recentRatings(recentRatings)
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public Page<MovieDTO> getUserWatchlist(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return movieRepository.findByWatchlistedByOrderByUpdatedAtDesc(user, pageable)
                .map(item -> MovieDTO.fromEntity(item, user));
    }

    @Transactional(readOnly = true)
    @Override
    public Page<MovieDTO> getUserFavorites(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return movieRepository.findByFavoritedByOrderByUpdatedAtDesc(user, pageable)
                .map(item -> MovieDTO.fromEntity(item, user));
    }

    @Transactional(readOnly = true)
    @Override
    public Page<UserRatingDTO> getUserRatings(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return ratingRepository.findByUserOrderByCreatedAtDesc(user, pageable)
                .map(rating -> UserRatingDTO.builder()
                        .id(rating.getId())
                        .movie(MovieDTO.fromEntity(rating.getMovie(), user))
                        .rating(rating.getValue())
                        .createdAt(rating.getCreatedAt())
                        .build());
    }
}

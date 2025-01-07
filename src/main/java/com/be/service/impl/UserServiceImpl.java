package com.be.service.impl;


import com.be.appexception.ResourceNotFoundException;
import com.be.model.base.AppResponse;
import com.be.model.dto.auth.SignUpRequest;
import com.be.model.dto.auth.SpringSecurityUserDetailsDto;
import com.be.model.dto.auth.UserDto;
import com.be.model.entity.User;
import com.be.model.entity.UserRole;
import com.be.repository.UserRepository;
import com.be.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public UserServiceImpl(UserRepository userRepository,
                           ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
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

    @jakarta.transaction.Transactional
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

    @Override
    public ResponseEntity<?> getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return new ResponseEntity<>(AppResponse.buildResponse(HttpStatus.OK, modelMapper.map(user, UserDto.class)), HttpStatus.OK);
    }
}

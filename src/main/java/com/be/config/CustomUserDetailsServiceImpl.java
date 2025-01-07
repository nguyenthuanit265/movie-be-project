package com.be.config;


import com.be.appexception.ResourceNotFoundException;
import com.be.model.entity.User;
import com.be.model.entity.UserRole;
import com.be.repository.UserRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component(value = "customUserDetailsService")
public class CustomUserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomUserDetailsServiceImpl(
            UserRepository userRepository,
            @Qualifier("passwordEncoder") PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found.");
        }


        UserRole userRole = UserRole.user;
        if (user.get().getEmail().contains("admin")) {
            userRole = UserRole.admin;
        } else if (user.get().getEmail().contains("user")) {
            userRole = UserRole.user;
        } else if (user.get().getEmail().contains("moderator")) {
            userRole = UserRole.moderator;
        } else {
            userRole = UserRole.user;
        }
//        List<GrantedAuthority> authorities = roles.stream()
//                .map(role -> new SimpleGrantedAuthority(role.getCode()))
//                .collect(Collectors.toList());
//        org.springframework.security.core.userdetails.User.UserBuilder userByEmailBuilder = org.springframework.security.core.userdetails.User.withUsername(user.get().getEmail())
//                .password(user.get().getPassword());
//        userByEmailBuilder.authorities(authorities);
//        return userByEmailBuilder.build();

//        if (!CollectionUtils.isEmpty(user.get().getRoles())) {
//            userByEmailBuilder.authorities(getAuthorities(user.get().getRoles()));
//        }

        user.get().setRole(userRole.name());
        return UserPrincipal.create(user.get());
    }

    @Transactional
    public UserDetails loadUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", id)
        );

        return UserPrincipal.create(user);
    }
}

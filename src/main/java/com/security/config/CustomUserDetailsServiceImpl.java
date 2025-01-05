package com.security.config;


import com.security.appexception.ResourceNotFoundException;
import com.security.model.entity.Role;
import com.security.model.entity.User;
import com.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

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


        List<Role> roles = new ArrayList<>();
        if (user.get().getEmail().contains("admin")) {
            roles.add(new Role("ROLE_ADMIN", "ADMIN"));
        } else if (user.get().getEmail().contains("user")) {
            roles.add(new Role("ROLE_USER", "USER"));
        } else if (user.get().getEmail().contains("moderator")) {
            roles.add(new Role("ROLE_MODERATOR", "MODERATOR"));
        } else {
            roles.add(new Role("ROLE_USER", "USER"));
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

        user.get().setRoles(new HashSet<>(roles));
        return UserPrincipal.create(user.get());
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Collection<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getCode().toUpperCase()))
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDetails loadUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", id)
        );

        return UserPrincipal.create(user);
    }
}

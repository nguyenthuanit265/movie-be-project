package com.security.config;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SpringSecurityUserDetailsDto implements UserDetails {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String email;
    private String username;
    private String name;
    private String password;
    private Boolean enabled = true;
    private Boolean credentialsExpired;
    private Boolean expired;
    private Boolean isLocked;
//    private Set<Role> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

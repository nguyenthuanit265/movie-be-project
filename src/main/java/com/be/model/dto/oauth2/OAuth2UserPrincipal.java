package com.be.model.dto.oauth2;


import com.be.model.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class OAuth2UserPrincipal implements OAuth2User {
    private Long id;
    private String email;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private Map<String, Object> attributes;

    public OAuth2UserPrincipal(Long id, String email, String password, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    public static OAuth2UserPrincipal create(User user) {
        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(user.getRole()));

        return new OAuth2UserPrincipal(
                user.getId(),
                user.getEmail(),
                user.getPasswordHash(),
                authorities
        );
    }

    public static OAuth2UserPrincipal create(User user, Map<String, Object> attributes) {
        OAuth2UserPrincipal userPrincipal = OAuth2UserPrincipal.create(user);
        userPrincipal.setAttributes(attributes);
        return userPrincipal;
    }

    @Override
    public String getName() {
        return String.valueOf(id);
    }
}

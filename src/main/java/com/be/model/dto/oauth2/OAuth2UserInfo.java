package com.be.model.dto.oauth2;

import lombok.Getter;

import java.util.Map;

@Getter
public class OAuth2UserInfo {
    protected Map<String, Object> attributes;

    public OAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public String getId() {
        return (String) attributes.get("sub");
    }

    public String getName() {
        return (String) attributes.get("name");
    }

    public String getEmail() {
        return (String) attributes.get("email");
    }

    public String getImageUrl() {
        return (String) attributes.get("picture");
    }
}
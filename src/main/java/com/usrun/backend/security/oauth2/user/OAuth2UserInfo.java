package com.usrun.backend.security.oauth2.user;

import com.usrun.backend.model.type.AuthType;

public abstract class OAuth2UserInfo {

    protected AuthType type;

    public OAuth2UserInfo(AuthType type) {
        this.type = type;
    }

    public abstract String getId();

    public abstract String getName();

    public abstract String getEmail();

    public abstract String getImageUrl();

    public AuthType getType() {
        return type;
    }
}

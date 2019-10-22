package com.usrun.backend.security.oauth2.user;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.usrun.backend.model.AuthType;

public class GoogleOAuth2UserInfo extends OAuth2UserInfo {
    private GoogleIdToken.Payload payload;

    public GoogleOAuth2UserInfo(AuthType type, GoogleIdToken.Payload payload) {
        super(type);
        this.payload = payload;
    }

    @Override
    public String getId() {
        return payload.getSubject();
    }

    @Override
    public String getName() {
        return (String)payload.get("name");
    }

    @Override
    public String getEmail() {
        return (String)payload.get("email");
    }

    @Override
    public String getImageUrl() {
        return (String)payload.get("picture");
    }
}

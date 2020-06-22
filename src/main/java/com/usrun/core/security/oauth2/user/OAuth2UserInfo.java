package com.usrun.core.security.oauth2.user;

import com.usrun.core.model.type.AuthType;

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

package com.usrun.core.security.oauth2;

/**
 * @author phuctt4
 */
public interface OAuth2Verify {
  UserInfo verify(String accessToken);
}

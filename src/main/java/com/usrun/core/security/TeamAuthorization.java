package com.usrun.core.security;

import org.springframework.security.core.Authentication;

/**
 * @author phuctt4
 */

public interface TeamAuthorization {

  boolean authorize(Authentication authentication, String roleTeam, long teamId);
}

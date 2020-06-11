package com.usrun.core.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * @author phuctt4
 */

public interface TeamAuthorization {
    boolean authorize(Authentication authentication, String roleTeam, long teamId);
}

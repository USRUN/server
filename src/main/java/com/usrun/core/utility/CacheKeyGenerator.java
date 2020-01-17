package com.usrun.core.utility;

import org.springframework.stereotype.Component;

/**
 * @author phuctt4
 */

@Component
public class CacheKeyGenerator {

    public String keyVerifyOtp(Long userId) {
        return "users:otp:" + userId;
    }

    public String keyTrack(Long trackId) {
        return "track:" + trackId;
    }
}

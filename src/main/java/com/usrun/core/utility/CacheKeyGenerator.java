package com.usrun.core.utility;

import com.usrun.core.model.type.TeamMemberType;
import org.springframework.stereotype.Component;

/**
 * @author phuctt4
 */

@Component
public class CacheKeyGenerator {

    public String keyVerifyOtp(long userId) {
        return "users:otp:" + userId;
    }

    public String keyTrack(long trackId) {
        return "track:" + trackId;
    }

    public String keyUser(long userId) {
        return "user:" + userId;
    }

    public String keyUserEmail(String email) {
        return "users:email:" + email;
    }

    public String keyTrackSig(long trackId, String sig) {
        return "track:sig:" + trackId + sig;
    }

    public String keyTeamMemberType(long teamId, long userId) {
        return "team:role:" + teamId + ":" + userId;
    }

    public String keyPost(long postId) {
        return "post:" + postId;
    }

    public String keyPostSortedSet(long teamId) {
        return "post:team:" + teamId;
    }
}

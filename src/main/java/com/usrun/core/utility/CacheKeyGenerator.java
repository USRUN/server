package com.usrun.core.utility;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;

/**
 * @author phuctt4
 */

@Component
public class CacheKeyGenerator {

  private final String PREFIX;

  public CacheKeyGenerator(@Value("${spring.profiles.active}") String env) {
    this.PREFIX = env + ":";
  }

  public String keyVerifyOtp(long userId) {
    return PREFIX + "users:otp:" + userId;
  }

  public String keyTrack(long trackId) {
    return PREFIX + "track:" + trackId;
  }

  public String keyUser(long userId) {
    return PREFIX + "user:" + userId;
  }

  public String keyUserEmail(String email) {
    return PREFIX + "users:email:" + email;
  }

  public String keyTrackSig(long trackId, String sig) {
    return PREFIX + "track:sig:" + trackId + sig;
  }

  public String keyTeamMemberType(long teamId, long userId) {
    return PREFIX + "team:role:" + teamId + ":" + userId;
  }

  public String keyActivity(long activityId) {
    return PREFIX + "activity:" + activityId;
  }

  public String keyActivitySortedSet(long teamId) {
    return PREFIX + "activities:team:" + teamId;
  }

  public String keyActivityCountByTeam(long teamId) {
    return PREFIX + "activities:team:count:" + teamId;
  }

  public String keyActivityLock(long userId, long time) {
    return PREFIX + "activity:lock:" + userId + ":" + time;
  }
  
  public String keyTeamStat(){
      return PREFIX + "team:stat:";
  }

  public String keyLoveCount(long activityId) {
    return PREFIX + "activity:love:" + activityId;
  }
}

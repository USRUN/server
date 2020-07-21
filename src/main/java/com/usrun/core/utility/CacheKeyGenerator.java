package com.usrun.core.utility;

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

  public String keyActivity(long activityId) {
    return "activity:" + activityId;
  }

  public String keyActivitySortedSet(long teamId) {
    return "activities:team:" + teamId;
  }

  public String keyActivityCountByTeam(long teamId) {
    return "activities:team:count:" + teamId;
  }

  public String keyActivityLock(long userId, long time) {
    return "activity:lock:" + userId + ":" + time;
  }
  
  public String keyTeamStat(){
      return"team:stat:";
  }

  public String keyLoveCount(long activityId) {
    return "activity:love:" + activityId;
  }
}

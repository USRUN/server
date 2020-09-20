package com.usrun.core.utility;

import com.fasterxml.jackson.core.type.TypeReference;
import com.usrun.core.model.User;
import com.usrun.core.model.UserActivity;
import com.usrun.core.model.track.Track;
import com.usrun.core.model.type.TeamMemberType;
import com.usrun.core.payload.dto.TeamActivityCountDTO;
import com.usrun.core.payload.dto.TeamStatDTO;
import com.usrun.core.repository.TeamRepository;
import com.usrun.core.repository.UserActivityRepository;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RAtomicLongAsync;
import org.redisson.api.RBatch;
import org.redisson.api.RBucket;
import org.redisson.api.RBucketAsync;
import org.redisson.api.RLock;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RScoredSortedSetAsync;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author phuctt4
 */
@Component
public class CacheClient {

  @Autowired
  private RedissonClient redissonClient;

  @Autowired
  private CacheKeyGenerator cacheKeyGenerator;

  @Autowired
  private TeamRepository teamRepository;

  @Autowired
  private UserActivityRepository userActivityRepository;

  public String generateAndSaveOTP(Long userId) {
    String otp = new DecimalFormat("000000").format(new Random().nextInt(999999));
    RBucket<String> rOtp = redissonClient.getBucket(cacheKeyGenerator.keyVerifyOtp(userId));
    rOtp.set(otp, 5, TimeUnit.MINUTES);
    return otp;
  }

  public Boolean verifyOTPFromCache(Long userId, String otp) {
    RBucket<String> rOtp = redissonClient.getBucket(cacheKeyGenerator.keyVerifyOtp(userId));
    return otp.equals(rOtp.getAndDelete());
  }

  public Boolean expireOTP(Long userId) {
    RBucket<String> rOtp = redissonClient.getBucket(cacheKeyGenerator.keyVerifyOtp(userId));
    return rOtp.remainTimeToLive() < 0;
  }

  public User setUser(User user) {
    String key = cacheKeyGenerator.keyUser(user.getId());
    String keyUserEmail = cacheKeyGenerator.keyUserEmail(user.getEmail());

    RBucket<String> rUser = redissonClient.getBucket(key);
    rUser.set(ObjectUtils.toJsonString(user), 14, TimeUnit.DAYS);

    RBucket<String> rUserEmail = redissonClient.getBucket(keyUserEmail);
    rUserEmail.set(String.valueOf(user.getId()), 14, TimeUnit.DAYS);
    return user;
  }

  public User getUser(Long userId) {
    String key = cacheKeyGenerator.keyUser(userId);
    RBucket<String> rBucket = redissonClient.getBucket(key);
    if (rBucket.isExists()) {
      return ObjectUtils.fromJsonString(rBucket.get(), User.class);
    }
    return null;
  }

  public User getUser(String email) {
    String keyUserEmail = cacheKeyGenerator.keyUserEmail(email);
    RBucket<String> rUserEmail = redissonClient.getBucket(keyUserEmail);
    long userId;
    if (rUserEmail.isExists()) {
      userId = Long.parseLong(rUserEmail.get());
    } else {
      return null;
    }
    String key = cacheKeyGenerator.keyUser(userId);
    RBucket<String> rUser = redissonClient.getBucket(key);
    if (rUser.isExists()) {
      return ObjectUtils.fromJsonString(rUser.get(), User.class);
    }
    return null;
  }

  public Track setTrack(Track track) {
    RBucket<String> rBucket = redissonClient
        .getBucket(cacheKeyGenerator.keyTrack(track.getTrackId()));
    rBucket.set(ObjectUtils.toJsonString(track), 1, TimeUnit.DAYS);
    return track;
  }

  public Track getTrack(Long trackId) {
    RBucket<String> rBucket = redissonClient.getBucket(cacheKeyGenerator.keyTrack(trackId));
    if (rBucket.isExists()) {
      return ObjectUtils.fromJsonString(rBucket.get(), Track.class);
    }
    return null;
  }

  public void setTrackSig(Long trackId, String sig) {
    RBucket<Boolean> rBucket = redissonClient
        .getBucket(cacheKeyGenerator.keyTrackSig(trackId, sig));
    rBucket.set(true, 1, TimeUnit.HOURS);
  }

  public boolean getTrackSig(Long trackId, String sig) {
    RBucket<Boolean> rBucket = redissonClient
        .getBucket(cacheKeyGenerator.keyTrackSig(trackId, sig));
    return rBucket.get() == null ? false : true;
  }

  public TeamMemberType getTeamMemberType(long teamId, long userId) {
    RBucket<String> rBucket = redissonClient
        .getBucket(cacheKeyGenerator.keyTeamMemberType(teamId, userId));
    if (rBucket.isExists()) {
      return TeamMemberType.fromInt(Integer.parseInt(rBucket.get()));
    }
    return null;
  }

  public void setTeamMemberType(long teamId, long userId, TeamMemberType teamMemberType) {
    RBucket<Integer> rBucket = redissonClient
        .getBucket(cacheKeyGenerator.keyTeamMemberType(teamId, userId));
    rBucket.set(teamMemberType.toValue(), 14, TimeUnit.DAYS);
  }

  public void setActivityCreated(User user, UserActivity activity) {
    RBatch rBatch = redissonClient.createBatch();
    RBucketAsync<String> rBucket = rBatch
        .getBucket(cacheKeyGenerator.keyActivity(activity.getUserActivityId()));
    Set<Long> teams = user.getTeams();
    if (teams != null && teams.size() > 0) {
      user.getTeams().forEach(team -> {
        RScoredSortedSetAsync<Long> rSortedSet = rBatch
            .getScoredSortedSet(cacheKeyGenerator.keyActivitySortedSet(team));
        rSortedSet.addAsync(activity.getUserActivityId(), activity.getUserActivityId());
        rBatch.getAtomicLong(cacheKeyGenerator.keyActivityCountByTeam(team)).addAndGetAsync(1);
      });
      rBucket.setAsync(ObjectUtils.toJsonString(activity), 2, TimeUnit.DAYS);
      rBatch.execute();
    }
  }

  public void setActivity(UserActivity userActivity) {
    RBucket<String> rBucket = redissonClient
        .getBucket(cacheKeyGenerator.keyActivity(userActivity.getUserActivityId()));
    rBucket.set(ObjectUtils.toJsonString(userActivity), 2, TimeUnit.DAYS);
  }

  public void setActivities(List<UserActivity> userActivities) {
    RBatch rBatch = redissonClient.createBatch();
    userActivities.forEach(userActivity
        -> rBatch.getBucket(cacheKeyGenerator.keyActivity(userActivity.getUserActivityId()))
        .setAsync(ObjectUtils.toJsonString(userActivity), 2, TimeUnit.DAYS));
    rBatch.execute();
  }

  public UserActivity getActivity(long activityId) {
    RBucket<String> rBucket = redissonClient
        .getBucket(cacheKeyGenerator.keyActivity(activityId));
    if (rBucket.isExists()) {
      return ObjectUtils.fromJsonString(rBucket.get(), UserActivity.class);
    }
    return null;
  }

  public List<UserActivity> getActivities(List<Long> activityIds) {
    RBatch rBatch = redissonClient.createBatch();
    activityIds.forEach(
        activityId -> rBatch.getBucket(cacheKeyGenerator.keyActivity(activityId)).getAsync());

    return rBatch.execute().getResponses().stream()
        .map(json -> ObjectUtils.fromJsonString(String.valueOf(json), UserActivity.class)).collect(
            Collectors.toList());
  }

  public List<?> getActivitiesByTeam(long teamId, int count, int offset) {
    RBatch rBatch = redissonClient.createBatch();
    RAtomicLongAsync rCount = rBatch
        .getAtomicLong(cacheKeyGenerator.keyActivityCountByTeam(teamId));
    RScoredSortedSetAsync<Long> rSortedSet = rBatch
        .getScoredSortedSet(cacheKeyGenerator.keyActivitySortedSet(teamId));
    int start = offset * count;
    int stop = (offset + 1) * count - 1;
    rCount.getAsync();
    rSortedSet.sizeAsync();
    rSortedSet.valueRangeReversedAsync(start, stop);
    return rBatch.execute().getResponses();
  }

  public void setActivitiesByTeam(long teamId, List<Long> userActivityIds) {
    RScoredSortedSet<Long> rSortedSet = redissonClient
        .getScoredSortedSet(cacheKeyGenerator.keyActivitySortedSet(teamId));
    Map<Long, Double> map = new HashMap<>();
    userActivityIds.forEach(id -> map.put(id, id.doubleValue()));
    rSortedSet.addAll(map);
  }

  public void setTeamStat(List<TeamStatDTO> teamStat) {
    RBucket<String> rBucket = redissonClient
        .getBucket(cacheKeyGenerator.keyTeamStat());
    rBucket.set(ObjectUtils.toJsonString(teamStat), 7, TimeUnit.DAYS);
  }

  public List<TeamStatDTO> getTeamStat() {
    RBucket<String> rBucket = redissonClient
        .getBucket(cacheKeyGenerator.keyTeamStat());
    return ObjectUtils.fromJsonString(rBucket.get(),
        new TypeReference<List<TeamStatDTO>>() {
        });
  }

  public void incCountActivityByTeam(long teamId) {
    RAtomicLong rCount = redissonClient
        .getAtomicLong(cacheKeyGenerator.keyActivityCountByTeam(teamId));
    rCount.addAndGet(1L);
  }

  public void decCountActivityByTeam(long teamId) {
    RAtomicLong rCount = redissonClient
        .getAtomicLong(cacheKeyGenerator.keyActivityCountByTeam(teamId));
    rCount.addAndGet(-1L);
  }

  public void setCountAllActivityByTeam(List<TeamActivityCountDTO> teamActivityCountDTOS) {
    RBatch rBatch = redissonClient.createBatch();
    teamActivityCountDTOS.forEach(dto
        -> rBatch.getAtomicLong(cacheKeyGenerator
        .keyActivityCountByTeam(dto.getTeamId())).setAsync(dto.getCount()));
    rBatch.execute();
  }

  public boolean acquireActivityLock(long userId, long time, long lockTime)
      throws InterruptedException {
    RLock rLock = redissonClient.getLock(cacheKeyGenerator.keyActivityLock(userId, time));
    return rLock.tryLock(lockTime, TimeUnit.MILLISECONDS);
  }

  public void releaseActivityLock(long userId, long time) {
    RLock rLock = redissonClient.getLock(cacheKeyGenerator.keyActivityLock(userId, time));
    rLock.unlock();
  }

  public boolean updateLoveCount(long activityId, long count) {
    RAtomicLong rLove = redissonClient
        .getAtomicLong(cacheKeyGenerator.keyLoveCount(activityId));
    if (!rLove.isExists()) {
      return false;
    }
    rLove.getAndAdd(count);
    return true;
  }

  public void setLoveCount(long activityId, long loveCount) {
    RAtomicLong rLove = redissonClient.getAtomicLong(cacheKeyGenerator.keyLoveCount(activityId));
    rLove.set(loveCount);
  }

  public long getLoveCount(long activityId) {
    RAtomicLong rLove = redissonClient.getAtomicLong(cacheKeyGenerator.keyLoveCount(activityId));
    if (rLove.isExists()) {
      return rLove.get();
    }
    return -1;
  }

  public List<Long> getLoveCounts(List<Long> activityIds) {
    RBatch rBatch = redissonClient.createBatch();
    activityIds.forEach(
        activityId -> rBatch.getBucket(cacheKeyGenerator.keyLoveCount(activityId)).getAsync());

    return ObjectUtils.fromJsonString(String.valueOf(rBatch.execute().getResponses()),
        new TypeReference<List<Long>>() {
        });
  }

  public List<Long> setLoveCounts(List<UserActivity> activities) {
    RBatch rBatch = redissonClient.createBatch();
    activities.forEach(
        activity -> rBatch.getBucket(cacheKeyGenerator.keyLoveCount(activity.getUserActivityId()))
            .setAsync(activity.getTotalLove()));

    return (List<Long>) rBatch.execute().getResponses();
  }

  public void setAppVersion(String appVersion) {
    RBucket<String> rBucket = redissonClient.getBucket(cacheKeyGenerator.keyAppVersion());
    rBucket.set(appVersion);
  }

  public String getAppVersion() {
    RBucket<String> rBucket = redissonClient.getBucket(cacheKeyGenerator.keyAppVersion());
    if(rBucket.isExists()) {
      return rBucket.get();
    } else {
      return null;
    }
  }
}

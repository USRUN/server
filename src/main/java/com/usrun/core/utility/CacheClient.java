package com.usrun.core.utility;

import com.usrun.core.model.User;
import com.usrun.core.model.UserActivity;
import com.usrun.core.model.track.Track;
import com.usrun.core.model.type.TeamMemberType;
import com.usrun.core.payload.dto.TeamActivityCountDTO;
import com.usrun.core.payload.dto.TeamLeaderBoardDTO;
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

        RBucket<User> rUser = redissonClient.getBucket(key);
        rUser.set(user, 14, TimeUnit.DAYS);

        RBucket<Long> rUserEmail = redissonClient.getBucket(keyUserEmail);
        rUserEmail.set(user.getId(), 14, TimeUnit.DAYS);
        return user;
    }

    public User getUser(Long userId) {
        String key = cacheKeyGenerator.keyUser(userId);
        RBucket<User> rBucket = redissonClient.getBucket(key);
        return rBucket.get();
    }

    public User getUser(String email) {
        String keyUserEmail = cacheKeyGenerator.keyUserEmail(email);
        RBucket<Long> rUserEmail = redissonClient.getBucket(keyUserEmail);
        Long userId = rUserEmail.get();
        if (userId == null) {
            return null;
        }
        String key = cacheKeyGenerator.keyUser(userId);
        RBucket<User> rUser = redissonClient.getBucket(key);
        User user = rUser.get();
        return user;
    }

    public Track setTrack(Track track) {
        RBucket<Track> rBucket = redissonClient
                .getBucket(cacheKeyGenerator.keyTrack(track.getTrackId()));
        rBucket.set(track, 1, TimeUnit.DAYS);
        return track;
    }

    public Track getTrack(Long trackId) {
        RBucket<Track> rBucket = redissonClient.getBucket(cacheKeyGenerator.keyTrack(trackId));
        Track track = rBucket.get();
        return track;
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
        RBucket<Integer> rBucket = redissonClient
                .getBucket(cacheKeyGenerator.keyTeamMemberType(teamId, userId));
        return rBucket.get() == null ? null : TeamMemberType.fromInt(rBucket.get());
    }

    public void setTeamMemberType(long teamId, long userId, TeamMemberType teamMemberType) {
        RBucket<Integer> rBucket = redissonClient
                .getBucket(cacheKeyGenerator.keyTeamMemberType(teamId, userId));
        rBucket.set(teamMemberType.toValue(), 14, TimeUnit.DAYS);
    }

    public void setActivityCreated(User user, UserActivity activity) {
        RBatch rBatch = redissonClient.createBatch();
        RBucketAsync<UserActivity> rBucket = rBatch
                .getBucket(cacheKeyGenerator.keyActivity(activity.getUserActivityId()));
        Set<Long> teams = user.getTeams();
        if (teams != null && teams.size() > 0) {
            user.getTeams().forEach(team -> {
                RScoredSortedSetAsync<Long> rSortedSet = rBatch
                        .getScoredSortedSet(cacheKeyGenerator.keyActivitySortedSet(team));
                rSortedSet.addAsync(activity.getUserActivityId(), activity.getUserActivityId());
                rBatch.getAtomicLong(cacheKeyGenerator.keyActivityCountByTeam(team)).addAndGetAsync(1);
            });
            rBucket.setAsync(activity, 2, TimeUnit.DAYS);
            rBatch.execute();
        }
    }

    public void setActivity(UserActivity userActivity) {
        RBucket<UserActivity> rBucket = redissonClient
                .getBucket(cacheKeyGenerator.keyActivity(userActivity.getUserActivityId()));
        rBucket.set(userActivity, 2, TimeUnit.DAYS);
    }

    public void setActivities(List<UserActivity> userActivities) {
        RBatch rBatch = redissonClient.createBatch();
        userActivities.forEach(userActivity
                -> rBatch.getBucket(cacheKeyGenerator.keyActivity(userActivity.getUserActivityId()))
                        .setAsync(userActivity, 2, TimeUnit.DAYS));
        rBatch.execute();
    }

    public UserActivity getActivity(long activityId) {
        RBucket<UserActivity> rBucket = redissonClient
                .getBucket(cacheKeyGenerator.keyActivity(activityId));
        return rBucket.get();
    }

    public List<UserActivity> getActivities(List<Long> activityIds) {
        RBatch rBatch = redissonClient.createBatch();
        activityIds.forEach(
                activityId -> rBatch.getBucket(cacheKeyGenerator.keyActivity(activityId)).getAsync());
        return (List<UserActivity>) rBatch.execute().getResponses();
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

    public void setTeamStat(long teamId, TeamStatDTO teamStat) {
        RBucket<TeamStatDTO> rBucket = redissonClient
                .getBucket(cacheKeyGenerator.keyTeamStat(teamId));
        rBucket.set(teamStat, 7, TimeUnit.DAYS);
    }

    public TeamStatDTO getTeamStat(long teamId) {
        RBucket<TeamStatDTO> rBucket = redissonClient
                .getBucket(cacheKeyGenerator.keyTeamStat(teamId));
        return rBucket.get();
    }

    public void setTeamLeaderBoard(List<TeamLeaderBoardDTO> teamLeaderBoard) {
        RBucket<List<TeamLeaderBoardDTO>> rBucket = redissonClient
                .getBucket(cacheKeyGenerator.keyTeamLeaderBoard());
        rBucket.set(teamLeaderBoard, 7, TimeUnit.DAYS);
    }

    public List<TeamLeaderBoardDTO> getTeamLeaderBoard(long teamId) {
        RBucket<List<TeamLeaderBoardDTO>> rBucket = redissonClient
                .getBucket(cacheKeyGenerator.keyTeamLeaderBoard());
        return rBucket.get();
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

    public boolean acquireActivityLock(long userId, long time, long lockTime) {
        RLock rLock = redissonClient.getLock(cacheKeyGenerator.keyActivityLock(userId, time));
        if (rLock.isLocked()) {
            return false;
        }
        rLock.lock(lockTime, TimeUnit.MILLISECONDS);
        return true;
    }

    public void releaseActivityLock(long userId, long time) {
        RLock rLock = redissonClient.getLock(cacheKeyGenerator.keyActivityLock(userId, time));
        if (rLock.isLocked()) {
            rLock.unlock();
        }
    }
}

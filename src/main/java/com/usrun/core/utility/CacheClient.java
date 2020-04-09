package com.usrun.core.utility;

import com.usrun.core.model.Post;
import com.usrun.core.model.User;
import com.usrun.core.model.UserActivity;
import com.usrun.core.model.track.Track;
import com.usrun.core.model.type.TeamMemberType;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author phuctt4
 */

@Component
public class CacheClient {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private CacheKeyGenerator cacheKeyGenerator;

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
        if(userId == null) {
            return null;
        }
        String key = cacheKeyGenerator.keyUser(userId);
        RBucket<User> rUser = redissonClient.getBucket(key);
        User user = rUser.get();
        return user;
    }

    public Track setTrack(Track track) {
        RBucket<Track> rBucket = redissonClient.getBucket(cacheKeyGenerator.keyTrack(track.getTrackId()));
        rBucket.set(track, 1, TimeUnit.DAYS);
        return track;
    }

    public Track getTrack(Long trackId) {
        RBucket<Track> rBucket = redissonClient.getBucket(cacheKeyGenerator.keyTrack(trackId));
        Track track = rBucket.get();
        return track;
    }

    public void setTrackSig(Long trackId, String sig) {
        RBucket<Boolean> rBucket = redissonClient.getBucket(cacheKeyGenerator.keyTrackSig(trackId, sig));
        rBucket.set(true, 1, TimeUnit.HOURS);
    }

    public boolean getTrackSig(Long trackId, String sig) {
        RBucket<Boolean> rBucket = redissonClient.getBucket(cacheKeyGenerator.keyTrackSig(trackId, sig));
        return rBucket.get() == null ? false : true;
    }

    public TeamMemberType getTeamMemberType(long teamId, long userId) {
        RBucket<Integer> rBucket = redissonClient.getBucket(cacheKeyGenerator.keyTeamMemberType(teamId, userId));
        return rBucket.get() == null ? null : TeamMemberType.fromInt(rBucket.get());
    }

    public void setTeamMemberType(long teamId, long userId, TeamMemberType teamMemberType) {
        RBucket<Integer> rBucket = redissonClient.getBucket(cacheKeyGenerator.keyTeamMemberType(teamId, userId));
        rBucket.set(teamMemberType.toValue(), 14, TimeUnit.DAYS);
    }

    public void setActivity(User user, UserActivity activity) {
        RBucket<UserActivity> rBucket = redissonClient.getBucket(cacheKeyGenerator.keyActivity(activity.getUserActivityId()));
        user.getTeams().forEach(team -> {
            RScoredSortedSet<Long> rSortedSet = redissonClient.getScoredSortedSet(cacheKeyGenerator.keyActivitySortedSet(team));
            rSortedSet.add(activity.getUserActivityId(), activity.getUserActivityId());
        });
        rBucket.set(activity, 2, TimeUnit.DAYS);
    }

    public UserActivity getActivity(long activityId) {
        RBucket<UserActivity> rBucket = redissonClient.getBucket(cacheKeyGenerator.keyActivity(activityId));
        return rBucket.get();
    }

    public List<Long> getActivityByTeam(long teamId, int count, int offset) {
        RScoredSortedSet<Long> rSortedSet = redissonClient.getScoredSortedSet(cacheKeyGenerator.keyActivitySortedSet(teamId));
        int start = offset * count;
        int stop = (offset + 1) * count - 1;
        return rSortedSet.valueRangeReversed(start, stop).stream().collect(Collectors.toList());
    }
}

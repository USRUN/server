package com.usrun.core.utility;

import com.usrun.core.model.User;
import com.usrun.core.model.track.Track;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.util.Random;
import java.util.concurrent.TimeUnit;

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
        rUser.set(user);

        RBucket<Long> rUserEmail = redissonClient.getBucket(keyUserEmail);
        rUserEmail.set(user.getId());
        return user;
    }

    @Transactional
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
        rBucket.set(track);
        return track;
    }

    public Track getTrack(Long trackId) {
        RBucket<Track> rBucket = redissonClient.getBucket(cacheKeyGenerator.keyTrack(trackId));
        Track track = rBucket.get();
        return track;
    }
}

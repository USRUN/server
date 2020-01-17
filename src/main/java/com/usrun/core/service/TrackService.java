package com.usrun.core.service;

import com.usrun.core.config.ErrorCode;
import com.usrun.core.exception.TrackException;
import com.usrun.core.model.track.Point;
import com.usrun.core.model.track.Track;
import com.usrun.core.repository.PointRepository;
import com.usrun.core.repository.TrackRepository;
import com.usrun.core.utility.CacheKeyGenerator;
import com.usrun.core.utility.UniqueIDGenerator;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author phuctt4
 */

@Service
public class TrackService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrackService.class);

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private UniqueIDGenerator uniqueIDGenerator;

    @Autowired
    private CacheKeyGenerator cacheKeyGenerator;

    @Autowired
    private TrackRepository trackRepository;

    @Autowired
    private PointRepository pointRepository;

    public Track createTrack(Long userId, String description) {
        Long trackId = uniqueIDGenerator.generateTrackId(userId);
        Track track = new Track(trackId, userId, description);
        trackRepository.save(track);
        RBucket<Track> rBucket = redissonClient.getBucket(cacheKeyGenerator.keyTrack(trackId));
        rBucket.set(track);
        LOGGER.info(track.toString());
        return track;
    }

    public Point track(Long userId, Long trackId, Float latitude, Float longitude) {
        RBucket<Track> rBucket = redissonClient.getBucket(cacheKeyGenerator.keyTrack(trackId));
        Track track = rBucket.get();
        if(track == null) {
            String msg = "Track not found in cache: " + trackId;
            LOGGER.error(msg);
            throw new TrackException(msg, ErrorCode.TRACK_NOT_FOUND);
        } else {
            if(track.getUserId() == userId) {
                Point point = new Point(trackId, latitude, longitude);
                pointRepository.save(point);
                LOGGER.info("Save: {}", point.toString());
                return point;
            } else {
                String msg = "Track does not belong to " + userId;
                LOGGER.error(msg);
                throw new TrackException("Track does not belong to " + userId, ErrorCode.TRACK_NOT_BELONG_TO_USER);
            }
        }
    }
}

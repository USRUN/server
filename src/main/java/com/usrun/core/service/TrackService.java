package com.usrun.core.service;

import com.google.common.hash.Hashing;
import com.usrun.core.config.AppProperties;
import com.usrun.core.config.ErrorCode;
import com.usrun.core.exception.TrackException;
import com.usrun.core.model.track.Location;
import com.usrun.core.model.track.Point;
import com.usrun.core.model.track.Track;
import com.usrun.core.payload.dto.TrackDTO;
import com.usrun.core.repository.PointRepository;
import com.usrun.core.repository.TrackRepository;
import com.usrun.core.utility.CacheClient;
import com.usrun.core.utility.CacheKeyGenerator;
import com.usrun.core.utility.UniqueIDGenerator;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author phuctt4
 */

@Service
public class TrackService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrackService.class);

    @Autowired
    private UniqueIDGenerator uniqueIDGenerator;

    @Autowired
    private TrackRepository trackRepository;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private CacheClient cacheClient;

    @Autowired
    private AppProperties appProperties;

    public Track createTrack(Long userId, String description) {
        Long trackId = uniqueIDGenerator.generateTrackId(userId);
        Track track = new Track(trackId, userId, description);

        trackRepository.save(track);
        cacheClient.setTrack(track);

        LOGGER.info(track.toString());
        return track;
    }

    public List<Point> track(Long userId, Long trackId, List<Location> locations, Long time, String sig) {
        Long t = System.currentTimeMillis() - time;
        if (t > appProperties.getTrack().getTimeInMicroseconds()) {
            String msg = String.format("[%s] Track point has exceeded time: %s", trackId, t);
            LOGGER.error(msg);
            throw new TrackException(msg, ErrorCode.TRACK_TIMEOUT);
        }

        String hmac = getSigTrack(trackId, time);

        if(!hmac.equals(sig)) {
            String msg = String.format("[%s] Track signature invalid", trackId);
            LOGGER.error(msg);
            throw new TrackException(msg, ErrorCode.TRACK_SIG_INVALID);
        }

        if(!cacheClient.getTrackSig(trackId, sig)) {
            cacheClient.setTrackSig(trackId, sig);
        } else {
            String msg = String.format("[%s] This track existed", trackId);
            LOGGER.error(msg);
            throw new TrackException(msg, ErrorCode.TRACK_SIG_INVALID);
        }

        Track track = cacheClient.getTrack(trackId);
        if (track == null) {
            String msg = String.format("[%s] Track not found in cache", trackId);
            LOGGER.error(msg);
            throw new TrackException(msg, ErrorCode.TRACK_NOT_FOUND);
        } else {
            if (track.getUserId() == userId) {
                List<Point> points = locations
                        .stream()
                        .map(location -> new Point(trackId, location.getLatitude(), location.getLongitude(), location.getTime()))
                        .collect(Collectors.toList());
                pointRepository.saveAll(points);
                LOGGER.info("Save: {}", points.toString());
                return points;
            } else {
                String msg = String.format("[%s] Track does not belong to %s", trackId, userId);
                LOGGER.error(msg);
                throw new TrackException(msg, ErrorCode.TRACK_NOT_BELONG_TO_USER);
            }
        }
    }

    private String getSigTrack(Long trackId, Long time) {
        StringBuffer buffer = new StringBuffer(Long.toString(trackId));
        buffer.append("|");
        buffer.append(time);
        return Hashing
                .hmacSha256(appProperties.getTrack().getKey().getBytes())
                .hashString(buffer.toString(), StandardCharsets.UTF_8)
                .toString();
    }

    public TrackDTO getTrack(Long userId, Long trackId) {
        Track track = trackRepository.findById(trackId).orElse(null);
        if(track == null) {
            String msg = String.format("[%s] Track not found", trackId);
            LOGGER.error(msg);
            throw new TrackException(msg, ErrorCode.TRACK_NOT_FOUND);
        } else {
            if(track.getUserId() == userId) {
                List<Point> points = pointRepository.findAllByTrackId(trackId);
                LOGGER.info("[{}] Get Track" + trackId);
                return new TrackDTO(track, points);
            } else {
                String msg = String.format("[%s] Track does not belong to %s", trackId, userId);
                LOGGER.error(msg);
                throw new TrackException(msg, ErrorCode.TRACK_NOT_BELONG_TO_USER);
            }
        }
    }
}

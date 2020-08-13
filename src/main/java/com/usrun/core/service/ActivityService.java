package com.usrun.core.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.hash.Hashing;
import com.usrun.core.config.AppProperties;
import com.usrun.core.config.ErrorCode;
import com.usrun.core.exception.CodeException;
import com.usrun.core.model.Event;
import com.usrun.core.model.Love;
import com.usrun.core.model.Team;
import com.usrun.core.model.User;
import com.usrun.core.model.UserActivity;
import com.usrun.core.payload.activity.UserFeedResp;
import com.usrun.core.payload.activity.UserStatResp;
import com.usrun.core.payload.dto.SplitPaceDTO;
import com.usrun.core.payload.dto.TeamActivityCountDTO;
import com.usrun.core.payload.user.CreateActivityRequest;
import com.usrun.core.repository.EventParticipantRepository;
import com.usrun.core.repository.EventRepository;
import com.usrun.core.repository.LoveRepository;
import com.usrun.core.repository.TeamRepository;
import com.usrun.core.repository.UserActivityRepository;
import com.usrun.core.repository.UserRepository;
import com.usrun.core.utility.CacheClient;
import com.usrun.core.utility.ObjectUtils;
import com.usrun.core.utility.SequenceGenerator;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ActivityService {

    public static final Logger LOGGER = LoggerFactory.getLogger(ActivityService.class);

    @Autowired
    private UserActivityRepository userActivityRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventParticipantRepository eventParticipantRepository;

    @Autowired
    private CacheClient cacheClient;

    @Autowired
    private AppProperties appProperties;

    @Autowired
    private AmazonClient amazonClient;

    @Autowired
    private TrackService trackService;

    @Autowired
    private EventService eventService;

    @Autowired
    private LoveRepository loveRepository;

    @Autowired
    private SequenceGenerator sequenceGenerator;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private GoogleMapService googleMapService;
    public final String IMAGE_DEFAULT = "https://s3-ap-southeast-1.amazonaws.com/usrun-photo/activity-1ae77e32-65c9-4dfe-b34a-1e965726b6b7";

    public String getSigActivity(long userId, long time) {
        StringBuffer buffer = new StringBuffer(Long.toString(userId));
        buffer.append("|");
        buffer.append(time);
        return Hashing
                .hmacSha256(appProperties.getActivity().getKey().getBytes())
                .hashString(buffer.toString(), StandardCharsets.UTF_8)
                .toString();
    }

    public UserActivity loadActivity(long activityId) {
        UserActivity activity = cacheClient.getActivity(activityId);
        long loveCount = cacheClient.getLoveCount(activityId);

        if (activity == null || loveCount == -1) {
            activity = userActivityRepository.findById(activityId);
            loveCount = loveRepository.countLove(activityId);
            activity.setTotalLove(loveCount);
            if (activity == null) {
                throw new CodeException(ErrorCode.ACTIVITY_NOT_FOUND);
            } else {
                cacheClient.setActivity(activity);
                cacheClient.setLoveCount(activityId, loveCount);
            }
        }
        return activity;
    }

    @Transactional
    public boolean loveActivity(long userId, long activityId, boolean isLove) {
        int count = isLove ? 1 : -1;
        Love love = new Love(activityId, userId);
        boolean affected;
        if (isLove) {
            affected = loveRepository.insert(love) != null;
        } else {
            affected = loveRepository.delete(love);
        }
        if (affected) {
            if (!cacheClient.updateLoveCount(activityId, count)) {
                cacheClient.setLoveCount(activityId, loveRepository.countLove(activityId));
            }
        }
        return affected;
    }

    public List<UserActivity> loadActivities(List<Long> activityIds) {
        List<UserActivity> userActivities = cacheClient.getActivities(activityIds);
        List<Long> loveCounts = cacheClient.getLoveCounts(activityIds);

        Iterator<Long> activityIdIterator = activityIds.iterator();
        Iterator<UserActivity> userActivityIterator = userActivities.iterator();
        Iterator<Long> loveCountIterator = loveCounts.iterator();

        List<UserActivity> rs = new ArrayList<>();
        List<Long> activitiesNeedQueryIds = new ArrayList<>();

        while (activityIdIterator.hasNext()) {
            Long activityId = activityIdIterator.next();
            Long loveCount = loveCountIterator.next();
            UserActivity userActivity = userActivityIterator.next();
            if (userActivity == null || loveCount == null) {
                activitiesNeedQueryIds.add(activityId);
            } else {
                userActivity.setTotalLove(loveCount);
                rs.add(userActivity);
            }
        }

        if (!activitiesNeedQueryIds.isEmpty()) {
            List<UserActivity> activitiesNeedQuery = userActivityRepository
                    .findByIds(activitiesNeedQueryIds);
            List<Long> loveCountsNeedQuery = loveRepository.countLoves(activitiesNeedQueryIds);
            Iterator<UserActivity> activitiesNeedQueryIterator = activitiesNeedQuery.iterator();
            Iterator<Long> loveCountsNeedQueryIterator = loveCountsNeedQuery.iterator();
            while (activitiesNeedQueryIterator.hasNext()) {
                UserActivity userActivity = activitiesNeedQueryIterator.next();
                Long loveCount = loveCountsNeedQueryIterator.next();
                userActivity.setTotalLove(loveCount);
            }
            cacheClient.setActivities(activitiesNeedQuery);
            cacheClient.setLoveCounts(activitiesNeedQuery);
            rs.addAll(activitiesNeedQuery);
            rs.sort((a, b) -> Long.compare(b.getUserActivityId(), a.getUserActivityId()));
        }

        return rs;
    }

    public List<UserActivity> getActivitiesByTeam(long teamId, int count, int offset) {
        List<?> rs = cacheClient.getActivitiesByTeam(teamId, count, offset);
        int start = offset * count;
        int stop = (offset + 1) * count;

        int countActivities = ((Long) rs.get(0)).intValue();
        int countActivitiesSortedSet = (Integer) rs.get(1);
        List<Long> activities = ObjectUtils.fromJsonString(String.valueOf(rs.get(2)),
                new TypeReference<List<Long>>() {
                });

        if (countActivitiesSortedSet >= stop || countActivities == countActivitiesSortedSet) {
            return loadActivities(activities);
        } else {
            if (countActivities < stop && countActivities > countActivitiesSortedSet) {
                stop = countActivities;
            }
            List<Long> userActivityIds = userActivityRepository.findByTeamId(teamId, stop);
            cacheClient.setActivitiesByTeam(teamId, userActivityIds);
            return loadActivities(userActivityIds.subList(start, stop));
        }
    }

    public UserActivity createUserActivity(long creatorId,
                                           CreateActivityRequest request) {
        List<String> photosBase64 = request.getPhotosBase64();
        List<String> photos = new ArrayList<>();
        try {
            String photoTrack = googleMapService.getRouteImage(request.getTrackRequest().getLocations());
            photos.add(photoTrack);
        } catch (IOException ex) {
            LOGGER.error("fail to parse image track: " + ex.getMessage());
        }
        for (String photoBase64 : photosBase64) {
            if (photoBase64.length() <= appProperties.getMaxImageSize()) {
                String fileUrl = amazonClient
                        .uploadFile(photoBase64, "activity-" + UUID.randomUUID().toString());
                if (fileUrl != null) {
                    photos.add(fileUrl);
                }
            } else {
                throw new CodeException(ErrorCode.INVALID_IMAGE_SIZE);
            }
        }
        long activityId = sequenceGenerator.nextId();
        UserActivity toCreate = new UserActivity(request, activityId, new Date(), photos);
        toCreate.setUserId(creatorId);
        eventService.updateDistance(creatorId, request.getEventId(), request.getTotalDistance());
        toCreate = userActivityRepository.insert(toCreate);
        LOGGER.info("User activity created for userID [{}] with ID: {}", toCreate.getUserId(),
                toCreate.getUserActivityId());
        return toCreate;
    }

    public void setCountAllActivityByTeam() {
        List<Team> teams = teamRepository.findAllTeam();
        List<TeamActivityCountDTO> dtos = teams.parallelStream()
                .map(team -> {
                    long teamId = team.getId();
                    long count = userActivityRepository.countUserActivityByUser(teamId);
                    return new TeamActivityCountDTO(teamId, count);
                }).collect(Collectors.toList());
        cacheClient.setCountAllActivityByTeam(dtos);
    }

    public UserStatResp getUserStat(long userId, Date startTime, Date endTime) {
        List<UserActivity> listActivity = userActivityRepository
                .findAllByTimeRangeAndUserId(userId, startTime, endTime);
        UserStatResp result = new UserStatResp();
        result.setNumberActivity(listActivity.size());
        int avgTime = 0;
        int avgPace = 0;
        int avgheart = 0;
        int avgElev = 0;
        for (UserActivity userActivity : listActivity) {
            if (userActivity.getCalories() > 0) {
                result.setTotalCal(result.getTotalCal() + userActivity.getCalories());
            }
            if (userActivity.getElevGain() > 0) {
                result.setAvgElev(result.getAvgElev() + userActivity.getElevGain());
                avgElev++;
            }
            if (userActivity.getAvgPace() > 0) {
                result.setAvgPace(result.getAvgPace() + (long) userActivity.getAvgPace());
                avgPace++;
            }
            if (userActivity.getAvgHeart() > 0) {
                result.setAvgHeart(result.getAvgHeart() + userActivity.getAvgHeart());
                avgheart++;
            }
            if (userActivity.getElevMax() > 0) {
                result.setMaxElev((long) Math.max(result.getMaxElev(), userActivity.getElevMax()));
            }
            if (userActivity.getTotalDistance() > 0) {
                result.setTotalDistance(result.getTotalDistance() + userActivity.getTotalDistance());
            }
            if (userActivity.getTotalStep() > 0) {
                result.setTotalStep(result.getTotalStep() + userActivity.getTotalStep());
            }
            if (userActivity.getTotalTime() > 0) {
                result.setAvgTime(result.getAvgTime() + userActivity.getTotalTime());
                result.setTotalTime(result.getTotalTime() + userActivity.getTotalTime());
                avgTime++;
            }
        }
        if (avgElev != 0) {
            result.setAvgElev(result.getAvgElev() / avgElev);
        }
        if (avgPace != 0) {
            result.setAvgPace(result.getAvgPace() / avgPace);
        }
        if (avgTime != 0) {
            result.setAvgTime(result.getAvgTime() / avgTime);
        }
        if (avgheart != 0) {
            result.setAvgHeart(result.getAvgHeart() / avgheart);
        }
        return result;
    }

    public List<UserFeedResp> getUserFeed(long userId, int offset, int limit) {
        List<UserActivity> userActivites = userActivityRepository.findAllByUserId(userId, offset, limit);
        User user = userRepository.findById(userId);

        List<Long> eventIds = userActivites.stream().map(item -> item.getEventId()).collect(Collectors.toList());
        List<Event> events = eventRepository.mFindById(eventIds);
        List<UserFeedResp> resp = new ArrayList<>();
        for (int i = 0; i < userActivites.size(); i++) {
            UserActivity item = userActivites.get(i);
            Optional<Event> e = events.stream().filter(event -> event.getEventId() == item.getEventId()).findFirst();
            if (!item.isShowMap()) {
                List<String> photos = item.getPhotos();
                if (photos.isEmpty()) {
                    photos.add(IMAGE_DEFAULT);
                } else {
                    photos.set(0, IMAGE_DEFAULT);
                }
            }

            String splitData = item.getSplitPace();
            Map<String, Object> result = new HashMap<>();
            if (splitData != null) {
                result = ObjectUtils.fromJsonString(splitData, new TypeReference<HashMap<String, Object>>() {
                });
            }
            List<SplitPaceDTO> splitResp = new ArrayList<>();
            for (Map.Entry<String, Object> entry : result.entrySet()) {
                Double km = Double.valueOf(entry.getKey());
                int pace = (int) entry.getValue();
                SplitPaceDTO itemSplit = new SplitPaceDTO(km, pace);
                splitResp.add(itemSplit);
            }

            UserFeedResp itemUserFeed = new UserFeedResp(item.getUserActivityId(),
                    item.getUserId(),
                    user.getName(),
                    user.getAvatar(),
                    user.isHcmus(),
                    item.getEventId(),
                    e.isPresent() ? e.get().getEventName() : "",
                    e.isPresent() ? e.get().getThumbnail() : "",
                    item.getCreateTime(),
                    item.getTotalDistance(),
                    item.getTotalTime(),
                    item.getTotalStep(),
                    item.getAvgPace(),
                    item.getAvgHeart(),
                    item.getMaxHeart(),
                    item.getCalories(),
                    item.getElevGain(),
                    item.getElevMax(),
                    item.getPhotos(),
                    item.getTitle(),
                    item.getDescription(),
                    item.getTotalLove(),
                    item.getTotalComment(),
                    item.getTotalShare(),
                    splitResp);
            resp.add(itemUserFeed);
        }
        return resp;
    }


    public UserActivity updateActivity(long activityId, long userId, String title, String description, List<String> photos, boolean isShowMap) {
        UserActivity toUpdate = userActivityRepository.findById(activityId);

        if (toUpdate == null) {
            throw new CodeException(ErrorCode.ACTIVITY_NOT_FOUND);
        }

        if (toUpdate.getUserId() != userId) {
            throw new CodeException(ErrorCode.ACTIVITY_DOESNT_BELONG_TO_USER);
        }

        if (title != null && !title.isEmpty()) {
            toUpdate.setTitle(title);
        }

        if (description != null && !description.isEmpty()) {
            toUpdate.setDescription(description);
        }

        if (photos != null) {
            for (int i = 0; i < photos.size(); i++) {
                if (!photos.get(i).startsWith("http")) {
                    if (photos.get(i).length() <= appProperties.getMaxImageSize()) {
                        String fileUrl = amazonClient
                                .uploadFile(photos.get(i), "activity-" + UUID.randomUUID().toString());
                        if (fileUrl != null) {
                            photos.add(fileUrl);
                        }
                    } else {
                        throw new CodeException(ErrorCode.INVALID_IMAGE_SIZE);
                    }
                }
            }
            toUpdate.setPhotos(photos);
        }

        toUpdate.setShowMap(isShowMap);

        UserActivity updated = userActivityRepository.update(toUpdate);
        LOGGER.info("Update activity {}", activityId);

        return updated;
    }

    public boolean deleteActivity(long activityId, long userId){
        UserActivity toUpdate = userActivityRepository.findById(activityId);

        if (toUpdate == null) {
            throw new CodeException(ErrorCode.ACTIVITY_NOT_FOUND);
        }

        if (toUpdate.getUserId() != userId) {
            throw new CodeException(ErrorCode.ACTIVITY_DOESNT_BELONG_TO_USER);
        }

        if(userActivityRepository.delete(activityId)){
            return true;
        } else {
            throw new CodeException(ErrorCode.ACTIVITY_DELETE_FAIL);
        }
    }
}

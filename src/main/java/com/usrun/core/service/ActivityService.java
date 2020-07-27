package com.usrun.core.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.hash.Hashing;
import com.usrun.core.config.AppProperties;
import com.usrun.core.config.ErrorCode;
import com.usrun.core.exception.CodeException;
import com.usrun.core.model.Love;
import com.usrun.core.model.Team;
import com.usrun.core.model.UserActivity;
import com.usrun.core.model.track.Track;
import com.usrun.core.payload.activity.UserStatResp;
import com.usrun.core.payload.dto.TeamActivityCountDTO;
import com.usrun.core.payload.user.CreateActivityRequest;
import com.usrun.core.repository.LoveRepository;
import com.usrun.core.repository.TeamRepository;
import com.usrun.core.repository.UserActivityRepository;
import com.usrun.core.utility.CacheClient;
import com.usrun.core.utility.ObjectUtils;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
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
  private CacheClient cacheClient;

  @Autowired
  private AppProperties appProperties;

  @Autowired
  private AmazonClient amazonClient;

  @Autowired
  private TrackService trackService;

  @Autowired
  private LoveRepository loveRepository;

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

    Track track = trackService
        .createTrack(creatorId, request.getDescription(), request.getTrackRequest().getLocations(),
            request.getTrackRequest().getSplitDistance());
    UserActivity toCreate = new UserActivity(request, track.getTrackId(), track.getTime(), photos);
    toCreate.setUserId(creatorId);

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
        result.setAvgPace(result.getAvgPace() + (long)userActivity.getAvgPace());
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

}

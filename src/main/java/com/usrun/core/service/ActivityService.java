package com.usrun.core.service;

import com.google.common.hash.Hashing;
import com.usrun.core.config.AppProperties;
import com.usrun.core.config.ErrorCode;
import com.usrun.core.exception.CodeException;
import com.usrun.core.model.UserActivity;
import com.usrun.core.payload.user.CreateActivityRequest;
import com.usrun.core.repository.TeamRepository;
import com.usrun.core.repository.UserActivityRepository;
import com.usrun.core.utility.CacheClient;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    if (activity == null) {
      activity = userActivityRepository.findById(activityId);
      if (activity == null) {
        throw new CodeException(ErrorCode.ACTIVITY_NOT_FOUND);
      } else {
        cacheClient.setActivity(activity);
      }
    }
    return activity;
  }

  public List<UserActivity> loadActivities(List<Long> activityIds) {
    List<UserActivity> userActivities = cacheClient.getActivities(activityIds);

    Iterator<Long> activityIdIterator = activityIds.iterator();
    Iterator<UserActivity> userActivityIterator = userActivities.iterator();

    List<UserActivity> rs = new ArrayList<>();
    List<Long> activitiesNeedQueryIds = new ArrayList<>();

    while (activityIdIterator.hasNext()) {
      Long activityId = activityIdIterator.next();
      UserActivity userActivity = userActivityIterator.next();
      if (userActivity == null) {
        activitiesNeedQueryIds.add(activityId);
      } else {
        rs.add(userActivity);
      }
    }

    if (!activitiesNeedQueryIds.isEmpty()) {
      List<UserActivity> activitiesNeedQuery = userActivityRepository
          .findByIds(activitiesNeedQueryIds);
      cacheClient.setActivities(activitiesNeedQuery);
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
    List<Long> activities = (List<Long>) rs.get(2);

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
      CreateActivityRequest createActivityRequest, long trackId, Date createTime) {
    List<String> photosBase64 = createActivityRequest.getPhotosBase64();
    List<String> photos = new ArrayList<>();
    int count = 1;
    for(String photoBase64 : photosBase64) {
      String fileUrl = amazonClient.uploadFile(photoBase64, "activity-" + trackId + "-" + 1);
      if(fileUrl != null) {
        photos.add(fileUrl);
      }
      count++;
    }
    UserActivity toCreate = new UserActivity(createActivityRequest, trackId, createTime, photos);
    toCreate.setUserId(creatorId);

    toCreate = userActivityRepository.insert(toCreate);
    LOGGER.info("User activity created for userID [{}] with ID: {}", toCreate.getUserId(),
        toCreate.getUserActivityId());
    return toCreate;
  }

  public void setCountAllActivityByTeam() {
//        List<Team> teams = teamRepository.findAllTeam();
//        List<TeamActivityCountDTO> dtos = teams.parallelStream()
//                .map(team -> {
//                    long teamId = team.getId();
//                    long count = userActivityRepository.countUserActivityByUser(teamId);
//                    return new TeamActivityCountDTO(teamId, count);
//                }).collect(Collectors.toList());
//        cacheClient.setCountAllActivityByTeam(dtos);
  }


}

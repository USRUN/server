package com.usrun.core.service;

import com.google.common.hash.Hashing;
import com.usrun.core.config.AppProperties;
import com.usrun.core.config.ErrorCode;
import com.usrun.core.exception.CodeException;
import com.usrun.core.exception.PostException;
import com.usrun.core.model.Post;
import com.usrun.core.model.Team;
import com.usrun.core.model.User;
import com.usrun.core.model.UserActivity;
import com.usrun.core.payload.dto.TeamActivityCountDTO;
import com.usrun.core.repository.*;
import com.usrun.core.utility.CacheClient;
import com.usrun.core.utility.SequenceGenerator;
import com.usrun.core.utility.UniqueIDGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ActivityService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrackService.class);


    @Autowired
    private UserService userService;

    @Autowired
    private UserActivityRepository userActivityRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private CacheClient cacheClient;

    @Autowired
    private AmazonClient amazonClient;

    @Autowired
    private AppProperties appProperties;

    public String getSigActivity(Long recordId) {
        StringBuffer buffer = new StringBuffer(Long.toString(recordId));
        return Hashing
                .hmacSha256(appProperties.getActivity().getKey().getBytes())
                .hashString(buffer.toString(), StandardCharsets.UTF_8)
                .toString();
    }

    public UserActivity loadActivity(long activityId) {
        UserActivity activity = cacheClient.getActivity(activityId);

        if(activity == null) {
            activity = userActivityRepository.findById(activityId);
            if(activity == null) {
                throw new CodeException(ErrorCode.ACTIVITY_NOT_FOUND);
            } else {
                User user = userService.loadUser(activity.getUserId());
                cacheClient.setActivity(user, activity);
            }
        }
        return activity;
    }

    public List<UserActivity> loadActivities(List<Long> activityIds) {
        List<UserActivity> userActivities = cacheClient.getActivities(activityIds);

        Iterator<Long> activityIdIterator = activityIds.iterator();
        Iterator<UserActivity> userActivityIterator = userActivities.iterator();
        List<UserActivity> rs = new ArrayList<>();
        while(activityIdIterator.hasNext()) {
            Long activityId = activityIdIterator.next();
            UserActivity userActivity = userActivityIterator.next();
            if(userActivity == null) {
                userActivity = userActivityRepository.findById(activityId);
                if(userActivity != null) {
                    rs.add(userActivity);
                    User user = userService.loadUser(userActivity.getUserId());
                    cacheClient.setActivity(user, userActivity);
                }
            } else rs.add(userActivity);
        }

        return rs;
    }

    public List<UserActivity> getActivitiesByTeam(long teamId, int count, int offset) {
        List<?> rs = cacheClient.getActivitiesByTeam(teamId, count , offset);
        int start = offset * count;
        int stop = (offset + 1) * count;

        int countActivities = ((Long) rs.get(0)).intValue();
        int countActivitiesSortedSet = (Integer) rs.get(1);
        List<Long> activities = (List<Long>)rs.get(2);

        if(countActivitiesSortedSet >= stop || countActivities == countActivitiesSortedSet) {
            return loadActivities(activities);
        } else {
            if(countActivities < stop && countActivities > countActivitiesSortedSet) {
                stop = countActivities;
            }
            List<Long> userActivityIds = userActivityRepository.findByTeamId(teamId, stop);
            cacheClient.setActivitiesByTeam(teamId, userActivityIds);
            return loadActivities(userActivityIds.subList(start, stop));
        }
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



}

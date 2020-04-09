package com.usrun.core.service;

import com.google.common.hash.Hashing;
import com.usrun.core.config.AppProperties;
import com.usrun.core.config.ErrorCode;
import com.usrun.core.exception.CodeException;
import com.usrun.core.exception.PostException;
import com.usrun.core.model.Post;
import com.usrun.core.model.User;
import com.usrun.core.model.UserActivity;
import com.usrun.core.repository.PointRepository;
import com.usrun.core.repository.PostRepository;
import com.usrun.core.repository.TrackRepository;
import com.usrun.core.repository.UserActivityRepository;
import com.usrun.core.utility.CacheClient;
import com.usrun.core.utility.SequenceGenerator;
import com.usrun.core.utility.UniqueIDGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
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

    public List<UserActivity> getActivitiesByTeam(long teamId, int count, int offset) {
        List<Long> postIds = cacheClient.getActivityByTeam(teamId, count , offset);
        return postIds.stream().map(id -> loadActivity(id)).collect(Collectors.toList());
    }

}

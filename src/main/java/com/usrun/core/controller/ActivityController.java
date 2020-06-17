/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.usrun.core.controller;

import com.usrun.core.config.ErrorCode;
import com.usrun.core.exception.CodeException;
import com.usrun.core.exception.TrackException;
import com.usrun.core.model.Love;
import com.usrun.core.model.Post;
import com.usrun.core.model.User;
import com.usrun.core.model.UserActivity;
import com.usrun.core.model.track.Location;
import com.usrun.core.model.track.Point;
import com.usrun.core.model.track.Track;
import com.usrun.core.payload.CodeResponse;
import com.usrun.core.payload.activity.ConditionRequest;
import com.usrun.core.payload.activity.LoveRequest;
import com.usrun.core.payload.user.GetActivityRequest;
import com.usrun.core.payload.user.GetActivitiesRequest;
import com.usrun.core.payload.user.CreateActivityRequest;
import com.usrun.core.payload.user.NumberActivityRequest;
import com.usrun.core.payload.user.TimeRequest;
import com.usrun.core.repository.LoveRepository;
import com.usrun.core.repository.UserActivityRepository;
import com.usrun.core.security.CurrentUser;
import com.usrun.core.security.UserPrincipal;
import com.usrun.core.service.ActivityService;
import com.usrun.core.service.PostService;
import com.usrun.core.service.TrackService;
import com.usrun.core.service.UserService;
import com.usrun.core.utility.CacheClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Time;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author anhhuy
 */
@RestController
@RequestMapping("/activity")
public class ActivityController {
    @Autowired
    private UserActivityRepository userActivityRepository;

    @Autowired
    private LoveRepository loveRepository;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private PostService postService;

    @Autowired
    private CacheClient cacheClient;

    @Autowired
    private TrackService trackService;

    @Autowired
    private UserService userService;

    @PostMapping("/getUserActivityByTimeWithSum")
    public ResponseEntity<?> getUserActivityByTimeWithSum(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestBody TimeRequest timeRequest
    ) {
        Long userId = userPrincipal.getId();
        List<UserActivity> allByTimeRangeAndUserId = userActivityRepository.findAllByTimeRangeAndUserId(userId, timeRequest.getFromTime(), timeRequest.getToTime());
        UserActivity valueSum = new UserActivity();
        for (UserActivity item : allByTimeRangeAndUserId) {
            valueSum.setTotalDistance(item.getTotalDistance() + valueSum.getTotalDistance());
            valueSum.setTotalTime(item.getTotalTime() + valueSum.getTotalTime());
            valueSum.setTotalStep(item.getTotalStep() + valueSum.getTotalStep());
            valueSum.setAvgPace(item.getAvgPace() + valueSum.getAvgPace());
            valueSum.setAvgHeart(item.getAvgHeart() + valueSum.getAvgHeart());
            valueSum.setMaxHeart(Math.max(item.getMaxHeart(), valueSum.getMaxHeart()));
            valueSum.setCalories(item.getCalories() + valueSum.getCalories());
            valueSum.setElevGain(item.getElevGain() + valueSum.getElevGain());
            valueSum.setElevMax(Math.max(item.getElevMax(), valueSum.getElevMax()));
            valueSum.setTotalLove(item.getTotalLove() + valueSum.getTotalLove());
            valueSum.setTotalComment(item.getTotalComment() + valueSum.getTotalComment());
            valueSum.setTotalShare(item.getTotalShare() + valueSum.getTotalShare());
        }
        valueSum.setUserId(userId);
        valueSum.setAvgPace(valueSum.getAvgPace() / allByTimeRangeAndUserId.size());
        valueSum.setAvgHeart(valueSum.getAvgHeart() / allByTimeRangeAndUserId.size());

        return new ResponseEntity<>(new CodeResponse(valueSum), HttpStatus.OK);
    }

    @PostMapping("/createUserActivity")
    public ResponseEntity<?> createUserActivity(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestBody CreateActivityRequest paramActivity
    ) {
        Long userId = userPrincipal.getId();
        String sig = paramActivity.getSig();
        String sigActivity = activityService.getSigActivity(userId);
        try {
//            if (sig.equals(sigActivity)) {
                List<List<Location>> locations = paramActivity.getTrackRequest().getLocations();
                Track track = trackService.createTrack(userId, "",locations);

//                locations.forEach(item -> {
//                    trackService.track(userId,
//                            track.getTrackId(),
//                            item,
//                            paramActivity.getTrackRequest().getTime());
//                });


                UserActivity userActivity = new UserActivity(paramActivity, track.getTrackId(), track.getTime());
                userActivity.setUserId(userId);
                UserActivity result = userActivityRepository.insert(userActivity);
                User user = userService.loadUser(userId);
                cacheClient.setActivityCreated(user, result);
                return new ResponseEntity<>(new CodeResponse(result), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new CodeResponse(ErrorCode.ACTIVITY_ADD_FAIL), HttpStatus.BAD_REQUEST);
            }
        } catch (TrackException exp) {
            return new ResponseEntity<>(new CodeResponse(exp.getErrorCode()), HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping("/getUserActivityByTime")
    public ResponseEntity<?> getUserActivityByTime(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestBody TimeRequest timeRequest
    ) {
        Long userId = userPrincipal.getId();
        List<UserActivity> allByTimeRangeAndUserId = userActivityRepository.findAllByTimeRangeAndUserId(userId, timeRequest.getFromTime(), timeRequest.getToTime());
        return new ResponseEntity<>(new CodeResponse(allByTimeRangeAndUserId), HttpStatus.OK);
    }

    @PostMapping("/getUserActivityByTimeWithCondition")
    public ResponseEntity<?> getUserActivityByTimeWithCondition(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestBody ConditionRequest conditionRequest
    ) {
        Long userId = userPrincipal.getId();
        List<UserActivity> allByTimeRangeAndUserId = userActivityRepository.findAllByTimeRangeAndUserIdWithCondition(userId, conditionRequest.getFromTime(), conditionRequest.getToTime(), conditionRequest.getDistance(), conditionRequest.getPace(), conditionRequest.getElevation());
        return new ResponseEntity<>(new CodeResponse(allByTimeRangeAndUserId), HttpStatus.OK);
    }

    @PostMapping("/getNumberLastUserActivity")
    public ResponseEntity<?> getNumberLastUserActivity(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestBody NumberActivityRequest numberActivityRequest
    ) {
        Long userId = userPrincipal.getId();
        Pageable pageable = PageRequest.of(numberActivityRequest.getOffset(), numberActivityRequest.getSize());
        List<UserActivity> allByTimeRangeAndUserId = userActivityRepository.findNumberActivityLast(userId, pageable);
        return new ResponseEntity<>(new CodeResponse(allByTimeRangeAndUserId), HttpStatus.OK);

    }

    @PostMapping("/getActivity")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getActivity(
            @RequestBody GetActivityRequest request
    ) {
        try {
            UserActivity userActivity = activityService.loadActivity(request.getActivityId());
            return ResponseEntity.ok(new CodeResponse(userActivity));
        } catch (CodeException ex) {
            return new ResponseEntity<>(new CodeResponse(ex.getErrorCode()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/getActivitiesByTeam")
    @PreAuthorize("hasRole('USER') && @teamAuthorization.authorize(authentication, 'MEMBER', #request.teamId)")
    public ResponseEntity<?> getActivitiesByTeam(
            @RequestBody GetActivitiesRequest request
    ) {
        List<UserActivity> activities = activityService.getActivitiesByTeam(request.getTeamId(), request.getCount(), request.getOffset());
        return ResponseEntity.ok(new CodeResponse(activities));
    }

    @PostMapping("/loveActivity")
    public ResponseEntity<?> loveActivity(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestBody LoveRequest request
    ) {
        Long userId = userPrincipal.getId();
        Love loveObject = new Love(request.getActivityId(), userId);
        Love insert = loveRepository.insert(loveObject);
        return new ResponseEntity<>(new CodeResponse(insert), HttpStatus.OK);
    }

    @PostMapping("/getUserLoveActivity")
    public ResponseEntity<?> getUserLoveActivity(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestBody LoveRequest request
    ) {
        List<Long> numberLoveOfActivity = loveRepository.getNumberLoveOfActivity(request.getActivityId());
        return new ResponseEntity<>(new CodeResponse(numberLoveOfActivity), HttpStatus.OK);
    }

    @PostMapping("/isLoveActivity")
    public ResponseEntity<?> isLoveActivity(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestBody LoveRequest request
    ) {
        Long userId = userPrincipal.getId();
        boolean userLoveActivity = loveRepository.isUserLoveActivity(userId, request.getActivityId());
        return new ResponseEntity<>(new CodeResponse(userLoveActivity), HttpStatus.OK);
    }

    @PostMapping("/removeLoveActivity")
    public ResponseEntity<?> removeLoveActivity(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestBody LoveRequest request
    ) {
        Long userId = userPrincipal.getId();
        Love loveObject = new Love(request.getActivityId(), userId);
        boolean remove = loveRepository.delete(loveObject);
        return new ResponseEntity<>(new CodeResponse(remove), HttpStatus.OK);
    }
}

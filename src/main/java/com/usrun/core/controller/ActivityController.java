/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.usrun.core.controller;

import com.usrun.core.config.AppProperties;
import com.usrun.core.config.ErrorCode;
import com.usrun.core.dao.ActivityDAO;
import com.usrun.core.model.User;
import com.usrun.core.model.UserActivity;
import com.usrun.core.payload.CodeResponse;
import com.usrun.core.payload.user.CreateActivityRequest;
import com.usrun.core.payload.user.NumberActivityRequest;
import com.usrun.core.payload.user.TimeRequest;
import com.usrun.core.repository.UserActivityRepository;
import com.usrun.core.security.CurrentUser;
import com.usrun.core.security.UserPrincipal;
import com.usrun.core.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.sql.Time;
import java.util.Date;
import java.util.List;

/**
 * @author anhhuy
 */
@Controller
public class ActivityController {
    @Autowired
    private UserActivityRepository userActivityRepository;

    @Autowired
    private ActivityService activityService;

    @RequestMapping("/getUserActivityByTimeWithSum")
    public ResponseEntity<?> getUserActivityByTimeWithSum(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestBody TimeRequest timeRequest
    ) {
        Long userId = userPrincipal.getId();
        List<UserActivity> allByTimeRangeAndUserId = userActivityRepository.findAllByTimeRangeAndUserId(userId, timeRequest.getFromTime(), timeRequest.getToTime());
        UserActivity valueSum = new UserActivity();
        for (UserActivity item : allByTimeRangeAndUserId) {
            valueSum.setTotalDistance(item.getTotalDistance() + valueSum.getTotalDistance());
            valueSum.setTotalTime(new Time(item.getTotalTime().getTime() + valueSum.getTotalTime().getTime()));
            valueSum.setTotalStep(item.getTotalStep() + valueSum.getTotalStep());
            valueSum.setAvgPace(item.getAvgPace() + valueSum.getAvgPace());
            valueSum.setAvgHeart(item.getAvgHeart() + valueSum.getAvgHeart());
            valueSum.setMaxHeart(Math.max(item.getMaxHeart(), valueSum.getMaxHeart()));
            valueSum.setCalories(item.getCalories() + valueSum.getCalories());
            valueSum.setElevGain(item.getElevGain() + valueSum.getElevGain());
            valueSum.setElevMax(Math.max(item.getElevMax(), valueSum.getElevMax()));
            valueSum.setTotalLike(item.getTotalLike() + valueSum.getTotalLike());
            valueSum.setTotalComment(item.getTotalComment() + valueSum.getTotalComment());
            valueSum.setTotalShare(item.getTotalShare() + valueSum.getTotalShare());
        }
        valueSum.setUserId(userId);
        valueSum.setAvgPace(valueSum.getAvgPace() / allByTimeRangeAndUserId.size());
        valueSum.setAvgHeart(valueSum.getAvgHeart() / allByTimeRangeAndUserId.size());

        return new ResponseEntity<>(new CodeResponse(valueSum), HttpStatus.OK);
    }

    @RequestMapping("/createUserActivity")
    public ResponseEntity<?> createUserActivity(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestBody CreateActivityRequest paramActivity
    ) {
        Long userId = userPrincipal.getId();
        String sig = paramActivity.getSig();
        Long activityId = paramActivity.getActivityId();
        String sigActivity = activityService.getSigActivity(activityId);
        if(sig.equals(sigActivity)){
            UserActivity userActivity = new UserActivity(paramActivity);
            userActivity.setUserId(userId);
            UserActivity result = userActivityRepository.insert(userActivity);
            return new ResponseEntity<>(new CodeResponse(result), HttpStatus.OK);
        }else{
            return new ResponseEntity<>(new CodeResponse(ErrorCode.ACTIVITY_ADD_FAIL), HttpStatus.BAD_REQUEST);
        }
    }


    @RequestMapping("/getUserActivityByTime")
    public ResponseEntity<?> getUserActivityByTime(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestBody TimeRequest timeRequest
    ) {
        Long userId = userPrincipal.getId();
        List<UserActivity> allByTimeRangeAndUserId = userActivityRepository.findAllByTimeRangeAndUserId(userId, timeRequest.getFromTime(), timeRequest.getToTime());
        return new ResponseEntity<>(new CodeResponse(allByTimeRangeAndUserId), HttpStatus.OK);
    }

    @RequestMapping("/getNumberLastUserActivity")
    public ResponseEntity<?> getNumberLastUserActivity(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestBody NumberActivityRequest numberActivityRequest
    ) {
        Long userId = userPrincipal.getId();
        Pageable pageable = PageRequest.of(numberActivityRequest.getOffset(), numberActivityRequest.getSize());
        List<UserActivity> allByTimeRangeAndUserId = userActivityRepository.findNumberActivityLast(userId, pageable);
        return new ResponseEntity<>(new CodeResponse(allByTimeRangeAndUserId), HttpStatus.OK);

    }

    @RequestMapping("/hardCodeData")
    public ResponseEntity<?> hardCodeData(@CurrentUser UserPrincipal userPrincipal) {
        for (int i = 0; i < 20; i++) {
            UserActivity userActivity = new UserActivity(i % 4 + 5, i % 4 * 10000l, new Time(i % 4 * 100000), 7.3, new Date());
            userActivityRepository.insert(userActivity);
        }
        return new ResponseEntity<>(ErrorCode.FIELD_REQUIRED, HttpStatus.OK);
    }
}

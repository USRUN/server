/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.usrun.core.controller;

import com.usrun.core.config.AppProperties;
import com.usrun.core.config.ErrorCode;
import com.usrun.core.exception.CodeException;
import com.usrun.core.model.Love;
import com.usrun.core.model.User;
import com.usrun.core.model.UserActivity;
import com.usrun.core.payload.CodeResponse;
import com.usrun.core.payload.activity.ActivityRequest;
import com.usrun.core.payload.activity.ConditionRequest;
import com.usrun.core.payload.activity.LoveRequest;
import com.usrun.core.payload.activity.UserStatRequest;
import com.usrun.core.payload.activity.UserStatResp;
import com.usrun.core.payload.user.CreateActivityRequest;
import com.usrun.core.payload.user.GetActivitiesRequest;
import com.usrun.core.payload.user.GetActivityRequest;
import com.usrun.core.payload.user.NumberActivityRequest;
import com.usrun.core.payload.user.TimeRequest;
import com.usrun.core.repository.LoveRepository;
import com.usrun.core.repository.UserActivityRepository;
import com.usrun.core.security.CurrentUser;
import com.usrun.core.security.UserPrincipal;
import com.usrun.core.service.ActivityService;
import com.usrun.core.service.TrackService;
import com.usrun.core.service.UserService;
import com.usrun.core.utility.CacheClient;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

/**
 * @author anhhuy
 */
@RestController
@RequestMapping("/activity")
public class ActivityController {

  private static final Logger logger = LoggerFactory.getLogger(ActivityController.class);

  @Autowired
  private UserActivityRepository userActivityRepository;

  @Autowired
  private LoveRepository loveRepository;

  @Autowired
  private ActivityService activityService;

  @Autowired
  private CacheClient cacheClient;

  @Autowired
  private TrackService trackService;

  @Autowired
  private UserService userService;

  @Autowired
  private AppProperties appProperties;

  @PostMapping("/getUserActivityByTimeWithSum")
  public ResponseEntity<?> getUserActivityByTimeWithSum(
      @CurrentUser UserPrincipal userPrincipal,
      @RequestBody TimeRequest timeRequest
  ) {
    try {
      Long userId = userPrincipal.getId();
      List<UserActivity> allByTimeRangeAndUserId = userActivityRepository
          .findAllByTimeRangeAndUserId(userId, timeRequest.getFromTime(), timeRequest.getToTime(),
              timeRequest.getOffset(), timeRequest.getLimit());
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
    } catch (CodeException ex) {
      return ResponseEntity.status(400).body(new CodeResponse(ex.getErrorCode()));
    } catch (Exception ex) {
      logger.error("", ex);
      return ResponseEntity.status(500).body(new CodeResponse(ErrorCode.SYSTEM_ERROR));
    }
  }

  @PostMapping("/createUserActivity")
  public ResponseEntity<?> createUserActivity(
      @CurrentUser UserPrincipal userPrincipal,
      @RequestBody CreateActivityRequest paramActivity
  ) {
    try {
      long userId = userPrincipal.getId();
      long time = paramActivity.getTime();
      long deltaTime = System.currentTimeMillis() - time;
      if (deltaTime > appProperties.getActivityLock()) {
        return new ResponseEntity<>(new CodeResponse(ErrorCode.ACTIVITY_REQUEST_TIME_INVALID),
            HttpStatus.BAD_REQUEST);
      }

      if (!cacheClient.acquireActivityLock(userId, time, appProperties.getActivityLock())) {
        return new ResponseEntity<>(new CodeResponse(ErrorCode.ACTIVITY_PROCESSING_OR_DUPLICATED),
            HttpStatus.BAD_REQUEST);
      }

      String sig = paramActivity.getSig();
      String sigActivity = activityService.getSigActivity(userId, time);
      try {
        if (sig.equals(sigActivity)) {
          UserActivity userActivity = activityService
              .createUserActivity(userId, paramActivity);
          User user = userService.loadUser(userId);
          cacheClient.setActivityCreated(user, userActivity);
          return new ResponseEntity<>(new CodeResponse(userActivity), HttpStatus.OK);
        } else {
          return new ResponseEntity<>(new CodeResponse(ErrorCode.ACTIVITY_ADD_FAIL),
              HttpStatus.BAD_REQUEST);
        }
      } catch (Exception exp) {
        logger.error("", exp);
        return new ResponseEntity<>(new CodeResponse(ErrorCode.FAIL), HttpStatus.BAD_REQUEST);
      }
    } catch (CodeException ex) {
      return ResponseEntity.status(400).body(new CodeResponse(ex.getErrorCode()));
    } catch (Exception ex) {
      logger.error("", ex);
      return ResponseEntity.status(500).body(new CodeResponse(ErrorCode.SYSTEM_ERROR));
    }
  }

  @PostMapping("/getActivityByUser")
  public ResponseEntity<?> getUserActivityByTime(
      @RequestBody ActivityRequest activityReq
  ) {
    try {
      List<UserActivity> allByTimeRangeAndUserId = userActivityRepository
          .findAllByUserId(activityReq.getUserId(), activityReq.getOffset(),
              activityReq.getLimit());
      return new ResponseEntity<>(new CodeResponse(allByTimeRangeAndUserId), HttpStatus.OK);
    } catch (CodeException ex) {
      return ResponseEntity.status(400).body(new CodeResponse(ex.getErrorCode()));
    } catch (Exception ex) {
      logger.error("", ex);
      return ResponseEntity.status(500).body(new CodeResponse(ErrorCode.SYSTEM_ERROR));
    }
  }

  @PostMapping("/getUserActivityByTime")
  public ResponseEntity<?> getUserActivityByTime(
      @CurrentUser UserPrincipal userPrincipal,
      @RequestBody TimeRequest timeRequest
  ) {
    try {
      Long userId = userPrincipal.getId();
      List<UserActivity> allByTimeRangeAndUserId = userActivityRepository
          .findAllByTimeRangeAndUserId(userId, timeRequest.getFromTime(), timeRequest.getToTime(),
              timeRequest.getOffset(), timeRequest.getLimit());
      return new ResponseEntity<>(new CodeResponse(allByTimeRangeAndUserId), HttpStatus.OK);
    } catch (CodeException ex) {
      return ResponseEntity.status(400).body(new CodeResponse(ex.getErrorCode()));
    } catch (Exception ex) {
      logger.error("", ex);
      return ResponseEntity.status(500).body(new CodeResponse(ErrorCode.SYSTEM_ERROR));
    }
  }

  @PostMapping("/getUserActivityByTimeWithCondition")
  public ResponseEntity<?> getUserActivityByTimeWithCondition(
      @CurrentUser UserPrincipal userPrincipal,
      @RequestBody ConditionRequest conditionRequest
  ) {
    try {
      Long userId = userPrincipal.getId();
      List<UserActivity> allByTimeRangeAndUserId = userActivityRepository
          .findAllByTimeRangeAndUserIdWithCondition(userId, conditionRequest.getFromTime(),
              conditionRequest.getToTime(), conditionRequest.getDistance(),
              conditionRequest.getPace(), conditionRequest.getElevation(),
              conditionRequest.getOffset(), conditionRequest.getLimit());
      return new ResponseEntity<>(new CodeResponse(allByTimeRangeAndUserId), HttpStatus.OK);
    } catch (CodeException ex) {
      return ResponseEntity.status(400).body(new CodeResponse(ex.getErrorCode()));
    } catch (Exception ex) {
      logger.error("", ex);
      return ResponseEntity.status(500).body(new CodeResponse(ErrorCode.SYSTEM_ERROR));
    }
  }

  @PostMapping("/getNumberLastUserActivity")
  public ResponseEntity<?> getNumberLastUserActivity(
      @CurrentUser UserPrincipal userPrincipal,
      @RequestBody NumberActivityRequest numberActivityRequest
  ) {
    try {
      Long userId = userPrincipal.getId();
      Pageable pageable = PageRequest
          .of(numberActivityRequest.getOffset(), numberActivityRequest.getSize());
      List<UserActivity> allByTimeRangeAndUserId = userActivityRepository
          .findNumberActivityLast(userId, pageable);
      return new ResponseEntity<>(new CodeResponse(allByTimeRangeAndUserId), HttpStatus.OK);
    } catch (CodeException ex) {
      return ResponseEntity.status(400).body(new CodeResponse(ex.getErrorCode()));
    } catch (Exception ex) {
      logger.error("", ex);
      return ResponseEntity.status(500).body(new CodeResponse(ErrorCode.SYSTEM_ERROR));
    }

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
      return ResponseEntity.status(400).body(new CodeResponse(ex.getErrorCode()));
    } catch (Exception ex) {
      logger.error("", ex);
      return ResponseEntity.status(500).body(new CodeResponse(ErrorCode.SYSTEM_ERROR));
    }
  }

  @PostMapping("/getActivitiesByTeam")
  @PreAuthorize("hasRole('USER') && @teamAuthorization.authorize(authentication, 'MEMBER', #request.teamId)")
  public ResponseEntity<?> getActivitiesByTeam(
      @RequestBody GetActivitiesRequest request
  ) {
    try {
      int count = request.getCount() <= 0 ? 10 : request.getCount();
      int offset = Math.max(request.getOffset(), 1) - 1;
      List<UserActivity> activities = activityService
          .getActivitiesByTeam(request.getTeamId(), count, offset);
      return ResponseEntity.ok(new CodeResponse(activities));
    } catch (CodeException ex) {
      return ResponseEntity.status(400).body(new CodeResponse(ex.getErrorCode()));
    } catch (Exception ex) {
      logger.error("", ex);
      return ResponseEntity.status(500).body(new CodeResponse(ErrorCode.SYSTEM_ERROR));
    }
  }

  @PostMapping("/loveActivity")
  public ResponseEntity<?> loveActivity(
      @CurrentUser UserPrincipal userPrincipal,
      @RequestBody LoveRequest request
  ) {
    try {
      long userId = userPrincipal.getId();
      long activityId = request.getActivityId();
//      Love loveObject = new Love(request.getActivityId(), userId);
//      Love insert = loveRepository.insert(loveObject);
      boolean r = activityService.loveActivity(userId, activityId, true);
      return new ResponseEntity<>(new CodeResponse(r), HttpStatus.OK);
    } catch (CodeException ex) {
      return ResponseEntity.status(400).body(new CodeResponse(ex.getErrorCode()));
    } catch (Exception ex) {
      logger.error("", ex);
      return ResponseEntity.status(500).body(new CodeResponse(ErrorCode.SYSTEM_ERROR));
    }
  }

  @PostMapping("/getUserLoveActivity")
  public ResponseEntity<?> getUserLoveActivity(
      @RequestBody LoveRequest request
  ) {
    try {
      List<Long> numberLoveOfActivity = loveRepository
          .getNumberLoveOfActivity(request.getActivityId());
      return new ResponseEntity<>(new CodeResponse(numberLoveOfActivity), HttpStatus.OK);
    } catch (CodeException ex) {
      return ResponseEntity.status(400).body(new CodeResponse(ex.getErrorCode()));
    } catch (Exception ex) {
      logger.error("", ex);
      return ResponseEntity.status(500).body(new CodeResponse(ErrorCode.SYSTEM_ERROR));
    }
  }

  @PostMapping("/isLoveActivity")
  public ResponseEntity<?> isLoveActivity(
      @CurrentUser UserPrincipal userPrincipal,
      @RequestBody LoveRequest request
  ) {
    try {
      Long userId = userPrincipal.getId();
      boolean userLoveActivity = loveRepository.isUserLoveActivity(userId, request.getActivityId());
      return new ResponseEntity<>(new CodeResponse(userLoveActivity), HttpStatus.OK);
    } catch (CodeException ex) {
      return ResponseEntity.status(400).body(new CodeResponse(ex.getErrorCode()));
    } catch (Exception ex) {
      logger.error("", ex);
      return ResponseEntity.status(500).body(new CodeResponse(ErrorCode.SYSTEM_ERROR));
    }
  }

  @PostMapping("/removeLoveActivity")
  public ResponseEntity<?> removeLoveActivity(
      @CurrentUser UserPrincipal userPrincipal,
      @RequestBody LoveRequest request
  ) {
    try {
      Long userId = userPrincipal.getId();
      Love loveObject = new Love(request.getActivityId(), userId);
      boolean remove = loveRepository.delete(loveObject);
      return new ResponseEntity<>(new CodeResponse(remove), HttpStatus.OK);
    } catch (CodeException ex) {
      return ResponseEntity.status(400).body(new CodeResponse(ex.getErrorCode()));
    } catch (Exception ex) {
      logger.error("", ex);
      return ResponseEntity.status(500).body(new CodeResponse(ErrorCode.SYSTEM_ERROR));
    }
  }

  @PostMapping("/getStatUser")
  public ResponseEntity<?> getStatUser(
      @CurrentUser UserPrincipal userPrincipal,
      @RequestBody UserStatRequest userStatRequest
  ) {
    try {
      Long userId = userPrincipal.getId();
      UserStatResp resp = activityService
          .getUserStat(userId, userStatRequest.getFromTime(), userStatRequest.getToTime());
      return new ResponseEntity<>(new CodeResponse(resp), HttpStatus.OK);
    } catch (CodeException ex) {
      return ResponseEntity.status(400).body(new CodeResponse(ex.getErrorCode()));
    } catch (Exception ex) {
      logger.error("", ex);
      return ResponseEntity.status(500).body(new CodeResponse(ErrorCode.SYSTEM_ERROR));
    }
  }
}

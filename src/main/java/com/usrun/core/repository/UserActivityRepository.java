package com.usrun.core.repository;

import com.usrun.core.model.UserActivity;
import java.util.Date;
import java.util.List;
import org.springframework.data.domain.Pageable;


public interface UserActivityRepository {

  UserActivity insert(UserActivity userActivity);

  UserActivity findById(long id);

  List<UserActivity> findAllByUserId(long userId);

  List<UserActivity> findAllByTimeRangeAndUserId(long userId, Date timeFrom, Date timeTo);

  List<UserActivity> findAllByTimeRangeAndUserIdWithCondition(long userId, Date timeFrom,
      Date timeTo, long distance, double pace, double elev);

  List<UserActivity> findNumberActivityLast(long userId, Pageable pageable);

  long countUserActivityByUser(long teamId);

  List<Long> findByTeamId(long teamId, long limit);

  List<UserActivity> findByIds(List<Long> ids);
}

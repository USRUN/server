package com.usrun.core.repository;

import com.usrun.core.model.UserActivity;
import com.usrun.core.payload.dto.UserActivityStatDTO;
import java.util.Date;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface UserActivityRepository {

  UserActivity insert(UserActivity userActivity);

  UserActivity findById(long id);

  List<UserActivity> findAllByUserId(long userId, int offset, int limit);

  List<UserActivity> findAllByTimeRangeAndUserId(long userId, Date timeFrom, Date timeTo,
      int offset, int limit);

  List<UserActivity> findAllByTimeRangeAndUserId(long userId, Date timeFrom, Date timeTo);

  List<UserActivity> findAllByTimeRangeAndUserIdWithCondition(long userId, Date timeFrom,
      Date timeTo, long distance, double pace, double elev, int offset, int limit);

  List<UserActivity> findNumberActivityLast(long userId, Pageable pageable);

  long countUserActivityByUser(long teamId);

  List<Long> findByTeamId(long teamId, long limit);

  List<UserActivity> findByUserIds(List<Long> ids);

  List<UserActivity> findByIds(List<Long> ids);

  List<UserActivity> findByTeamId(long teamId);

  void updateTotalLove(long activityId, int count);

  List<UserActivityStatDTO> getStat();

  UserActivity update(UserActivity activityId);

  boolean delete(long activityId);
}

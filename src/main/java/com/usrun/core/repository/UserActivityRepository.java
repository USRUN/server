package com.usrun.core.repository;

import com.usrun.core.model.UserActivity;
import java.util.*;


public interface UserActivityRepository {
    UserActivity insert(UserActivity userActivity);
    UserActivity findById(long id);
    List<UserActivity> findAllByUserId(long userId);
    List<UserActivity> findAllByTimeRangeAndUserId(long userId, Date timeFrom, Date timeTo);
    List<UserActivity> findNumberActivityLast(long userId, int number);
}

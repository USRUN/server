package com.usrun.core.repository;

import com.usrun.core.model.Love;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface LoveRepository {

  Love insert(Love loveObj);

  boolean delete(Love toDelete);

  List<Long> getNumberLoveOfActivity(long activityId);

  boolean isUserLoveActivity(long userId, long activityId);
}

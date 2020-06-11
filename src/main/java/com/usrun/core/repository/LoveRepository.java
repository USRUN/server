package com.usrun.core.repository;

import com.usrun.core.model.Love;
import com.usrun.core.model.junction.TeamMember;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface LoveRepository {
    Love insert (Love loveObj);
    boolean delete(Love toDelete);
    List<Long> getNumberLoveOfActivity(long activityId);
    boolean isUserLoveActivity(long userId, long activityId);
}

package com.usrun.core.repository.impl;

import com.usrun.core.model.Love;
import com.usrun.core.repository.LoveRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class LoveRepositoryImpl implements LoveRepository {

  @Autowired
  private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  private MapSqlParameterSource mapLove(Love loveObj) {
    MapSqlParameterSource map = new MapSqlParameterSource();
    map.addValue("userId", loveObj.getUserId());
    map.addValue("activityId", loveObj.getActivityId());
    return map;
  }

  private List<Love> getLoves(String sql, MapSqlParameterSource params) {
    return namedParameterJdbcTemplate.query(
        sql,
        params,
        (rs, i) -> new Love(
            rs.getLong("userId"),
            rs.getLong("activityId")));
  }


  @Override
  public Love insert(Love loveObj) {
    MapSqlParameterSource map = mapLove(loveObj);
    try {
      namedParameterJdbcTemplate.update(
          "INSERT INTO love(userId,activityId)" +
              " VALUES(:userId,:activityId)",
          map
      );
    } catch (Exception ex) {
      log.error("", ex);
      return null;
    }
    return loveObj;
  }

  @Override
  public boolean delete(Love toDelete) {
    MapSqlParameterSource map = mapLove(toDelete);
    String sql = "DELETE FROM love WHERE userId = :userId AND activityId = :activityId";
    int status = namedParameterJdbcTemplate.update(sql, map);
    return status != 0;
  }

  @Override
  public List<Long> getNumberLoveOfActivity(long activityId) {
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("activityId", activityId);
    String sql = "SELECT distinct userId FROM love WHERE activityId = :activityId";
    List<Long> toReturn = namedParameterJdbcTemplate.query(
        sql,
        params,
        (rs, i) -> new Long(
            rs.getLong("userId")));
    return toReturn;
  }

  @Override
  public boolean isUserLoveActivity(long userId, long activityId) {
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("activityId", activityId);
    params.addValue("userId", userId);
    String sql = "SELECT * FROM love WHERE activityId = :activityId AND userId = :userId";
    List<Love> loves = getLoves(sql, params);
    return !loves.isEmpty();
  }

  @Override
  public long countLove(long activityId) {
    MapSqlParameterSource params = new MapSqlParameterSource("activityId", activityId);
    String sql = "SELECT COUNT(userId) as loveCount FROM love WHERE activityId = :activityId";
    List<Long> counts = namedParameterJdbcTemplate
        .query(sql, params, (rs, i) -> new Long(rs.getLong("loveCount")));
    if (counts.isEmpty()) {
      return 0;
    }
    return counts.get(0);
  }

  @Override
  public List<Long> countLoves(List<Long> activityIds) {
    if (activityIds == null || activityIds.isEmpty()) {
      activityIds = Collections.singletonList(-1L);
    }
    List<Long> loveCounts = new ArrayList<>(activityIds.size());
    Map<Long, Long> loveMap = new HashMap<>();
    MapSqlParameterSource params = new MapSqlParameterSource("activityIds", activityIds);
    String sql = "SELECT activityId, COUNT(userId) as loveCount FROM love "
        + "WHERE activityId IN (:activityIds) "
        + "GROUP BY activityId";
    namedParameterJdbcTemplate
        .query(sql, params,
            (rs, i) -> loveMap.put(rs.getLong("activityId"), rs.getLong("loveCount")));
    for (Long activityId : activityIds) {
      if (loveMap.get(activityId) == null) {
        loveCounts.add(0L);
      } else {
        loveCounts.add(loveMap.get(activityId));
      }
    }
    return loveCounts;
  }
}

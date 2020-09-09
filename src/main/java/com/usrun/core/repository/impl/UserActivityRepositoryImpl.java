package com.usrun.core.repository.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.usrun.core.model.UserActivity;
import com.usrun.core.payload.dto.UserActivityStatDTO;
import com.usrun.core.repository.UserActivityRepository;
import com.usrun.core.utility.ObjectUtils;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserActivityRepositoryImpl implements UserActivityRepository {

  @Autowired
  private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  @Override
  public UserActivity insert(UserActivity userActivity) {
    MapSqlParameterSource map = getMapUserActivity(userActivity);
    namedParameterJdbcTemplate.update(
        "INSERT INTO userActivity (userActivityId, userId,createTime, totalDistance, totalTime, "
            + "totalStep, avgPace, avgHeart, maxHeart, calories, "
            + "elevGain, elevMax, photo, title, description, totalLove, totalComment,totalShare, processed,deleted, privacy, eventId, isShowMap, splitPace ) values ("
            + ":userActivityId ,:userId,:createTime,  :totalDistance, :totalTime, :totalStep, :avgPace, :avgHeart, :maxHeart, "
            + ":calories, :elevGain, :elevMax, :photo, :title, :description, :totalLove, :totalComment, :totalShare, :processed, :deleted, :privacy, :eventId, :isShowMap, :splitPace)",
        map
    );
    return userActivity;
  }

  @Override
  public UserActivity findById(long id) {
    MapSqlParameterSource params = new MapSqlParameterSource("userActivityId", id);
    String sql = "SELECT * FROM userActivity WHERE userActivityId = :userActivityId";
    List<UserActivity> userActivity = findUserActivity(sql, params);
    if (userActivity.size() > 0) {
      return userActivity.get(0);
    } else {
      return null;
    }
  }

  @Override
  public List<UserActivity> findAllByUserId(long userId, int offset, int limit) {
    MapSqlParameterSource params = new MapSqlParameterSource("userId", userId);
    params.addValue("offset", offset * limit);
    params.addValue("limit", limit);
    String sql = "SELECT * FROM userActivity WHERE userId= :userId ORDER BY createTime DESC LIMIT :limit OFFSET :offset";
    List<UserActivity> userActivity = findUserActivity(sql, params);
    return userActivity;
  }

  @Override
  public List<UserActivity> findAllByTimeRangeAndUserId(long userId, Date timeFrom, Date timeTo,
      int offset, int limit) {
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("userId", userId);
    params.addValue("timeFrom", timeFrom);
    params.addValue("timeTo", timeTo);
    params.addValue("offset", offset * limit);
    params.addValue("limit", limit);
    String sql = "SELECT * FROM userActivity WHERE userId= :userId AND createTime >= :timeFrom AND createTime <= :timeTo LIMIT :limit OFFSET :offset";
    List<UserActivity> userActivity = findUserActivity(sql, params);
    return userActivity;
  }

  @Override
  public List<UserActivity> findAllByTimeRangeAndUserId(long userId, Date timeFrom, Date timeTo) {
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("userId", userId);
    params.addValue("timeFrom", timeFrom);
    params.addValue("timeTo", timeTo);
    String sql = "SELECT * FROM userActivity WHERE userId = :userId AND createTime >= :timeFrom AND createTime <= :timeTo";
    List<UserActivity> userActivity = findUserActivity(sql, params);
    return userActivity;
  }

  @Override
  public List<UserActivity> findAllByTimeRangeAndUserIdWithCondition(long userId, Date timeFrom,
      Date timeTo, long distance, double pace, double elev, int offset, int limmit) {
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("userId", userId);
    params.addValue("timeFrom", timeFrom);
    params.addValue("timeTo", timeTo);
    params.addValue("totalDistance", distance);
    params.addValue("avgPace", pace);
    params.addValue("elevGain", elev);
    params.addValue("offset", offset * limmit);
    params.addValue("limit", limmit);
    String sql
        =
        "SELECT * FROM userActivity WHERE userId = :userId AND createTime >= :timeFrom AND createTime <= :timeTo"
            + "AND totalDistance >= :distance AND avgPace <= :pace AND elevGain >= elev LIMIT :limit OFFSET :offset ";
    List<UserActivity> userActivity = findUserActivity(sql, params);
    return userActivity;
  }

  @Override
  public List<UserActivity> findNumberActivityLast(long userId, Pageable pageable) {
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("userId", userId);
    params.addValue("size", pageable.getPageSize());
    params.addValue("offset", pageable.getOffset());
    String sql = "SELECT * FROM userActivity WHERE userId = :userId ORDER BY createTime DESC, userActivityId DESC LIMIT :size OFFSET :offset";
    List<UserActivity> userActivity = findUserActivity(sql, params);
    return userActivity;
  }

  @Override
  public long countUserActivityByUser(long teamId) {
    MapSqlParameterSource params = new MapSqlParameterSource("teamId", teamId);
    String sql = "SELECT count(*) FROM userActivity ua, teamMember tm WHERE tm.teamId = :teamId AND tm.userId = ua.userId";
    return namedParameterJdbcTemplate.queryForObject(sql, params, Long.class);
  }

  @Override
  public List<Long> findByTeamId(long teamId, long limit) {
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("teamId", teamId);
    params.addValue("limit", limit);
    String sql = "SELECT userActivityId "
        + "FROM userActivity ua, teamMember tm "
        + "WHERE tm.teamId = :teamId AND tm.userId = ua.userId "
        + "ORDER BY userActivityId "
        + "DESC LIMIT :limit";
    return namedParameterJdbcTemplate.query(
        sql,
        params,
        (rs, i) -> new Long(rs.getLong("userActivityId")));
  }

  @Override
  public List<UserActivity> findByUserIds(List<Long> ids) {
    if (ids == null || ids.isEmpty()) {
      ids = Collections.singletonList(-1L);
    }
    MapSqlParameterSource params = new MapSqlParameterSource("ids", ids);
    String sql = "SELECT * FROM userActivity WHERE userId IN (:ids)";
    return findUserActivity(sql, params);
  }

  @Override
  public List<UserActivity> findByIds(List<Long> ids) {
    if (ids == null || ids.isEmpty()) {
      ids = Collections.singletonList(-1L);
    }
    MapSqlParameterSource params = new MapSqlParameterSource("ids", ids);
    String sql = "SELECT * FROM userActivity WHERE userActivityId IN (:ids) ORDER BY userActivityId DESC";
    return findUserActivity(sql, params);
  }

  private List<UserActivity> findUserActivity(String sql, MapSqlParameterSource params) {
    List<UserActivity> listUserActivity = namedParameterJdbcTemplate.query(
        sql,
        params,
        (rs, i) -> new UserActivity(rs.getLong("userActivityId"),
            rs.getLong("userId"),
            rs.getTimestamp("createTime").getTime(),
            rs.getLong("totalDistance"),
            rs.getLong("totalTime"),
            rs.getLong("totalStep"),
            rs.getDouble("avgPace"),
            rs.getDouble("avgHeart"),
            rs.getDouble("maxHeart"),
            rs.getInt("calories"),
            rs.getDouble("elevGain"),
            rs.getDouble("elevMax"),
            ObjectUtils.fromJsonString(rs.getString("photo"), new TypeReference<List<String>>() {
            }),
            rs.getString("title"),
            rs.getString("description"),
            rs.getInt("totalLove"),
            rs.getInt("totalComment"),
            rs.getInt("totalShare"),
            rs.getBoolean("processed"),
            rs.getInt("deleted"),
            rs.getInt("privacy"),
            rs.getLong("eventId"),
            rs.getBoolean("isShowMap"),
            rs.getString("splitPace")
        ));
    if (listUserActivity.size() > 0) {
      return listUserActivity;
    } else {
      return Collections.emptyList();
    }
  }

  private MapSqlParameterSource getMapUserActivity(UserActivity userActivity) {
    MapSqlParameterSource map = new MapSqlParameterSource();
    map.addValue("userActivityId", userActivity.getUserActivityId());
    map.addValue("userId", userActivity.getUserId());
    map.addValue("createTime", userActivity.getCreateTime());
    map.addValue("totalDistance", userActivity.getTotalDistance());
    map.addValue("totalTime", userActivity.getTotalTime());
    map.addValue("totalStep", userActivity.getTotalStep());
    map.addValue("avgPace", userActivity.getAvgPace());
    map.addValue("avgHeart", userActivity.getAvgHeart());
    map.addValue("maxHeart", userActivity.getMaxHeart());
    map.addValue("calories", userActivity.getCalories());
    map.addValue("elevGain", userActivity.getElevGain());
    map.addValue("elevMax", userActivity.getElevMax());
    map.addValue("photo", ObjectUtils.toJsonString(userActivity.getPhotos()));
    map.addValue("title", userActivity.getTitle());
    map.addValue("description", userActivity.getDescription());
    map.addValue("totalLove", userActivity.getTotalLove());
    map.addValue("totalComment", userActivity.getTotalComment());
    map.addValue("totalShare", userActivity.getTotalShare());
    map.addValue("processed", userActivity.isProcessed());
    map.addValue("deleted", userActivity.getDeleted());
    map.addValue("privacy", userActivity.getPrivacy());
    map.addValue("eventId", userActivity.getEventId());
    map.addValue("isShowMap", userActivity.isShowMap());
    map.addValue("splitPace", userActivity.getSplitPace());
    return map;
  }

  @Override
  public List<UserActivity> findByTeamId(long teamId) {
    MapSqlParameterSource params = new MapSqlParameterSource("teamId", teamId);
    String sql = "SELECT * "
        + "FROM userActivity ua, teamMember tm "
        + "WHERE tm.teamId = :teamId AND tm.userId = ua.userId ";
    return findUserActivity(sql, params);
  }

  @Override
  public void updateTotalLove(long activityId, int count) {
    MapSqlParameterSource params = new MapSqlParameterSource("activityId", activityId);
    params.addValue("count", count);
    String sql = "UPDATE userActivity "
        + "SET totalLove = "
        + "(SELECT totalLove FROM userActivity WHERE userActivityId = :activityId FOR UPDATE) + :count "
        + "WHERE userActivityId = :activityId";
    namedParameterJdbcTemplate.update(sql, params);
  }

  @Override
  public List<UserActivityStatDTO> getStat() {
    String sql = "SELECT userId, SUM(totalDistance) as totalDistance, "
        + "SUM(totalTime) as totalTime, MAX(totalDistance) as maxDistance, "
        + "MAX(totalTime) as maxTime, COUNT(userActivityId) as userActivityCount "
        + "FROM userActivity GROUP BY userId";
    return namedParameterJdbcTemplate.query(sql, (rs, i) -> new UserActivityStatDTO(
        rs.getLong("userId"),
        rs.getLong("totalDistance"),
        rs.getLong("totalTime"),
        rs.getLong("maxDistance"),
        rs.getLong("maxTime"),
        rs.getLong("userActivityCount")
    ));
  }

  @Override
  public UserActivity update(UserActivity toUpdate) {
    MapSqlParameterSource params = getMapUserActivity(toUpdate);

    int affected = namedParameterJdbcTemplate.update(
        "UPDATE userActivity SET title = :title, description = :description, photo = :photo, isShowMap = :isShowMap",
        params
    );

    return affected == 0 ? null : toUpdate;
  }

  @Override
  public boolean delete(long activityId) {
    MapSqlParameterSource params = new MapSqlParameterSource("activityId", activityId);

    String sql = "UPDATE userActivity SET isDeleted = true WHERE userActivityId = :activityId";

    int affected = namedParameterJdbcTemplate.update(sql, params);
    return affected != 0;
  }
}

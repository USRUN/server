package com.usrun.core.repository.impl;

import com.usrun.core.model.Role;
import com.usrun.core.model.User;
import com.usrun.core.model.UserActivity;
import com.usrun.core.model.type.AuthType;
import com.usrun.core.model.type.Gender;
import com.usrun.core.model.type.RoleType;
import com.usrun.core.payload.dto.UserFilterDTO;
import com.usrun.core.repository.UserActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class UserActivityRepositoryImpl implements UserActivityRepository {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public UserActivity insert(UserActivity userActivity) {
        MapSqlParameterSource map = getMapUserActivity(userActivity);
        final KeyHolder holder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(
                "INSERT INTO userActivity (userId, createTime, totalDistance, totalTime, " +
                        "totalStep, avgPace, avgHeart, maxHeart, calories, " +
                        "elevGain, elevMax, photo, title, description, totalLike, totalComment,totalShare, processed,deleted, privacy ) values (" +
                        ":userId, :createTime, :totalDistance, :totalTime, :totalStep, :avgPace, :avgHeart, :maxHeart, " +
                        ":calories, :elevGain, :elevMax, :photo, :title, :description, :totalLike, :totalComment, :totalShare, :processed, :deleted, :privacy)",
                map
        );
        return userActivity;
    }


    @Override
    public UserActivity findById(long id){
        MapSqlParameterSource params = new MapSqlParameterSource("userActivityId", id);
        String sql = "SELECT * FROM userActivity WHERE userActivityId = :userActivityId";
        List<UserActivity> userActivity = findUserActivity(sql, params);
        if(userActivity.size()>0) return userActivity.get(0);
        else return null;
    }

    @Override
    public List<UserActivity> findAllByUserId(long userId) {
        MapSqlParameterSource params = new MapSqlParameterSource("userId", userId);
        String sql = "SELECT * FROM userActivity WHERE userId = :userId";
        List<UserActivity> userActivity = findUserActivity(sql, params);
        return userActivity;
    }

    @Override
    public List<UserActivity> findAllByTimeRangeAndUserId(long userId, Date timeFrom, Date timeTo) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", userId);
        params.addValue("timeFrom", timeFrom);
        params.addValue("timeTo",timeTo);
        String sql = "SELECT * FROM userActivity WHERE userId = :userId AND createTime >= :timeFrom AND createTime <= :timeTo ";
        List<UserActivity> userActivity = findUserActivity(sql, params);
        return userActivity;
    }

    @Override
    public List<UserActivity> findNumberActivityLast(long userId, int number) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", userId);
        params.addValue("number", number);
        String sql = "SELECT * FROM userActivity WHERE userId = :userId ORDER BY createTime DESC LIMIT :number";
        List<UserActivity> userActivity = findUserActivity(sql, params);
        return userActivity;
    }


    private List<UserActivity> findUserActivity(String sql, MapSqlParameterSource params) {
        List<UserActivity> listUserActivity = namedParameterJdbcTemplate.query(
                sql,
                params,
                (rs, i) -> new UserActivity(rs.getLong("userActivityId"),
                        rs.getLong("userId"),
                        rs.getDate("createTime"),
                        rs.getLong("totalDistance"),
                        rs.getTime("totalTime"),
                        rs.getLong("totalStep"),
                        rs.getDouble("avgPace"),
                        rs.getDouble("avgHeart"),
                        rs.getDouble("maxHeart"),
                        rs.getInt("calories"),
                        rs.getDouble("elevGain"),
                        rs.getDouble("elevMax"),
                        rs.getString("photo"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getInt("totalLike"),
                        rs.getInt("totalComment"),
                        rs.getInt("totalShare"),
                        rs.getBoolean("processed"),
                        rs.getInt("deleted"),
                        rs.getInt("privacy")
                ));
        if (listUserActivity != null && listUserActivity.size() >0) {
            return listUserActivity;
        } else {
            return null;
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
        map.addValue("photo", userActivity.getPhoto());
        map.addValue("title", userActivity.getTitle());
        map.addValue("description", userActivity.getDescription());
        map.addValue("totalLike", userActivity.getTotalLike());
        map.addValue("totalComment", userActivity.getTotalComment());
        map.addValue("totalShare", userActivity.getTotalShare());
        map.addValue("processed", userActivity.isProcessed());
        map.addValue("deleted", userActivity.getDeleted());
        map.addValue("privacy", userActivity.getPrivacy());
        return map;
    }

    private List<UserFilterDTO> getUserFilterDTO(String sql, MapSqlParameterSource params) {
        List<UserFilterDTO> userFilterDTOS = namedParameterJdbcTemplate.query(
                sql,
                params,
                (rs, i) -> new UserFilterDTO(
                        rs.getLong("userId"),
                        rs.getString("displayName"),
                        rs.getString("email"),
                        rs.getString("userCode"),
                        Gender.fromInt(rs.getInt("gender")),
                        rs.getDate("birthday")
                )
        );
        return userFilterDTOS;
    }

}

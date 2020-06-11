package com.usrun.core.repository.impl;

import com.usrun.core.model.Love;
import com.usrun.core.model.UserActivity;
import com.usrun.core.model.junction.TeamMember;
import com.usrun.core.model.type.TeamMemberType;
import com.usrun.core.repository.LoveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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
    private List<Love> getLove(String sql, MapSqlParameterSource params) {
        List<Love> toReturn = namedParameterJdbcTemplate.query(
                sql,
                params,
                (rs, i) -> new Love(
                        rs.getLong("userId"),
                        rs.getLong("activityId")));

        if (toReturn != null && toReturn.size() > 0)
            return toReturn;
        return null;
    }


    @Override
    public Love insert(Love loveObj) {
        MapSqlParameterSource map = mapLove(loveObj);
        try{
            namedParameterJdbcTemplate.update(
                    "INSERT INTO love(userId,activityId)" +
                            " VALUES(:userId,:activityId)",
                    map
            );
            return loveObj;
        }catch (Exception ex){
            return null;
        }
    }

    @Override
    public boolean delete(Love toDelete) {
        int status = 0;
        MapSqlParameterSource map = mapLove(toDelete);
        status = namedParameterJdbcTemplate.update(
                "DELETE FROM teamMember" +
                        "WHERE  userId= :userId, activityId= :activityId",
                map
        );
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
        String sql = "SELECT COUNT(userId) FROM love WHERE activityId = :activityId and userId = :userId";
        Integer number = namedParameterJdbcTemplate.queryForObject(sql,params, Integer.class);
        if(number > 0) return true;
        return false;
    }

}

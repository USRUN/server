package com.usrun.core.repository.impl;

import com.usrun.core.model.Team;
import com.usrun.core.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class TeamRepositoryImpl implements TeamRepository {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Team insert(Team toInsert) {
        MapSqlParameterSource map = mapTeamObject(toInsert);
        final KeyHolder holder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(
                "INSERT INTO usrun.team (privacy, totalMember, teamName, thumbnail, verified, deleted, createTime, location) " +
                        "values (" +
                        ":privacy, :totalMember, :teamName, :thumbnail, :verified, :deleted, :createTime, :location )",
                map,
                holder,
                new String[]{"GENERATED_ID"});

        return toInsert;
    }

    @Override
    public Team update(Team user) {
        return null;
    }

    @Override
    public Team findTeamByName(String teamName) {
        return null;
    }

    @Override
    public Team findTeamById(Long teamId) {
        return null;
    }

    @Override
    public boolean joinTeam(Long teamId) {
        return false;
    }

    @Override
    public boolean getPendingList(Long teamId) {
        return false;
    }

    @Override
    public boolean updatePendingList(Long teamId, int action) {
        return false;
    }

    private MapSqlParameterSource mapTeamObject(Team toMap){
        MapSqlParameterSource toReturn = new MapSqlParameterSource();

        toReturn.addValue("teamName",toMap.getTeamName());
        toReturn.addValue("privacy",toMap.getPrivacy());
        toReturn.addValue("thumbnail",toMap.getThumbnail());
        toReturn.addValue("location",toMap.getLocation());
        toReturn.addValue("totalMember",toMap.getTotalMember());
        toReturn.addValue("createTime",toMap.getCreateTime());
        toReturn.addValue("deleted",toMap.isDeleted());
        toReturn.addValue("verified",toMap.isVerified());

        return toReturn;
    }
}

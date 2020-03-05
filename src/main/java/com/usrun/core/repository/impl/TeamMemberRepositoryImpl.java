package com.usrun.core.repository.impl;

import com.usrun.core.model.junction.TeamMember;
import com.usrun.core.model.type.TeamMemberType;
import com.usrun.core.repository.TeamMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class TeamMemberRepositoryImpl implements TeamMemberRepository {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public TeamMember insert(TeamMember toInsert) {

        MapSqlParameterSource map = mapTeamMember(toInsert);
        namedParameterJdbcTemplate.update(
                "INSERT INTO teamMember(teamId,userId,teamMemberType,addTime)" +
                " VALUES(:teamId,:userId,:teamMemberType,addTime)",
                map
        );
        return toInsert;
    }

    @Override
    public TeamMember update(TeamMember toUpdate) {
        return null;
    }

    @Override
    public TeamMember findById(Long userIdToFind) {
        return null;
    }

    @Override
    public Optional<TeamMember[]> filterByMemberType(TeamMemberType toFilter) {
        return Optional.empty();
    }

    private MapSqlParameterSource mapTeamMember(TeamMember toMap){

        MapSqlParameterSource toReturn = new MapSqlParameterSource();

        toReturn.addValue("teamId", toMap.getTeamId());
        toReturn.addValue("userId",toMap.getUserId());
        toReturn.addValue("teamMemberType",toMap.getTeamMemberType());
        toReturn.addValue("addTime",toMap.getAddTime());

        return toReturn;
    }
}

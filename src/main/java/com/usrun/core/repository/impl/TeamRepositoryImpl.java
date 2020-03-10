package com.usrun.core.repository.impl;

import com.usrun.core.model.Team;
import com.usrun.core.model.User;
import com.usrun.core.model.junction.TeamMember;
import com.usrun.core.model.type.TeamMemberType;
import com.usrun.core.repository.TeamMemberRepository;
import com.usrun.core.repository.TeamRepository;
import com.usrun.core.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public class TeamRepositoryImpl implements TeamRepository {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Team insert(Team toInsert, Long userId) {
        MapSqlParameterSource map = mapTeamObject(toInsert);
        final KeyHolder holder = new GeneratedKeyHolder();

        // insert team into DB
        namedParameterJdbcTemplate.update(
                "INSERT INTO usrun.team (privacy, totalMember, teamName, thumbnail, verified, deleted, createTime, location,description) " +
                        "values (" +
                        ":privacy, :totalMember, :teamName, :thumbnail, :verified, :deleted, :createTime, :location, :description )",
                map,
                holder,
                new String[]{"GENERATED_ID"});

        toInsert.setId(holder.getKey().longValue());

        // adding team creator as owner to DB
        TeamMember owner = new TeamMember(toInsert.getId(),userId, TeamMemberType.OWNER,toInsert.getCreateTime());
        teamMemberRepository.insert(owner);

        //inserting team details

        return toInsert;
    }

    @Override
    public Team update(Team user) {
        return null;
    }

    @Override
    public Team findTeamByName(String teamName) {
        MapSqlParameterSource params = new MapSqlParameterSource("teamName",teamName);
        String sql = "SELECT * FROM team WHERE `team`.teamId = :teamName";

        return getTeam(sql,params);
    }

    @Override
    public Team findTeamById(Long teamId) {
        MapSqlParameterSource params = new MapSqlParameterSource("teamId",teamId);
        String sql = "SELECT * FROM team WHERE `team`.teamId = :teamId";

        return getTeam(sql,params);
    }

    @Override
    public boolean joinTeam(Long requestingId,Long teamId) {
        TeamMember pendingMember = new TeamMember(teamId,requestingId,TeamMemberType.PENDING,new Date());
        pendingMember = teamMemberRepository.insert(pendingMember);

        if(pendingMember != null)
            return true;

        return false;
    }

    @Override
    public List<User> getMemberListByType(Long teamId,TeamMemberType toGet) {
        List<TeamMember> pendingList = teamMemberRepository.filterByMemberType(toGet);
        List<User> toReturn = Collections.emptyList();
        pendingList.forEach(pending ->
            toReturn.add(
                    userRepository.findById(pending.getUserId())
            )
        );

        return toReturn;
    }

    @Override
    public boolean updateTeamMemberType(Long teamId, Long memberId, TeamMemberType toChangeInto) {
        TeamMember toUpdate = teamMemberRepository.findById(teamId,memberId);
        if(toUpdate != null){
            toUpdate.setTeamMemberType(toChangeInto);
            teamMemberRepository.update(toUpdate);
            return true;
        }
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
        toReturn.addValue("description",toMap.getDescription());

        return toReturn;
    }

    private Team getTeam(String sql, MapSqlParameterSource params) {
        Optional<Team> toReturn = namedParameterJdbcTemplate.query(
                sql,
                params,
                (rs, i) -> new Team(
                        rs.getLong("teamId"),
                        rs.getInt("privacy"),
                        rs.getInt("totalMember"),
                        rs.getString("teamName"),
                        rs.getString("thumbnail"),
                        rs.getBoolean("verified"),
                        rs.getBoolean("deleted"),
                        rs.getDate("createTime"),
                        rs.getString("location"),
                        rs.getString("description")
                )).stream().findFirst();

        if (toReturn.isPresent()) {
            if (toReturn.get().isDeleted())
                return null;
            return toReturn.get();
        }
        return null;
    }
}

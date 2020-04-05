package com.usrun.core.repository.impl;

import com.usrun.core.model.Team;
import com.usrun.core.model.User;
import com.usrun.core.model.junction.TeamMember;
import com.usrun.core.model.type.TeamMemberType;
import com.usrun.core.repository.TeamMemberRepository;
import com.usrun.core.repository.TeamRepository;
import com.usrun.core.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class TeamRepositoryImpl implements TeamRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(TeamRepository.class);

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
        TeamMember owner = new TeamMember(toInsert.getId(),userId, TeamMemberType.OWNER.toValue(),toInsert.getCreateTime());
        teamMemberRepository.insert(owner);

        //inserting team details

        return toInsert;
    }

    @Override
    public Team update(Team toUpdate) {

        MapSqlParameterSource map = mapTeamObject(toUpdate);

        namedParameterJdbcTemplate.update(
                "UPDATE usrun.team SET " +
                "teamName = :teamName, thumbnail=:thumbnail, privacy = :privacy, location = :location, description = :description",
                map);

        return toUpdate;
    }

    @Override
    public boolean delete(Team toDelete) {
        toDelete.setDeleted(true);

        this.update(toDelete);

        return true;
    }

    @Override
    public Team findTeamByName(String teamName) {
        MapSqlParameterSource params = new MapSqlParameterSource("teamName",teamName);
        String sql = "SELECT * FROM team WHERE `team`.teamId = :teamName";

        return getTeamSQLParamMap(sql,params);
    }

    @Override
    public Team findTeamById(Long teamId) {
        MapSqlParameterSource params = new MapSqlParameterSource("teamId",teamId);
        String sql = "SELECT * FROM team WHERE `team`.teamId = :teamId";

        return getTeamSQLParamMap(sql,params);
    }

    @Override
    public boolean joinTeam(Long requestingId,Long teamId) {
        TeamMember pendingMember = new TeamMember(teamId,requestingId,TeamMemberType.PENDING.toValue(),new Date());
        pendingMember = teamMemberRepository.insert(pendingMember);

        if(pendingMember != null)
            return true;

        return false;
    }

    @Override
    public boolean cancelJoinTeam(Long requestingId, Long teamId){
        TeamMember toDelete = teamMemberRepository.findById(teamId,requestingId);

        return teamMemberRepository.delete(toDelete);
    }

    @Override
    public int changeTotalMember(Long teamId, int changeAmount) {
        Team toChange = this.findTeamById(teamId);
        int newTotalMember = toChange.getTotalMember() + changeAmount;
        if(newTotalMember < 2) return -1;

        toChange.setTotalMember(toChange.getTotalMember() + changeAmount);
        MapSqlParameterSource map = mapTeamObject(toChange);

        this.update(toChange);

        return newTotalMember;
    }

    @Override
    public List<User> getMemberListByType(Long teamId,TeamMemberType toGet) {
        List<TeamMember> pendingList = teamMemberRepository.filterByMemberType(teamId, toGet);
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

    @Override
    public Set<Long> getTeamsByUser(long userId) {
        MapSqlParameterSource params = new MapSqlParameterSource("userId", userId);
        String sql = "SELECT teamId FROM usrun.team WHERE usrun.teamMember.userId = :userId";
        List<Long> teams = namedParameterJdbcTemplate.query(
                sql,
                params,
                (rs, i) -> new Long(rs.getLong("teamId"))
        );
        return teams.stream().collect(Collectors.toSet());
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

    private Team getTeamSQLParamMap(String sql, MapSqlParameterSource params) {
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
        LOGGER.warn("Can't find team with {}",params);
        return null;
    }
}

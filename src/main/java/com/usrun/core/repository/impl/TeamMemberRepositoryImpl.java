package com.usrun.core.repository.impl;

import com.usrun.core.model.junction.TeamMember;
import com.usrun.core.model.type.TeamMemberType;
import com.usrun.core.repository.TeamMemberRepository;
import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.SUNDAY;
import java.time.LocalDate;
import java.time.ZoneId;
import static java.time.temporal.TemporalAdjusters.nextOrSame;
import static java.time.temporal.TemporalAdjusters.previousOrSame;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class TeamMemberRepositoryImpl implements TeamMemberRepository {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public TeamMember insert(TeamMember toInsert) {

        MapSqlParameterSource map = mapTeamMember(toInsert);
        namedParameterJdbcTemplate.update(
                "INSERT INTO teamMember(teamId, userId, teamMemberType, addTime)"
                + " VALUES(:teamId,:userId,:teamMemberType,:addTime)",
                map
        );
        return toInsert;
    }

    @Override
    public TeamMember update(TeamMember toUpdate) {
        MapSqlParameterSource map = mapTeamMember(toUpdate);
        String sql = "UPDATE teamMember SET teamMemberType= :teamMemberType "
                + "WHERE teamId = :teamId AND userId= :userId";
        int effect = namedParameterJdbcTemplate.update(sql, map);
        return effect == 0 ? null : toUpdate;
    }

    @Override
    public boolean delete(TeamMember toDelete) {
        int status = 0;
        MapSqlParameterSource map = mapTeamMember(toDelete);
        status = namedParameterJdbcTemplate.update(
                "DELETE FROM teamMember "
                + "WHERE teamId = :teamId AND userId= :userId",
                map
        );

        return status != 0;
    }

    @Override
    public TeamMember findById(Long teamId, Long userId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("teamId", teamId);
        params.addValue("userId", userId);
        String sql = "SELECT * FROM teamMember WHERE teamId = :teamId AND userId = :userId";

        return getTeamMember(sql, params);
    }

    @Override
    public List<TeamMember> filterByMemberType(long teamId, TeamMemberType toFilter) {
        MapSqlParameterSource params = new MapSqlParameterSource("teamMemberType", toFilter);
        params.addValue("teamId", teamId);
        String sql = "SELECT * FROM teamMember WHERE teamId = :teamId AND teamMemberType = :teamMemberType";

        List<TeamMember> toReturn = namedParameterJdbcTemplate.query(
                sql,
                params,
                (rs, i) -> new TeamMember(
                        rs.getLong("teamId"),
                        rs.getLong("userId"),
                        rs.getInt("teamMemberType"),
                        rs.getDate("addTime")));
        return toReturn;
    }

    @Override
    public List<TeamMember> getAllMemberOfTeam(long teamId) {
        MapSqlParameterSource params = new MapSqlParameterSource("teamId", teamId);

        String sql = "SELECT * FROM teamMember WHERE teamId = :teamId";

        List<TeamMember> toReturn = namedParameterJdbcTemplate.query(
                sql,
                params,
                (rs, i) -> new TeamMember(
                        rs.getLong("teamId"),
                        rs.getLong("userId"),
                        rs.getInt("teamMemberType"),
                        rs.getDate("addTime")));
        return toReturn;
    }

    private TeamMember getTeamMember(String sql, MapSqlParameterSource params) {
        Optional<TeamMember> toReturn = namedParameterJdbcTemplate.query(
                sql,
                params,
                (rs, i) -> new TeamMember(
                        rs.getLong("teamId"),
                        rs.getLong("userId"),
                        rs.getInt("teamMemberType"),
                        rs.getDate("addTime"))).stream().findFirst();

        if (toReturn.isPresent()) {
            return toReturn.get();
        }
        return null;
    }

    private MapSqlParameterSource mapTeamMember(TeamMember toMap) {

        MapSqlParameterSource toReturn = new MapSqlParameterSource();

        toReturn.addValue("teamId", toMap.getTeamId());
        toReturn.addValue("userId", toMap.getUserId());
        toReturn.addValue("teamMemberType", toMap.getTeamMemberType().toValue());
        toReturn.addValue("addTime", toMap.getAddTime());

        return toReturn;
    }

    @Override
    public List<TeamMember> getMemberAvailable(long teamId) {
        MapSqlParameterSource params = new MapSqlParameterSource("teamId", teamId);

        String sql = "SELECT * FROM teamMember WHERE teamId = :teamId AND teamMemberType <= 2";

        List<TeamMember> toReturn = namedParameterJdbcTemplate.query(
                sql,
                params,
                (rs, i) -> new TeamMember(
                        rs.getLong("teamId"),
                        rs.getLong("userId"),
                        rs.getInt("teamMemberType"),
                        rs.getDate("addTime")));
        return toReturn;
    }

    @Override
    public List<Long> getAllIdMemberOfTeam(long teamId) {
        MapSqlParameterSource params = new MapSqlParameterSource("teamId", teamId);

        String sql = "SELECT * FROM teamMember WHERE teamId = :teamId";

        List<Long> toReturn = namedParameterJdbcTemplate.query(
                sql,
                params,
                (rs, i) -> rs.getLong("userId"));
        return toReturn;
    }

    @Override
    public List<TeamMember> getNewMemberInWeek(long teamId) {
        LocalDate today = LocalDate.now();

        LocalDate monday = today.with(previousOrSame(MONDAY));
        Date mondayDate = Date.from(monday.atStartOfDay(ZoneId.systemDefault()).toInstant());
        MapSqlParameterSource params = new MapSqlParameterSource("teamId", teamId);
        params.addValue("monday", mondayDate);

        String sql = "SELECT * FROM teamMember WHERE teamId = :teamId AND addTime >= :monday";

        List<TeamMember> toReturn = namedParameterJdbcTemplate.query(
                sql,
                params,
                (rs, i) -> new TeamMember(
                        rs.getLong("teamId"),
                        rs.getLong("userId"),
                        rs.getInt("teamMemberType"),
                        rs.getDate("addTime")));
        return toReturn;
    }
}

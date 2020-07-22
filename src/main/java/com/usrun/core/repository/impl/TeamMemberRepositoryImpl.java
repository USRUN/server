package com.usrun.core.repository.impl;

import static java.time.DayOfWeek.MONDAY;
import static java.time.temporal.TemporalAdjusters.previousOrSame;

import com.usrun.core.model.junction.TeamMember;
import com.usrun.core.model.type.TeamMemberType;
import com.usrun.core.payload.dto.TeamNewMemberDTO;
import com.usrun.core.payload.dto.TeamStatDTO;
import com.usrun.core.repository.TeamMemberRepository;
import java.time.LocalDate;
import java.time.ZoneId;
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
            + " VALUES(:teamId, :userId, :teamMemberType, :addTime)",
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
    return getTeamMembers(sql, params);
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
  public List<TeamNewMemberDTO> getNewMemberInWeek() {
    LocalDate today = LocalDate.now();

    LocalDate monday = today.with(previousOrSame(MONDAY));
    Date mondayDate = Date.from(monday.atStartOfDay(ZoneId.systemDefault()).toInstant());
    MapSqlParameterSource params = new MapSqlParameterSource("monday", mondayDate);

    String sql = "SELECT teamId, count(userId) as newUser FROM teamMember WHERE addTime >= :monday group by teamId";

    List<TeamNewMemberDTO> toReturn = namedParameterJdbcTemplate.query(
        sql,
        params,
        (rs, i) -> new TeamNewMemberDTO(
            rs.getLong("teamId"),
            rs.getInt("newUser")));
    return toReturn;
  }

  @Override
  public List<TeamStatDTO> getTeamStat() {
    String sql = "select tm.teamId,t.teamName, t.thumbnail, sum(ua.totalDistance) as totalDistance, max(ua.totalTime) as maxTime, max(ua.totalDistance) as maxDistance, count(userActivityId) as numActivity, count(distinct(tm.userId)) as numberUser from teamMember tm, userActivity ua, team t where ua.userId = tm.userId and t.teamId = tm.teamId group by tm.teamId order by totalDistance DESC;";
    List<TeamStatDTO> toReturn = namedParameterJdbcTemplate.query(
        sql,
        (rs, i) -> new TeamStatDTO(rs.getLong("teamId"),
            rs.getString("teamName"),
            rs.getString("thumbnail"),
            rs.getLong("totalDistance"),
            rs.getLong("maxTime"),
            rs.getLong("maxDistance"),
            0,
            rs.getInt("numberUser"),
            rs.getLong("numActivity")));
    return toReturn;
  }

  @Override
  public List<TeamMember> getAll() {
    String sql = "SELECT * FROM teamMember";
    return getTeamMembers(sql, new MapSqlParameterSource());
  }

  @Override
  public List<TeamMember> getAllByLessEqualTeamMemberType(TeamMemberType teamMemberType) {
    String sql = "SELECT * FROM teamMember WHERE teamMemberType <= :teamMemberType";
    return getTeamMembers(sql,
        new MapSqlParameterSource("teamMemberType", teamMemberType.toValue()));
  }

  private List<TeamMember> getTeamMembers(String sql, MapSqlParameterSource params) {
    return namedParameterJdbcTemplate.query(
        sql,
        params,
        (rs, i) -> new TeamMember(
            rs.getLong("teamId"),
            rs.getLong("userId"),
            rs.getInt("teamMemberType"),
            rs.getDate("addTime")));
  }
}

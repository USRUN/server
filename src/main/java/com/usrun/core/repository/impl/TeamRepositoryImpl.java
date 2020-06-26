package com.usrun.core.repository.impl;

import com.usrun.core.model.Team;
import com.usrun.core.model.User;
import com.usrun.core.model.junction.TeamMember;
import com.usrun.core.model.type.TeamMemberType;
import com.usrun.core.payload.dto.LeaderBoardTeamDTO;
import com.usrun.core.repository.TeamMemberRepository;
import com.usrun.core.repository.TeamRepository;
import com.usrun.core.repository.UserRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Slf4j
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
        "INSERT INTO team (privacy, totalMember, teamName, verified, deleted, createTime, district, province, thumbnail, banner) "
            + "VALUES ("
            + ":privacy, :totalMember, :teamName, :verified, :deleted, :createTime, :district, :province, :thumbnail, :banner)",
        map,
        holder,
        new String[]{"GENERATED_ID"});

    toInsert.setId(holder.getKey().longValue());

    // adding team creator as owner to DB
    TeamMember owner = new TeamMember(toInsert.getId(), userId, TeamMemberType.OWNER.toValue(),
        toInsert.getCreateTime());
    teamMemberRepository.insert(owner);

    //inserting team details

    return toInsert;
  }

  @Override
  public Team update(Team toUpdate) {
    MapSqlParameterSource map = mapTeamObject(toUpdate);
    String sql = "UPDATE team SET "
        + "privacy = :privacy, teamName = :teamName, thumbnail = :thumbnail, banner = :banner, "
        + "deleted= :deleted, privacy = :privacy, district = :district, province = :province, "
        + "description = :description, totalMember = :totalMember "
        + "WHERE teamId = :teamId";
    int effect = namedParameterJdbcTemplate.update(sql, map);
    return effect == 0 ? null : toUpdate;
  }

  @Override
  public boolean delete(Team toDelete) {
    toDelete.setDeleted(true);

    Team result = this.update(toDelete);

    return result.isDeleted();
  }

  @Override
  public Team findTeamByName(String teamName) {
    MapSqlParameterSource params = new MapSqlParameterSource("teamName", teamName);
    String sql = "SELECT * FROM team WHERE `team`.teamId = :teamName";

    return getTeamSQLParamMap(sql, params);
  }

  @Override
  public Team findTeamById(Long teamId) {
    MapSqlParameterSource params = new MapSqlParameterSource("teamId", teamId);
    String sql = "SELECT * FROM team WHERE `team`.teamId = :teamId";

    return getTeamSQLParamMap(sql, params);
  }

  @Override
  public boolean joinTeam(Long requestingId, Long teamId) {
    TeamMember pendingMember = new TeamMember(teamId, requestingId,
        TeamMemberType.PENDING.toValue(), new Date());
    pendingMember = teamMemberRepository.insert(pendingMember);

    return (pendingMember != null);
  }

  @Override
  public boolean cancelJoinTeam(Long requestingId, Long teamId) {
    TeamMember toDelete = teamMemberRepository.findById(teamId, requestingId);
    if (toDelete == null) {
      throw new DataRetrievalFailureException("Can't find teamMember");
    }
    return teamMemberRepository.delete(toDelete);
  }

  @Override
  public int changeTotalMember(Long teamId, int changeAmount) {
    Team toChange = this.findTeamById(teamId);
    int newTotalMember = toChange.getTotalMember() + changeAmount;
    if (newTotalMember < 1) {
      log.error("Member count can not be less than 1, teamId: {}, changeAmount: {}", teamId,
          changeAmount);
      return -1;
    }

    toChange.setTotalMember(newTotalMember);
    this.update(toChange);

    return newTotalMember;
  }

  @Override
  public List<User> getMemberListByType(Long teamId, TeamMemberType toGet) {
    List<TeamMember> pendingList = teamMemberRepository.filterByMemberType(teamId, toGet);
    List<User> toReturn = new ArrayList<>();
    pendingList.forEach(pending ->
        toReturn.add(
            userRepository.findById(pending.getUserId())
        )
    );

    return toReturn;
  }

  @Override
  public boolean updateTeamMemberType(Long teamId, Long memberId, TeamMemberType toChangeInto) {
    TeamMember toUpdate = teamMemberRepository.findById(teamId, memberId);
    if (toUpdate != null) {
      if (toUpdate.getTeamMemberType() == toChangeInto) {
        return false;
      } else {
        toUpdate.setTeamMemberType(toChangeInto);
        TeamMember updated = teamMemberRepository.update(toUpdate);
        return updated != null;
      }
    } else {
      return false;
    }
  }

  /*
      location: location to search for teams
      howMany: how many will be returned
      toExclude: teamIds to exclude from the returned set (user is already a member)
   */

  @Override
  public Set<Team> getTeamSuggestionByUserLocation(String district, String province, int howMany,
      Set<Long> toExclude) {
    if (toExclude == null || toExclude.isEmpty()) {
      toExclude = Collections.singleton(0L);
    }
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("district", district);
    params.addValue("province", province);
    params.addValue("howMany", howMany);
    params.addValue("toExclude", toExclude);
    String sql = "SELECT * FROM team WHERE team.teamId NOT IN (:toExclude) "
        + "AND (:district is null OR district = :district) "
        + "AND (:province is null OR province = :province) "
        + "LIMIT :howMany";
    return getMultipleTeamSQLParamMap(sql, params);
  }

  @Override
  public List<Team> findAllTeam() {
    MapSqlParameterSource params = new MapSqlParameterSource();
    String sql = "SELECT * FROM team";
    return getTeamsSQLParamMap(sql, params);
  }

  @Override
  public List<Team> getTeamsByUserReturnTeam(long userId) {
    MapSqlParameterSource params = new MapSqlParameterSource("userId", userId);
    String sql = "SELECT * FROM team t, teamMember tm "
        + "WHERE tm.userId = :userId AND tm.teamId = t.teamId";
    return getTeamsSQLParamMap(sql, params);
  }

  @Override
  public List<LeaderBoardTeamDTO> getLeaderBoard(long teamId) {
    MapSqlParameterSource params = new MapSqlParameterSource("teamId", teamId);
    String sql = "SELECT ua.userId, SUM(ua.totalDistance) as total "
        + "FROM userActivity ua, teamMember tm "
        + "WHERE tm.teamId = :teamId AND tm.userId = ua.userId "
        + "GROUP BY ua.userId "
        + "ORDER BY total DESC";
    return namedParameterJdbcTemplate.query(sql, params,
        (rs, i) -> new LeaderBoardTeamDTO(rs.getLong("userId"), rs.getLong("total")));
  }

  @Override
  public Set<Long> getTeamsByUser(long userId) {
    MapSqlParameterSource params = new MapSqlParameterSource("userId", userId);
    String sql = "SELECT teamId FROM teamMember WHERE teamMember.userId = :userId";
    List<Long> teams = namedParameterJdbcTemplate.query(
        sql,
        params,
        (rs, i) -> rs.getLong("teamId")
    );
    return new HashSet<>(teams);
  }

  @Override
  public Set<Team> findTeamWithNameContains(String searchString, int offset, int count) {
    Set<Team> toReturn = null;

    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("teamName", "%" + searchString + "%");
    params.addValue("offset", offset * count);
    params.addValue("count", count);

    String sql = "SELECT * " +
        "FROM team " +
        "WHERE teamName LIKE :teamName " +
        "LIMIT :count OFFSET :offset";

    toReturn = getMultipleTeamSQLParamMap(sql, params);
    return toReturn;
  }

  private MapSqlParameterSource mapTeamObject(Team toMap) {
    MapSqlParameterSource toReturn = new MapSqlParameterSource();

    toReturn.addValue("teamId", toMap.getId());
    toReturn.addValue("teamName", toMap.getTeamName());
    toReturn.addValue("privacy", toMap.getPrivacy());
    toReturn.addValue("thumbnail", toMap.getThumbnail());
    toReturn.addValue("banner", toMap.getBanner());
    toReturn.addValue("district", toMap.getDistrict());
    toReturn.addValue("province", toMap.getProvince());
    toReturn.addValue("totalMember", toMap.getTotalMember());
    toReturn.addValue("createTime", toMap.getCreateTime());
    toReturn.addValue("deleted", toMap.isDeleted());
    toReturn.addValue("verified", toMap.isVerified());
    toReturn.addValue("description", toMap.getDescription());

    return toReturn;
  }

  private Set<Team> getMultipleTeamSQLParamMap(String sql, MapSqlParameterSource params) {
    return namedParameterJdbcTemplate.query(sql, params, rs -> {
          Set<Team> set = new HashSet<Team>();
          while (rs.next()) {
            Team team = new Team(
                rs.getLong("teamId"),
                rs.getInt("privacy"),
                rs.getInt("totalMember"),
                rs.getString("teamName"),
                rs.getString("thumbnail"),
                rs.getString("banner"),
                rs.getBoolean("verified"),
                rs.getBoolean("deleted"),
                rs.getDate("createTime"),
                rs.getString("district"),
                rs.getString("province"),
                rs.getString("description")
            );

            set.add(team);
          }
          return set;
        }
    );
  }

  private List<Team> getTeamsSQLParamMap(String sql, MapSqlParameterSource params) {
    return namedParameterJdbcTemplate.query(
        sql,
        params,
        (rs, i) -> new Team(
            rs.getLong("teamId"),
            rs.getInt("privacy"),
            rs.getInt("totalMember"),
            rs.getString("teamName"),
            rs.getString("thumbnail"),
            rs.getString("banner"),
            rs.getBoolean("verified"),
            rs.getBoolean("deleted"),
            rs.getDate("createTime"),
            rs.getString("province"),
            rs.getString("district"),
            rs.getString("description")
        ));
  }

  private Team getTeamSQLParamMap(String sql, MapSqlParameterSource params) {
    Optional<Team> toReturn = getTeamsSQLParamMap(sql, params).stream().findFirst();

    if (toReturn.isPresent()) {
      if (toReturn.get().isDeleted()) {
        return null;
      }
      return toReturn.get();
    }
    log.error("Can't find team with {}", params);
    return null;
  }
}

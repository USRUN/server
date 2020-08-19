/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.usrun.core.repository.impl;

import com.usrun.core.model.EventParticipant;
import com.usrun.core.payload.dto.EventTeamStatDTO;
import com.usrun.core.payload.dto.EventUserStatDTO;
import com.usrun.core.payload.dto.TeamEventDTO;
import com.usrun.core.payload.dto.UserEventDTO;
import com.usrun.core.repository.EventParticipantRepository;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * @author anhhuy
 */
@Repository
public class EventParticipantRepositoryImpl implements EventParticipantRepository {

  private static final Logger logger = LoggerFactory
      .getLogger(EventParticipantRepositoryImpl.class);
  @Autowired
  private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  private MapSqlParameterSource mapEvent(EventParticipant eventParticipant) {
    MapSqlParameterSource map = new MapSqlParameterSource();
    map.addValue("eventId", eventParticipant.getEventId());
    map.addValue("teamId", eventParticipant.getTeamId());
    map.addValue("userId", eventParticipant.getUserId());
    map.addValue("distance", eventParticipant.getDistance());
    return map;
  }

  @Override
  public List<UserEventDTO> getUserParticipant(long eventId, int offset, int count) {
    MapSqlParameterSource map = new MapSqlParameterSource("eventId", eventId);
    map.addValue("offset",offset);
    map.addValue("count",count);

    try{
      List<UserEventDTO> users = namedParameterJdbcTemplate.query("SELECT user.userId,displayName,province,avatar  FROM user LEFT JOIN eventParticipant" +
              " ON user.userId = eventParticipant.userId" +
              " WHERE eventId = :eventId " +
                      "LIMIT :offset, :count ",map,
              (rs,i) -> new UserEventDTO(
                      rs.getLong("userId"),
                      rs.getString("displayName"),
                      rs.getInt("province"),
                      rs.getString("avatar")
              ));
      return users;
    } catch (Exception ex){
      logger.error(ex.getMessage(),ex);
      return null;
    }
  }

  @Override
  public List<TeamEventDTO> getTeamParticipant(long eventId, int offset, int count) {
    MapSqlParameterSource map = new MapSqlParameterSource("eventId", eventId);
    map.addValue("offset",offset);
    map.addValue("count",count);

    try{
      List<TeamEventDTO> teams = namedParameterJdbcTemplate.query("SELECT team.teamId,teamName,province,thumbnail,totalMember  FROM team LEFT JOIN eventParticipant" +
                      " ON team.teamId = eventParticipant.teamId" +
                      " WHERE eventId = :eventId " +
                      "LIMIT :offset, :count ",map,
              (rs,i) -> new TeamEventDTO(
                      rs.getLong("teamId"),
                      rs.getString("teamName"),
                      rs.getString("thumbnail"),
                      rs.getInt("totalMember"),
                      rs.getInt("province")
              ));
      return teams;
    } catch (Exception ex){
      logger.error(ex.getMessage(),ex);
      return null;
    }
  }

  @Override
  public int insert(EventParticipant eventParticipant) {
    MapSqlParameterSource map = mapEvent(eventParticipant);
    try {
      int putError = namedParameterJdbcTemplate.update(
          "INSERT INTO eventParticipant(eventId,userId,teamId,distance)"
              + " VALUES(:eventId, :userId, :teamId, :distance)",
          map
      );
      return putError;
    } catch (Exception ex) {
      logger.error(ex.getMessage(), ex);
      return -1;
    }
  }

  @Override
  public boolean delete(EventParticipant eventParticipant) {
    int status = 0;
    MapSqlParameterSource map = mapEvent(eventParticipant);
    status = namedParameterJdbcTemplate.update(
        "DELETE FROM eventParticipant "
            + "WHERE  eventId= :eventId AND teamId= :teamId AND userId= :userId",
        map
    );
    return status != 0;
  }

  @Override
  public List<EventParticipant> findByUserId(long userId) {
    MapSqlParameterSource params = new MapSqlParameterSource("userId", userId);
    String sql = "SELECT * FROM eventParticipant WHERE userId = :userId";
    List<EventParticipant> eventParticipants = findEventParticipant(sql, params);
    if (eventParticipants.size() > 0) {
      return eventParticipants;
    } else {
      return null;
    }
  }

  private List<EventParticipant> findEventParticipant(String sql, MapSqlParameterSource params) {
    List<EventParticipant> listEventParticipant = namedParameterJdbcTemplate.query(sql,
        params,
        (rs, i) -> new EventParticipant(rs.getLong("eventId"),
            rs.getLong("userId"),
            rs.getLong("teamId"),
            rs.getLong("distance")
        ));
    if (listEventParticipant.size() > 0) {
      return listEventParticipant;
    } else {
      return Collections.emptyList();
    }
  }

  @Override
  public EventParticipant findEventParticipant(long eventId, long userId) {
    MapSqlParameterSource params = new MapSqlParameterSource("userId", userId);
    params.addValue("eventId", eventId);
    String sql = "SELECT * FROM eventParticipant WHERE userId = :userId AND eventId = :eventId";
    List<EventParticipant> eventParticipants = findEventParticipant(sql, params);
    if (eventParticipants.size() > 0) {
      return eventParticipants.get(0);
    } else {
      return null;
    }
  }

  @Override
  public int updateEventParticipant(EventParticipant eventParticipant) {
    MapSqlParameterSource map = mapEvent(eventParticipant);
    String sql = "UPDATE eventParticipant SET distance= :distance "
        + "WHERE teamId = :teamId AND userId= :userId AND eventId = :eventId";
    int effect = namedParameterJdbcTemplate.update(sql, map);
    return effect;
  }

  @Override
  public boolean updateDistance(long userId, long eventId, long distance) {
    int effect = 0;
    MapSqlParameterSource map = new MapSqlParameterSource();
    map.addValue("eventId", eventId);
    map.addValue("userId", userId);
    map.addValue("distance", distance);
    String sql = "UPDATE eventParticipant SET distance=  distance + :distance "
        + "WHERE userId= :userId AND eventId = :eventId";
    effect = namedParameterJdbcTemplate.update(sql, map);
    return effect != 0;
  }

  @Override
  public List<EventTeamStatDTO> getTeamStat(long eventId, int top) {
    MapSqlParameterSource params = new MapSqlParameterSource("eventId", eventId);
    params.addValue("top", top);
    String sql = "SELECT teamId, SUM(distance) as totalDistance FROM eventParticipant "
        + "WHERE eventId = :eventId "
        + "GROUP BY teamId "
        + "ORDER BY totalDistance DESC "
        + "LIMIT :top";
    return namedParameterJdbcTemplate.query(sql, params,
        (rs, i) -> new EventTeamStatDTO(rs.getLong("teamId"), rs.getLong("totalDistance")));
  }

  @Override
  public List<EventUserStatDTO> getUserStat(long eventId, int top) {
    MapSqlParameterSource params = new MapSqlParameterSource("eventId", eventId);
    params.addValue("top", top);
    String sql = "SELECT userId, distance FROM eventParticipant "
        + "WHERE eventId = :eventId "
        + "ORDER BY distance DESC "
        + "LIMIT :top";
    return namedParameterJdbcTemplate.query(sql, params,
        (rs, i) -> new EventUserStatDTO(rs.getLong("userId"), rs.getLong("distance")));
  }
}

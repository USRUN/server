/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.usrun.core.repository.impl;

import com.usrun.core.model.EventParticipant;
import com.usrun.core.repository.EventParticipantRepostitory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

/**
 * @author anhhuy
 */
public class EventParticipantRepositoryImpl implements EventParticipantRepostitory {

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
  public EventParticipant insert(EventParticipant eventParticipant) {
    MapSqlParameterSource map = mapEvent(eventParticipant);
    try {
      namedParameterJdbcTemplate.update(
          "INSERT INTO event(eventId,teamId,userId,distance)"
              + " VALUES(:eventId, :teamId, :userId, :distance)",
          map
      );
      return eventParticipant;
    } catch (Exception ex) {
      return null;
    }
  }

  @Override
  public boolean delete(EventParticipant eventParticipant) {
    int status = 0;
    MapSqlParameterSource map = mapEvent(eventParticipant);
    status = namedParameterJdbcTemplate.update(
        "DELETE FROM teamMember"
            + "WHERE  eventId= :eventId AND teamId= :teamId AND userId= :userId",
        map
    );
    return status != 0;
  }
}

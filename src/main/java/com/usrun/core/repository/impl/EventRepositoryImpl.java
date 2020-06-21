/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.usrun.core.repository.impl;

import com.usrun.core.model.Event;
import com.usrun.core.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * @author anhhuy
 */
@Repository
public class EventRepositoryImpl implements EventRepository {

  @Autowired
  private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  private MapSqlParameterSource mapEvent(Event event) {
    MapSqlParameterSource map = new MapSqlParameterSource();
    map.addValue("eventId", event.getEventId());
    map.addValue("createTime", event.getCreateTime());
    map.addValue("endTime", event.getEndTime());
    map.addValue("eventName", event.getEventName());
    map.addValue("sponsor", event.getSponsor());
    map.addValue("startTime", event.getStartTime());
    map.addValue("status", event.getStatus());
    map.addValue("subtitle", event.getSubtitle());
    map.addValue("thumbnail", event.getThumbnail());
    map.addValue("totalParticipant", event.getTotalParticipant());
    return map;
  }

  @Override
  public Event insert(Event event) {
    MapSqlParameterSource map = mapEvent(event);
    try {
      namedParameterJdbcTemplate.update(
          "INSERT INTO event(eventId,createTime,endTime,eventName,sponsor,startTime,status,subtitle,thumbnail,totalParticipant)"
              + " VALUES(:eventId, :createTime, :endTime, :eventName, :sponsor, :startTime, :status, :subtitle, :thumbnail, :totalParticipant)",
          map
      );
      return event;
    } catch (Exception ex) {
      return null;
    }
  }

  @Override
  public boolean delete(Event event) {
    int status = 0;
    MapSqlParameterSource map = mapEvent(event);
    status = namedParameterJdbcTemplate.update(
        "DELETE FROM teamMember"
            + "WHERE  eventId= :eventId",
        map
    );
    return status != 0;
  }
}

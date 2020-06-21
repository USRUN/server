/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.usrun.core.repository.impl;

import com.usrun.core.model.Event;
import com.usrun.core.model.Organization;
import com.usrun.core.repository.EventRepository;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 *
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
        map.addValue("startTime", event.getStartTime());
        map.addValue("status", event.getStatus());
        map.addValue("subtitle", event.getSubtitle());
        map.addValue("thumbnail", event.getThumbnail());
        map.addValue("totalParticipant", event.getTotalParticipant());
        return map;
    }

    @Override
    public int insert(Event event) {
        MapSqlParameterSource map = mapEvent(event);
        try {
            int putError = namedParameterJdbcTemplate.update(
                    "INSERT INTO event(createTime,endTime,eventName,startTime,status,subtitle,thumbnail,totalParticipant)"
                    + " VALUES(:createTime, :endTime, :eventName, :startTime, :status, :subtitle, :thumbnail, :totalParticipant)",
                    map
            );
            return putError;
        } catch (Exception ex) {
            return -1;
        }
    }

    @Override
    public boolean delete(Event event) {
        int status = 0;
        MapSqlParameterSource map = mapEvent(event);
        status = namedParameterJdbcTemplate.update(
                "DELETE FROM event"
                + "WHERE  eventId= :eventId",
                map
        );
        return status != 0;
    }

    @Override
    public Event findById(long id) {
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        String sql = "SELECT * FROM event WHERE eventId = :id";
        List<Event> events = findEvent(sql, params);
        if (events.size() > 0) {
            return events.get(0);
        } else {
            return null;
        }
    }

    @Override
    public List<Event> findByName(String name) {
        MapSqlParameterSource params = new MapSqlParameterSource("name", name);
        String sql = "SELECT * FROM event WHERE eventName = :name";
        List<Event> events = findEvent(sql, params);
        if (events.size() > 0) {
            return events;
        } else {
            return null;
        }
    }

    private List<Event> findEvent(String sql, MapSqlParameterSource params) {
        List<Event> listEvent = namedParameterJdbcTemplate.query(sql,
                params,
                (rs, i) -> new Event(rs.getLong("eventId"),
                        rs.getInt("status"),
                        rs.getDate("createTime"),
                        rs.getString("eventName"),
                        rs.getString("subtitle"),
                        rs.getString("thumbnail"),
                        rs.getInt("totalParticipant"),
                        rs.getDate("startTime"),
                        rs.getDate("endTime"),
                        rs.getInt("status")
                ));
        if (listEvent.size() > 0) {
            return listEvent;
        } else {
            return Collections.emptyList();
        }
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.usrun.core.repository;

import com.usrun.core.model.Event;
import com.usrun.core.payload.event.EventWithCheckJoin;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * @author anhhuy
 */
@Repository
public interface EventRepository {

    int insert(Event event);

    Event findById(long id);

    List<Event> findByName(String name);

    boolean delete(Event delete);

    List<Event> mFindById(List<Long> ids);

    List<EventWithCheckJoin> getAllEvent(long userId, int offset, int limit);

    List<EventWithCheckJoin> getUserEventWithCheckJoin(long curUserId, long userId);

    List<Event> getMyEvent(long userId, int offset, int limit);

    List<Event> getMyEventNotJoin(long userId, int offset, int limit);

    List<EventWithCheckJoin> searchEvent(long userId, String name, int offset, int limit);

    boolean leaveEvent(long userId, long eventId);

    boolean inscreaseEventParticipant(long eventId);

    boolean descreaseEventParticipant(long eventId);
}

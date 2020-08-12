/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.usrun.core.repository;

import com.usrun.core.model.EventParticipant;
import com.usrun.core.payload.dto.EventTeamStatDTO;
import java.util.List;

/**
 * @author anhhuy
 */
public interface EventParticipantRepository {

  int insert(EventParticipant eventParticipant);
  
  EventParticipant findEventParticipant(long eventId, long userId);
  
  int updateEventParticipant(EventParticipant eventParticipant);

  List<EventParticipant> findByUserId(long userId);

  boolean delete(EventParticipant eventParticipant);
  
  boolean updateDistance(long userId, long eventId, long distance);

  List<EventTeamStatDTO> getTeamStat(long eventId, int top);
}

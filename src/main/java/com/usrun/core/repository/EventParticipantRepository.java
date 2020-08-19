/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.usrun.core.repository;

import com.usrun.core.model.EventParticipant;
import com.usrun.core.model.Team;
import com.usrun.core.model.User;
import com.usrun.core.payload.dto.EventTeamStatDTO;
import com.usrun.core.payload.dto.EventUserStatDTO;
import com.usrun.core.payload.dto.TeamEventDTO;
import com.usrun.core.payload.dto.UserEventDTO;

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

  List<EventUserStatDTO> getUserStat(long eventId, int top);

  List<UserEventDTO> getUserParticipant(long eventId, int offset, int count);

  List<TeamEventDTO> getTeamParticipant(long eventId, int offset, int count);
}

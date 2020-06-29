/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.usrun.core.service;

import com.usrun.core.config.ErrorCode;
import com.usrun.core.exception.CodeException;
import com.usrun.core.model.Event;
import com.usrun.core.model.EventParticipant;
import com.usrun.core.repository.EventParticipantRepository;
import com.usrun.core.repository.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author huyna
 */
@Component
public class EventService {

  private static final Logger logger = LoggerFactory.getLogger(EventService.class);

  @Autowired
  private EventParticipantRepository eventParticipant;

  @Autowired
  private EventRepository eventRepository;

  public void addActivityForEvent(long userId, long eventId, long distance) {
    try {
      if (userId < 0 || eventId < 0 || distance < 0) {
        throw new CodeException(ErrorCode.INVALID_PARAM);
      }
      //@TODO: check status event;
      Event event = eventRepository.findById(eventId);
      if (event == null) {
        throw new CodeException(ErrorCode.EVENT_NOT_FOUND);
      }
      long startTime = event.getStartTime().getTime();
      long endTime = event.getEndTime().getTime();

      if (System.currentTimeMillis() < startTime || System.currentTimeMillis() > endTime) {
        throw new CodeException(ErrorCode.NOT_TIME_EVENT);
      }

      EventParticipant eventpart = eventParticipant.findEventParticipant(eventId, userId);
      if (eventpart == null) {
        throw new CodeException(ErrorCode.USER_NOT_JOIN_EVENT);
      }

      eventpart.setDistance(eventpart.getDistance() + distance);
      eventParticipant.updateEventParticipant(eventpart);
    } catch (CodeException ex) {
      logger.info("add activity to event: " + userId + " | " + eventId + " | " + ex.getErrorCode());
    } catch (Exception ex) {
      logger.error("", ex);
    }
  }

}

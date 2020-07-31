/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.usrun.core.controller;

import com.usrun.core.config.ErrorCode;
import com.usrun.core.exception.CodeException;
import com.usrun.core.model.Event;
import com.usrun.core.model.EventParticipant;
import com.usrun.core.model.User;
import com.usrun.core.payload.CodeResponse;
import com.usrun.core.payload.event.EventReq;
import com.usrun.core.payload.event.JoinEventReq;
import com.usrun.core.repository.EventParticipantRepository;
import com.usrun.core.repository.EventRepository;
import com.usrun.core.security.CurrentUser;
import com.usrun.core.security.UserPrincipal;
import com.usrun.core.service.UserService;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author huyna3
 */
@RestController
@RequestMapping("/event")
public class EventController {

  private static final Logger logger = LoggerFactory.getLogger(EventController.class);
  @Autowired
  private EventRepository eventRepository;

  @Autowired
  private EventParticipantRepository eventParticipantRepository;

  @Autowired
  private UserService userService;

  @PostMapping("/createEvent")
  public ResponseEntity<?> createEvent(
      @RequestBody EventReq eventReq
  ) {
    try {
      Event event = new Event(
          (short) 1,
          new Date(System.currentTimeMillis()),
          eventReq.getEventName(),
          eventReq.getSubtitle(),
          eventReq.getThumbnail(),
          0,
          eventReq.getStartTime(),
          eventReq.getEndTime(),
          eventReq.getDelete()
      );
      int putError = eventRepository.insert(event);
      if (putError >= 0) {
        return ResponseEntity.ok(new CodeResponse("put success"));
      }
      return ResponseEntity.ok(new CodeResponse(""));
    } catch (CodeException ex) {
      return ResponseEntity.ok(new CodeResponse(ex.getErrorCode()));
    } catch (Exception ex) {
      logger.error("", ex);
      return ResponseEntity.ok(new CodeResponse(ErrorCode.SYSTEM_ERROR));
    }
  }

  @PostMapping("/joinEvent")
  public ResponseEntity<?> joinEvent(
      @CurrentUser UserPrincipal userPrincipal,
      @RequestBody JoinEventReq joinEventReq
  ) {
    try {
      long userId = userPrincipal.getId();
      long teamId = joinEventReq.getTeamId();
      long eventId = joinEventReq.getEventId();

      Event event = eventRepository.findById(eventId);
      if (event == null) {
        return ResponseEntity.ok(new CodeResponse(ErrorCode.EVENT_NOT_EXISTED));
      }

      User user = userService.loadUser(userId);
      if (!user.getTeams().contains(teamId)) {
        return ResponseEntity.ok(new CodeResponse(ErrorCode.TEAM_USER_NOT_FOUND));
      }

      EventParticipant eventParticipant = new EventParticipant(eventId, userId, teamId, 0);
      int put = eventParticipantRepository.insert(eventParticipant);
      if (put >= 0) {
        return ResponseEntity.ok(new CodeResponse(ErrorCode.SUCCESS));
      }
      return ResponseEntity.ok(new CodeResponse(""));
    } catch (CodeException ex) {
      return ResponseEntity.ok(new CodeResponse(ex.getErrorCode()));
    } catch (Exception ex) {
      logger.error("", ex);
      return ResponseEntity.ok(new CodeResponse(ErrorCode.SYSTEM_ERROR));
    }

  }

  @PostMapping("/getEventOfUser")
  public ResponseEntity<?> getEventOfUser(
      @CurrentUser UserPrincipal userPrincipal
  ) {
    try {
      long userId = userPrincipal.getId();

      List<EventParticipant> listEventPart = eventParticipantRepository.findByUserId(userId);
      logger.info("listEventPart : " + listEventPart.size());
      List<Long> ids = new ArrayList<>();
      listEventPart.stream().forEach(item -> ids.add(item.getEventId()));
      logger.info("list Ids: " + ids);
      List<Event> listEvent = eventRepository.mFindById(ids);
      return ResponseEntity.ok(new CodeResponse(listEvent));
    } catch (CodeException ex) {
      return ResponseEntity.ok(new CodeResponse(ex.getErrorCode()));
    } catch (Exception ex) {
      logger.error("", ex);
      return ResponseEntity.ok(new CodeResponse(ErrorCode.SYSTEM_ERROR));
    }
  }

}

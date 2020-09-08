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
import com.usrun.core.payload.event.EventIdReq;
import com.usrun.core.payload.event.EventInfoResp;
import com.usrun.core.payload.event.EventLeaderBoardRequest;
import com.usrun.core.payload.event.EventListResponse;
import com.usrun.core.payload.event.EventOrganization;
import com.usrun.core.payload.event.EventParticipantRequest;
import com.usrun.core.payload.event.EventReq;
import com.usrun.core.payload.event.EventWithCheckJoin;
import com.usrun.core.payload.event.JoinEventReq;
import com.usrun.core.payload.event.LimitOffsetReq;
import com.usrun.core.payload.event.SearchEventReq;
import com.usrun.core.payload.event.UserEventReq;
import com.usrun.core.repository.EventParticipantRepository;
import com.usrun.core.repository.EventRepository;
import com.usrun.core.repository.SponsorRepository;
import com.usrun.core.repository.TeamRepository;
import com.usrun.core.security.CurrentUser;
import com.usrun.core.security.UserPrincipal;
import com.usrun.core.service.EventService;
import com.usrun.core.service.UserService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
  private SponsorRepository sponsor;

  @Autowired
  private EventParticipantRepository eventParticipantRepository;

  @Autowired
  private UserService userService;

  @Autowired
  private EventService eventService;
  @Autowired
  private TeamRepository teamMember;

  @PostMapping("/createEvent")
  public ResponseEntity<?> createEvent(
      @RequestBody EventReq eventReq
  ) {
    int putError = eventService.createEvent(eventReq);
    if (putError > 0) {
      return new ResponseEntity<>(new CodeResponse("put success"), HttpStatus.OK);
    }
    return new ResponseEntity<>(new CodeResponse(ErrorCode.ADD_EVENT_FAIL), HttpStatus.OK);
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
        return new ResponseEntity<>(new CodeResponse(ErrorCode.EVENT_NOT_EXISTED), HttpStatus.OK);
      }

      User user = userService.loadUser(userId);
      if (!user.getTeams().contains(teamId)) {
        return ResponseEntity.status(400).body(new CodeResponse(ErrorCode.TEAM_USER_NOT_FOUND));
      }

      EventParticipant eventParticipant = new EventParticipant(eventId, userId, teamId, 0);
      int put = eventParticipantRepository.insert(eventParticipant);
      boolean inscreaseParticipantResult = eventRepository.inscreaseEventParticipant(eventId);
      if(!inscreaseParticipantResult){
          logger.error("cannot inscrease participant in event: "+eventId );
      }
      if (put >= 0) {
        return new ResponseEntity<>(new CodeResponse(ErrorCode.SUCCESS), HttpStatus.OK);
      }
      return new ResponseEntity<>(new CodeResponse(""), HttpStatus.BAD_REQUEST);
    } catch (Exception ex) {
      logger.error(ex.getMessage(), ex);
      return new ResponseEntity<>(new CodeResponse(""), HttpStatus.BAD_REQUEST);
    }

  }

  @PostMapping("/leaveEvent")
  public ResponseEntity<?> leaveEvent(
      @CurrentUser UserPrincipal userPrincipal,
      @RequestBody EventIdReq eventIdReq
  ) {
    try {
      long userId = userPrincipal.getId();
      long eventId = eventIdReq.getEventId();

      Event event = eventRepository.findById(eventId);
      if (event == null) {
        return new ResponseEntity<>(new CodeResponse(ErrorCode.EVENT_NOT_EXISTED), HttpStatus.OK);
      }
      boolean put = eventRepository.leaveEvent(userId, eventId);
      if (put) {
        return new ResponseEntity<>(new CodeResponse(ErrorCode.SUCCESS), HttpStatus.OK);
      }
      return new ResponseEntity<>(new CodeResponse(""), HttpStatus.BAD_REQUEST);
    } catch (Exception ex) {
      logger.error(ex.getMessage(), ex);
      return new ResponseEntity<>(new CodeResponse(""), HttpStatus.BAD_REQUEST);
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
      return new ResponseEntity<>(new CodeResponse(listEvent), HttpStatus.OK);
    } catch (Exception ex) {
      logger.error(ex.getMessage(), ex);
      return new ResponseEntity<>(new CodeResponse(""), HttpStatus.BAD_REQUEST);
    }
  }

  @PostMapping("/getAllEvent")
  public ResponseEntity<?> getAllEvent(
      @CurrentUser UserPrincipal userPrincipal,
      @RequestBody LimitOffsetReq limitOffsetReq
  ) {
    try {
      long userId = userPrincipal.getId();

      List<EventWithCheckJoin> listEventPart = eventRepository
          .getAllEvent(userId, limitOffsetReq.offset, limitOffsetReq.limit);
      return new ResponseEntity<>(new CodeResponse(listEventPart), HttpStatus.OK);
    } catch (Exception ex) {
      logger.error(ex.getMessage(), ex);
      return new ResponseEntity<>(new CodeResponse(ErrorCode.GET_EVENT_FAIL), HttpStatus.OK);
    }
  }

  @PostMapping("/getUserEventWithCheckJoin")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<?> getUserEventWithCheckJoin(
      @CurrentUser UserPrincipal userPrincipal,
      @RequestBody UserEventReq userEventReq
  ) {
    try {
      long curUserId = userPrincipal.getId();
      long userId = userEventReq.getUserId();

      List<EventWithCheckJoin> listEventPart = eventRepository
          .getUserEventWithCheckJoin(curUserId, userId);
      List<EventListResponse> resp = listEventPart.stream().map(item -> new EventListResponse(item,
          eventParticipantRepository.getTotalTeamOfEvent(item.getEventId())))
          .collect(Collectors.toList());
      return new ResponseEntity<>(new CodeResponse(resp), HttpStatus.OK);
    } catch (Exception ex) {
      logger.error(ex.getMessage(), ex);
      return new ResponseEntity<>(new CodeResponse(ErrorCode.GET_EVENT_FAIL), HttpStatus.OK);
    }
  }

  @PostMapping("/getMyEvent")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<?> getMyEvent(
      @CurrentUser UserPrincipal userPrincipal,
      @RequestBody LimitOffsetReq limitOffsetReq
  ) {
    try {
      long userId = userPrincipal.getId();

      List<Event> listEventPart = eventRepository
          .getMyEvent(userId, limitOffsetReq.offset, limitOffsetReq.limit);
      List<EventListResponse> resp = listEventPart.stream().map(item -> new EventListResponse(item,
          eventParticipantRepository.getTotalTeamOfEvent(item.getEventId()), true))
          .collect(Collectors.toList());
      return new ResponseEntity<>(new CodeResponse(resp), HttpStatus.OK);
    } catch (Exception ex) {
      logger.error(ex.getMessage(), ex);
      return new ResponseEntity<>(new CodeResponse(ErrorCode.GET_EVENT_FAIL), HttpStatus.OK);
    }
  }

  @PostMapping("/getEventNotJoin")
  public ResponseEntity<?> getEventNotJoin(
      @CurrentUser UserPrincipal userPrincipal,
      @RequestBody LimitOffsetReq limitOffsetReq
  ) {
    try {
      long userId = userPrincipal.getId();

      List<Event> listEventPart = eventRepository
          .getMyEventNotJoin(userId, limitOffsetReq.offset, limitOffsetReq.limit);
      List<EventListResponse> resp = listEventPart.stream().map(item -> new EventListResponse(item,
          eventParticipantRepository.getTotalTeamOfEvent(item.getEventId()), false))
          .collect(Collectors.toList());
      return new ResponseEntity<>(new CodeResponse(resp), HttpStatus.OK);
    } catch (Exception ex) {
      logger.error(ex.getMessage(), ex);
      return new ResponseEntity<>(new CodeResponse(ErrorCode.GET_EVENT_FAIL), HttpStatus.OK);
    }
  }

  @PostMapping("/searchEvent")
  public ResponseEntity<?> searchEvent(
      @CurrentUser UserPrincipal userPrincipal,
      @RequestBody SearchEventReq searchReq
  ) {
    try {
      long userId = userPrincipal.getId();

      List<EventWithCheckJoin> listEventPart = eventRepository
          .searchEvent(userId, '%' + searchReq.getName() + '%', searchReq.offset, searchReq.limit);

      List<EventListResponse> resp = listEventPart.stream().map(item -> new EventListResponse(item,
          eventParticipantRepository.getTotalTeamOfEvent(item.getEventId())))
          .collect(Collectors.toList());
      return new ResponseEntity<>(new CodeResponse(resp), HttpStatus.OK);
    } catch (Exception ex) {
      logger.error(ex.getMessage(), ex);
      return new ResponseEntity<>(new CodeResponse(ErrorCode.GET_EVENT_FAIL), HttpStatus.OK);
    }
  }

  @PostMapping("/getTeamLeaderBoard")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<?> getTeamLeaderBoard(
      @CurrentUser UserPrincipal userPrincipal,
      @RequestBody EventLeaderBoardRequest request
  ) {
    try {
      long eventId = request.getEventId();
      int top = Math.max(request.getTop(), 10);
      EventParticipant eventParticipant = eventParticipantRepository
          .findEventParticipant(eventId, userPrincipal.getId());
      long teamId = eventParticipant != null ? eventParticipant.getTeamId() : -1;
      return ResponseEntity
          .ok(new CodeResponse(eventService.getEventTeamLeaderBoard(eventId, top, teamId)));
    } catch (CodeException ex) {
      return ResponseEntity.ok(new CodeResponse(ex.getErrorCode()));
    } catch (Exception ex) {
      logger.error("", ex);
      return ResponseEntity.ok(new CodeResponse(ErrorCode.SYSTEM_ERROR));
    }
  }

  @PostMapping("/getUserLeaderBoard")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<?> getUserLeaderBoard(
      @CurrentUser UserPrincipal userPrincipal,
      @RequestBody EventLeaderBoardRequest request
  ) {
    try {
      long eventId = request.getEventId();
      int top = Math.max(request.getTop(), 10);
      return ResponseEntity.ok(new CodeResponse(
          eventService.getEventUserLeaderBoard(eventId, userPrincipal.getId(), top)));
    } catch (CodeException ex) {
      return ResponseEntity.ok(new CodeResponse(ex.getErrorCode()));
    } catch (Exception ex) {
      logger.error("", ex);
      return ResponseEntity.ok(new CodeResponse(ErrorCode.SYSTEM_ERROR));
    }
  }

  @PostMapping("/getEventAthletes")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<?> getEventAthletes(
      @RequestBody EventParticipantRequest eventParticipantRequest
  ) {
    try {
      long eventId = eventParticipantRequest.getEventId();
      int offset = Math.max(eventParticipantRequest.getOffset(), 0);
      int count = eventParticipantRequest.getCount() <= 0 ? 10 : eventParticipantRequest.getCount();
      String name = eventParticipantRequest.getName();
      return ResponseEntity
          .ok(new CodeResponse(eventService.getUserEvent(eventId, offset, count, name)));
    } catch (CodeException ex) {
      return ResponseEntity.ok(new CodeResponse(ex.getErrorCode()));
    } catch (Exception ex) {
      logger.error("", ex);
      return ResponseEntity.ok(new CodeResponse(ErrorCode.SYSTEM_ERROR));
    }
  }

  @PostMapping("/getEventTeams")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<?> getEventTeams(
      @RequestBody EventParticipantRequest eventParticipantRequest
  ) {
    try {
      long eventId = eventParticipantRequest.getEventId();
      int offset = Math.max(eventParticipantRequest.getOffset(), 0);
      int count = eventParticipantRequest.getCount() <= 0 ? 10 : eventParticipantRequest.getCount();
      return ResponseEntity.ok(eventService.getTeamEvent(eventId, offset, count));
    } catch (CodeException ex) {
      return ResponseEntity.ok(new CodeResponse(ex.getErrorCode()));
    } catch (Exception ex) {
      logger.error("", ex);
      return ResponseEntity.ok(new CodeResponse(ErrorCode.SYSTEM_ERROR));
    }
  }

  @PostMapping("/getEventInfo")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<?> getEventInfo(
      @CurrentUser UserPrincipal userPrincipal,
      @RequestBody EventIdReq eventIdReq
  ) {
    try {
      long eventId = eventIdReq.getEventId();
      Event event = eventRepository.findById(eventId);
      int numberTeam = eventParticipantRepository.getTotalTeamOfEvent(eventId);
      long totalDistance = eventParticipantRepository.getTotalDistanceOfEvent(eventId);
      List<List<EventOrganization>> dataOrganization = new ArrayList<>();
      for (int i = 0; i < 5; i++) {
        List<EventOrganization> listOrganizationWithRole = sponsor
            .getEventOrganizationWithRole(eventId, i);
        dataOrganization.add(listOrganizationWithRole);
      }
      EventParticipant eventParticipant = eventParticipantRepository
          .findEventParticipant(eventId, userPrincipal.getId());
      long teamId = eventParticipant != null ? eventParticipant.getTeamId() : -1;
      EventInfoResp resp = new EventInfoResp(event, numberTeam, totalDistance, dataOrganization,
          teamId);
      return ResponseEntity.ok(new CodeResponse(resp));
    } catch (CodeException ex) {
      return ResponseEntity.ok(new CodeResponse(ex.getErrorCode()));
    } catch (Exception ex) {
      logger.error("", ex);
      return ResponseEntity.ok(new CodeResponse(ErrorCode.SYSTEM_ERROR));
    }
  }
}

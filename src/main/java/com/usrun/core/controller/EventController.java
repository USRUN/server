/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.usrun.core.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.usrun.core.config.ErrorCode;
import com.usrun.core.model.Event;
import com.usrun.core.model.EventParticipant;
import com.usrun.core.payload.CodeResponse;
import com.usrun.core.payload.event.EventReq;
import com.usrun.core.payload.event.JoinEventReq;
import com.usrun.core.repository.EventParticipantRepostitory;
import com.usrun.core.repository.EventRepository;
import com.usrun.core.security.CurrentUser;
import com.usrun.core.security.UserPrincipal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author huyna3
 */
@RestController
@RequestMapping("/event")
public class EventController {

    private static final Logger logger = LoggerFactory.getLogger(EventController.class);
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventParticipantRepostitory eventParticipantRespository;

    @PostMapping("/createEvent")
    public ResponseEntity<?> createEvent(
            @RequestBody EventReq eventReq
    ) {
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
            return new ResponseEntity<>(new CodeResponse("put success"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new CodeResponse(""), HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/joinEvent")
    public ResponseEntity<?> joinEvent(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestBody JoinEventReq joinEventReq
    ) {

        long userId = userPrincipal.getId();
        long teamId = joinEventReq.getTeamId();
        long eventId = joinEventReq.getEventId();

        Event event = eventRepository.findById(eventId);
        if (event == null) {
            return new ResponseEntity<>(new CodeResponse(ErrorCode.EVENT_NOT_EXISTED), HttpStatus.OK);
        }
        //TODO:// check team

        EventParticipant eventParticipant = new EventParticipant(eventId, userId, teamId, 0);
        int put = eventParticipantRespository.insert(eventParticipant);
        System.out.println("put" + put);
        if (put >= 0) {
            return new ResponseEntity<>(new CodeResponse(ErrorCode.SUCCESS), HttpStatus.OK);
        }
        return new ResponseEntity<>(new CodeResponse(""), HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/getEventOfUser")
    public ResponseEntity<?> getEventOfUser(
            @CurrentUser UserPrincipal userPrincipal
    ) {
        try {
            long userId = userPrincipal.getId();

            List<EventParticipant> listEventPart = eventParticipantRespository.findByUserId(userId);
            List<Event> listEvent = new ArrayList<>();
            JSONArray dataresp = new JSONArray();
            for (EventParticipant eventParticipant : listEventPart) {
                Event curEvent = eventRepository.findById(eventParticipant.getEventId());
                listEvent.add(curEvent);
            }
            return new ResponseEntity<>(new CodeResponse(listEvent), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return new ResponseEntity<>(new CodeResponse(""), HttpStatus.BAD_REQUEST);
        }
    }

}

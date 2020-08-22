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
import com.usrun.core.payload.dto.*;
import com.usrun.core.payload.event.EventReq;
import com.usrun.core.repository.EventParticipantRepository;
import com.usrun.core.repository.EventRepository;
import com.usrun.core.repository.SponsorRepository;
import com.usrun.core.repository.TeamRepository;
import com.usrun.core.repository.UserRepository;
import com.usrun.core.utility.SequenceGenerator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author huyna
 */
@Component
public class EventService {

    private static final Logger logger = LoggerFactory.getLogger(EventService.class);

    @Autowired
    private EventParticipantRepository eventParticipantRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private SponsorRepository sponsorRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private SequenceGenerator sequenceGenerator;

    @Autowired
    private UserRepository userRepository;

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

            EventParticipant eventpart = eventParticipantRepository
                    .findEventParticipant(eventId, userId);
            if (eventpart == null) {
                throw new CodeException(ErrorCode.USER_NOT_JOIN_EVENT);
            }

            eventpart.setDistance(eventpart.getDistance() + distance);
            eventParticipantRepository.updateEventParticipant(eventpart);
        } catch (CodeException ex) {
            logger.info("add activity to event: " + userId + " | " + eventId + " | " + ex.getErrorCode());
        } catch (Exception ex) {
            logger.error("", ex);
        }
    }

    @Transactional
    public int createEvent(EventReq eventReq) {
        try {
            long eventId = sequenceGenerator.nextId();

            Event event = new Event(
                    eventId,
                    (short) 1,
                    new Date(System.currentTimeMillis()),
                    eventReq.getEventName(),
                    eventReq.getSubtitle(),
                    eventReq.getThumbnail(),
                    eventReq.getPoster(),
                    0,
                    eventReq.getStartTime(),
                    eventReq.getEndTime(),
                    eventReq.getDelete(),
                    eventReq.getBanner(),
                    eventReq.getPoweredBy(),
                    eventReq.getReward(),
                    eventReq.getDescription()
            );

            int[] listResultInsertSponsor = sponsorRepository
                    .addOrganizers(eventId, eventReq.getOrganizers());
            boolean addSponsorResult = Arrays.stream(listResultInsertSponsor)
                    .boxed()
                    .anyMatch(item -> item == 0);
            if (addSponsorResult) {
                logger.error("add sponsor for event fail");
                return 0;
            }

            int putError = eventRepository.insert(event);
            return putError;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return 0;
        }
    }

    public boolean updateDistance(long userId, long eventId, long distance) {
        try {
            return eventParticipantRepository.updateDistance(userId, eventId, distance);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return false;
        }
    }

    public List<EventTeamStatDTO> getEventTeamLeaderBoard(long eventId, int top, long teamId) {
        //get leader board
        List<EventTeamStatDTO> eventTeamStats = eventParticipantRepository.getTeamStat(eventId);
        top = Math.min(top, eventTeamStats.size());
        List<EventTeamStatDTO> topTeamStats = eventTeamStats.subList(0, top);

        int[] position = {-1};
        eventTeamStats.stream().peek(x -> position[0]++) // increment every element encounter
                .filter(item -> item.getItemId() == teamId)
                .findFirst();
        for (int i = 0; i < topTeamStats.size(); i++) {
            topTeamStats.get(i).setRank(i + 1);
        }
        EventTeamStatDTO myteam = eventTeamStats.get(position[0]);
        if (position[0] < eventTeamStats.size() && position[0] >= top && myteam.getItemId() == teamId) {
            myteam.setRank(position[0] + 1);
            topTeamStats.add(myteam);
        }

        List<Long> teamIds = eventTeamStats.stream()
                .map(eventTeamStat -> eventTeamStat.getItemId())
                .collect(Collectors.toList());
        //get Team info in leader board
        Map<Long, ShortTeamDTO> teamMap = teamRepository.getShortTeams(teamIds)
                .stream().collect(Collectors.toMap(ShortTeamDTO::getTeamId, Function.identity()));

        List<EventTeamStatDTO> result = new ArrayList<>();
        for (int i = 0; i < topTeamStats.size(); i++) {
            EventTeamStatDTO e = topTeamStats.get(i);
            ShortTeamDTO team = teamMap.get(e.getItemId());
            EventTeamStatDTO item;
            if (team == null) {
                item = null;
            } else {
                item = new EventTeamStatDTO(e.getItemId(), e.getDistance(), team.getTeamName(),
                        team.getThumbnail(), e.getRank());
            }
            result.add(item);
        }
        return result;
    }

    public List<EventUserStatDTO> getEventUserLeaderBoard(long eventId, long userId, int top) {
        //get leader board
        List<EventUserStatDTO> eventUserStats = eventParticipantRepository.getUserStat(eventId);
        top = Math.min(top, eventUserStats.size());
        List<EventUserStatDTO> topUserStats = eventUserStats.subList(0, top);
        int[] position = {-1};
        eventUserStats.stream().peek(x -> position[0]++) // increment every element encounter
                .filter(item -> item.getItemId() == userId)
                .findFirst();
        for (int i = 0; i < topUserStats.size(); i++) {
            topUserStats.get(i).setRank(i + 1);
        }
        EventUserStatDTO userStat = eventUserStats.get(position[0]);
        if (position[0] < eventUserStats.size() && position[0] >= top && userStat.getItemId() ==userId ) {
            userStat.setRank(position[0] + 1);
            topUserStats.add(userStat);
        }
        List<Long> userIds = topUserStats.stream()
                .map(e -> e.getItemId())
                .collect(Collectors.toList());
        //get User info in leader board
        Map<Long, ShortUserDTO> userMap = userRepository.findAll(userIds)
                .stream().collect(Collectors.toMap(ShortUserDTO::getUserId, Function.identity()));
        List<EventUserStatDTO> result = new ArrayList<>();
        for (int i = 0; i < topUserStats.size(); i++) {
            EventUserStatDTO e = topUserStats.get(i);
            ShortUserDTO user = userMap.get(e.getItemId());
            EventUserStatDTO item;
            if (user == null) {
                item = null;
            } else {
                item = new EventUserStatDTO(e.getItemId(), e.getDistance(), user.getDisplayName(),
                        user.getAvatar(), e.getRank());
            }
            result.add(item);
        }
        return result;
    }

    public List<TeamEventDTO> getTeamEvent(long eventId, int offset, int count) {
        return eventParticipantRepository.getTeamParticipant(eventId, offset, count);
    }

    public List<UserEventDTO> getUserEvent(long eventId, int offset, int count, String name) {
        return eventParticipantRepository.getUserParticipant(eventId, offset, count, name);
    }
}

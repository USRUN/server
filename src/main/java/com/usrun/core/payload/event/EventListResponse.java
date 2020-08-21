/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.usrun.core.payload.event;

import com.usrun.core.model.Event;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author huyna3
 *
 */
@Getter
@Setter

public class EventListResponse {

    private Long eventId;
    private int status;
    private String eventName;
    private String subtitle;
    private String thumbnail;
    private int totalTeamParticipant;
    private int totalParticipant;
    private Date startTime;
    private Date endTime;
    private String banner;
    private String poweredBy;
    private boolean joined;

    public EventListResponse(Long eventId, int status, String eventName, String subtitle, String thumbnail, int totalTeamParticipant, int totalParticipant, Date startTime, Date endTime, String banner, String poweredBy, boolean joined) {
        this.eventId = eventId;
        this.status = status;
        this.eventName = eventName;
        this.subtitle = subtitle;
        this.thumbnail = thumbnail;
        this.totalTeamParticipant = totalTeamParticipant;
        this.totalParticipant = totalParticipant;
        this.startTime = startTime;
        this.endTime = endTime;
        this.banner = banner;
        this.poweredBy = poweredBy;
        this.joined = joined;
    }

    public EventListResponse(Event event,int totalTeam, boolean isJoin) {
        this.eventId = event.getEventId();
        this.status = EventListResponse.getStatus(event);
        this.eventName = event.getEventName();
        this.subtitle = event.getSubtitle();
        this.thumbnail = event.getThumbnail();
        this.totalTeamParticipant = totalTeam;
        this.totalParticipant = event.getTotalParticipant();
        this.startTime = event.getStartTime();
        this.endTime = event.getEndTime();
        this.banner = event.getBanner();
        this.poweredBy = event.getPoweredBy();
        this.joined = isJoin;
    }
    
    public EventListResponse(EventWithCheckJoin event,int totalTeam) {
        this.eventId = event.getEventId();
        this.status = EventListResponse.getStatus(event);
        this.eventName = event.getEventName();
        this.subtitle = event.getSubtitle();
        this.thumbnail = event.getThumbnail();
        this.totalTeamParticipant = totalTeam;
        this.totalParticipant = event.getTotalParticipant();
        this.startTime = event.getStartTime();
        this.endTime = event.getEndTime();
        this.banner = event.getBanner();
        this.poweredBy = event.getPoweredBy();
        this.joined = event.isJoin();
    }

    public static int getStatus(Event event) {
        Date current = new Date();
        if (current.getTime() < event.getStartTime().getTime()) {
            return 0;
        } else if (current.getTime() >= event.getStartTime().getTime() && current.getTime() < event.getEndTime().getTime()) {
            return 1;
        } else {
            return 2;
        }
    }
    
        public static int getStatus(EventWithCheckJoin event) {
        Date current = new Date();
        if (current.getTime() < event.getStartTime().getTime()) {
            return 0;
        } else if (current.getTime() >= event.getStartTime().getTime() && current.getTime() < event.getEndTime().getTime()) {
            return 1;
        } else {
            return 2;
        }
    }

}

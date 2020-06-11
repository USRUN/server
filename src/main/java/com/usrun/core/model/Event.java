package com.usrun.core.model;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
public class Event {

    public Event(Long eventId, short status, Date createTime, String eventName, String subtitle, String thumbnail, String sponsor, int totalParticipant, long startTime, long endTime, boolean deleted) {
        this.eventId = eventId;
        this.status = status;
        this.createTime = createTime;
        this.eventName = eventName;
        this.subtitle = subtitle;
        this.thumbnail = thumbnail;
        this.sponsor = sponsor;
        this.totalParticipant = totalParticipant;
        this.startTime = startTime;
        this.endTime = endTime;
        this.deleted = deleted;
    }
    @Id
    private Long eventId;
    private short status;
    private Date createTime;
    private String eventName;
    private String subtitle;
    private String thumbnail;
    private String  sponsor;
    private int totalParticipant;
    private long startTime;
    private long endTime;
    private boolean deleted;
}

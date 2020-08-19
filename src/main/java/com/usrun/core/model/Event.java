package com.usrun.core.model;

import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
public class Event {

  public Event(int status, Date createTime, String eventName, String subtitle, String thumbnail,
      int totalParticipant, Date startTime, Date endTime, int deleted) {
    this.status = status;
    this.createTime = createTime;
    this.eventName = eventName;
    this.subtitle = subtitle;
    this.thumbnail = thumbnail;
    this.totalParticipant = totalParticipant;
    this.startTime = startTime;
    this.endTime = endTime;
    this.deleted = deleted;
  }

  @Id
  private Long eventId;
  private int status;
  private Date createTime;
  private String eventName;
  private String subtitle;
  private String thumbnail;
  private String poster;
  private long totalDistance;
  private int totalTeamParticipant;
  private int totalParticipant;
  private Date startTime;
  private Date endTime;
  private int deleted;
  private String banner;
  private String poweredBy;
  private List<String> eventDetail;
  private String reward;

    public Event(Long eventId, int status, Date createTime, String eventName, String subtitle, String thumbnail, String poster, long totalDistance, int totalTeamParticipant, int totalParticipant, Date startTime, Date endTime, int deleted, String banner, String poweredBy, List<String> eventDetail, String reward) {
        this.eventId = eventId;
        this.status = status;
        this.createTime = createTime;
        this.eventName = eventName;
        this.subtitle = subtitle;
        this.thumbnail = thumbnail;
        this.poster = poster;
        this.totalDistance = totalDistance;
        this.totalTeamParticipant = totalTeamParticipant;
        this.totalParticipant = totalParticipant;
        this.startTime = startTime;
        this.endTime = endTime;
        this.deleted = deleted;
        this.banner = banner;
        this.poweredBy = poweredBy;
        this.eventDetail = eventDetail;
        this.reward = reward;
    }
}
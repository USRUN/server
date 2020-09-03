/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.usrun.core.payload.event;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

/**
 * @author huyna3
 */
@Getter
@Setter
public class EventWithCheckJoin {

  public EventWithCheckJoin(int status, Date createTime, String eventName, String subtitle,
      String thumbnail,
      int totalParticipant, Date startTime, Date endTime, int deleted, boolean isJoin) {
    this.status = status;
    this.createTime = createTime;
    this.eventName = eventName;
    this.subtitle = subtitle;
    this.thumbnail = thumbnail;
    this.totalParticipant = totalParticipant;
    this.startTime = startTime;
    this.endTime = endTime;
    this.deleted = deleted;
    this.isJoin = isJoin;
  }

  @Id
  private Long eventId;
  private int status;
  private Date createTime;
  private String eventName;
  private String subtitle;
  private boolean isJoin;
  private String thumbnail;
  private String poster;
  private long totalDistance;
  private int totalTeamParticipant;
  private int totalParticipant;
  private Date startTime;
  private Date endTime;
  private String banner;
  private String poweredBy;
  private int deleted;

  public EventWithCheckJoin(Long eventId, int status, Date createTime, String eventName,
      String subtitle, String thumbnail,
      int totalParticipant, Date startTime, Date endTime, int deleted, boolean isJoin,
      String banner, String poweredBy) {
    this.eventId = eventId;
    this.status = status;
    this.createTime = createTime;
    this.eventName = eventName;
    this.subtitle = subtitle;
    this.thumbnail = thumbnail;
    this.totalParticipant = totalParticipant;
    this.startTime = startTime;
    this.endTime = endTime;
    this.deleted = deleted;
    this.isJoin = isJoin;
    this.banner = banner;
    this.poweredBy = poweredBy;
  }
}

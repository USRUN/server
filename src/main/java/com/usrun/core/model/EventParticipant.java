/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.usrun.core.model;

/**
 * @author huyna3
 */
public class EventParticipant {

  private long eventId;
  private long userId;
  private long teamId;
  private long distance;

  public EventParticipant(long eventId, long userId, long teamId, long distance) {
    this.eventId = eventId;
    this.userId = userId;
    this.teamId = teamId;
    this.distance = distance;
  }

  public long getEventId() {
    return eventId;
  }

  public void setEventId(long eventId) {
    this.eventId = eventId;
  }

  public long getUserId() {
    return userId;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }

  public long getTeamId() {
    return teamId;
  }

  public void setTeamId(long teamId) {
    this.teamId = teamId;
  }

  public long getDistance() {
    return distance;
  }

  public void setDistance(long distance) {
    this.distance = distance;
  }
}

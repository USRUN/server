/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.usrun.core.model;

import com.usrun.core.model.type.SponsorType;
import lombok.Getter;
import lombok.Setter;

/**
 * @author huyna3
 */
@Getter
@Setter
public class Sponsor {

  private long eventId;
  private long organizationId;
  private SponsorType role;

  public Sponsor(long eventId, long organizationId, SponsorType role) {
    this.eventId = eventId;
    this.organizationId = organizationId;
    this.role = role;
  }
}

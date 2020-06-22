/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.usrun.core.model;

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

  public Sponsor(long eventId, long organizationId) {
    this.eventId = eventId;
    this.organizationId = organizationId;
  }


}

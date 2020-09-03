package com.usrun.core.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author phuctt4
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventTeamStatDTO {

  private long itemId;
  private long distance;
  private String name;
  private String avatar;
  private int rank;

  public EventTeamStatDTO(long teamId, long distance) {
    this.itemId = teamId;
    this.distance = distance;
  }
}

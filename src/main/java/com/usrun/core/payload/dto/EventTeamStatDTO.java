package com.usrun.core.payload.dto;

import lombok.*;

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

  public EventTeamStatDTO(long teamId, long distance) {
    this.itemId = teamId;
    this.distance = distance;
  }
}

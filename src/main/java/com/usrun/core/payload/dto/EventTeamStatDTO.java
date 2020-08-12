package com.usrun.core.payload.dto;

import lombok.*;

/**
 * @author phuctt4
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventTeamStatDTO {
  private long teamId;
  private long distance;
  private String teamName;
  private String thumbnail;

  public EventTeamStatDTO(long teamId, long distance) {
    this.teamId = teamId;
    this.distance = distance;
  }
}

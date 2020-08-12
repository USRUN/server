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
public class EventUserStatDTO {
  private long userId;
  private long distance;
  private String displayName;
  private String avatar;

  public EventUserStatDTO(long userId, long distance) {
    this.userId = userId;
    this.distance = distance;
  }
}

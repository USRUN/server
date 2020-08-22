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
  private long itemId;
  private long distance;
  private String name;
  private String avatar;
  private int rank;

  public EventUserStatDTO(long userId, long distance) {
    this.itemId = userId;
    this.distance = distance;
  }
}

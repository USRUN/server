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
public class UserActivityStatDTO {

  private long userId;
  private long totalDistance;
  private long totalTime;
  private long maxTime;
  private long maxDistance;
  private long totalUserAcitivity;
}

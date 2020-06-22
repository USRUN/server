package com.usrun.core.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author phuctt4
 */

@Data
@AllArgsConstructor
public class TeamActivityCountDTO {

  private long teamId;
  private long count;
}

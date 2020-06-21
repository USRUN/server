package com.usrun.core.payload.user;

import lombok.Getter;
import lombok.Setter;

/**
 * @author phuctt4
 */

@Getter
@Setter
public class GetActivitiesRequest {

  private long teamId;
  private int count = 30;
  private int offset = 0;
}

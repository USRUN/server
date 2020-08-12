package com.usrun.core.payload.event;

import lombok.Data;

/**
 * @author phuctt4
 */

@Data
public class EventLeaderBoardRequest {
  private long eventId;
  private int top;
}

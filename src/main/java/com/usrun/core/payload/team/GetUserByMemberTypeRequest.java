package com.usrun.core.payload.team;

import lombok.Data;

/**
 * @author phuctt4
 */

@Data
public class GetUserByMemberTypeRequest {
  private long teamId;
  private int memberType;
  private int page;
  private int count;
}

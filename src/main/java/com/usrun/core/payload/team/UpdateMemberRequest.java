package com.usrun.core.payload.team;

import lombok.Data;

@Data
public class UpdateMemberRequest {

  private Long teamId;
  private Long memberId;
  private int memberType;
}

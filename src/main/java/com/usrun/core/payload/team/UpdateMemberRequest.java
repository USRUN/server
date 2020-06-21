package com.usrun.core.payload.team;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class UpdateMemberRequest {

  private Long teamId;
  private Long memberId;
  private int memberType;
}

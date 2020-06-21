package com.usrun.core.payload.team;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class GetAllTeamMemberRequest {

  private long teamId;
  private int pageNum;
  private int perPage;
}

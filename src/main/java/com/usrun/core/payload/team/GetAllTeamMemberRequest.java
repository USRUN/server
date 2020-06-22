package com.usrun.core.payload.team;

import lombok.Data;

@Data
public class GetAllTeamMemberRequest {

  private long teamId;
  private int pageNum;
  private int perPage;
}

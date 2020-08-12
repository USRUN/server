package com.usrun.core.payload.team;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTeamRequest {
  // OwnerId is assumed to be the current user

  private long ownerId;
  private int privacy;
  private String teamName;
  private Integer province;
  private String thumbnail;
}
